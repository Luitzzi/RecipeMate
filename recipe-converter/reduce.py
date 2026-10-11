"""Reduces a dumped recipe page to compact markdown for the model.

Structured data (JSON-LD, microdata) is removed on purpose: the model must work from the visible content.
"""

import argparse
import json
import re
import sys
from pathlib import Path
from urllib.parse import urljoin

import html2text
from bs4 import BeautifulSoup, Tag

MAX_CHARS = 60_000

DROP_TAGS = [
    "script", "style", "noscript", "svg", "iframe", "template", "head", "button", "select", "input",
    "textarea", "video", "audio", "dialog", "canvas", "object", "embed",
]  # fmt: skip
# Removed unless they hold the main heading or are marked as recipe content.
CHROME_TAGS = ["nav", "footer", "aside", "form"]
# Class/id tokens are only trusted on containers; on inline elements they often mark links inside recipe text.
CONTAINER_TAGS = {"div", "section", "aside", "article", "ul", "ol", "dl", "table", "details", "header", "figure"}
RECIPE_TOKENS = {"recipe", "ingredient", "ingredients", "instructions", "directions", "method"}

# Class/id tokens of user discussions and consent dialogs. These blocks may be most of the page, so no size limit
# applies.
BULK_TOKENS = {
    "comments", "comment", "commentlist", "respond", "disqus", "reviews", "ugc", "feedback",
    "onetrust", "cookiebot", "didomi", "usercentrics", "fides", "gdpr", "cmp", "consent", "cookie", "cookies",
    "modal", "overlay", "popup",
}  # fmt: skip
# Class/id tokens of page chrome. Only removed when the block is a small part of the page.
CHROME_TOKENS = {
    "sidebar", "related", "newsletter", "share", "sharing", "social", "advert",
    "advertisement", "ad", "ads", "promo", "footer", "nav", "navigation", "navbar", "menu",
    "breadcrumb", "breadcrumbs", "recirc", "recirculation", "subscribe", "signup", "login", "paywall", "banner",
    "toolbar", "skip", "outbrain", "taboola",
}  # fmt: skip
CHROME_MAX_SHARE = 0.25

MIN_MENU_LINKS = 10

IMAGE_ATTRS = ["data-src", "data-lazy-src", "data-original", "data-pin-media", "src"]
MIN_IMAGE_SIDE = 100


def _tokens(tag: Tag) -> set[str]:
    raw = " ".join(tag.get("class") or []) + " " + (tag.get("id") or "")
    raw = re.sub(r"([a-z])([A-Z])", r"\1 \2", raw)
    return {t for t in re.split(r"[^A-Za-z]+", raw.lower()) if t}


def _text_len(tag: Tag) -> int:
    return len(tag.get_text(" ", strip=True))


def _keep_amount_inputs(body: Tag) -> None:
    """Serving counts often sit in an input field ("Servings [4]"); its value is visible text to the reader."""
    for field in body.find_all("input"):
        value = (field.get("value") or "").strip()
        if (field.get("type") or "text").lower() in ("text", "number") and re.fullmatch(r"\d{1,4}([.,]\d)?", value):
            field.replace_with(f" {value} ")


def _strip_noise(body: Tag, protected: set[int]) -> None:
    _keep_amount_inputs(body)
    for tag in body.find_all(DROP_TAGS):
        tag.decompose()

    total = max(_text_len(body), 1)
    for tag in body.find_all(True):
        if tag.decomposed or id(tag) in protected:
            continue
        tokens = _tokens(tag) if tag.name in CONTAINER_TAGS else set()
        if tokens & BULK_TOKENS or tag.get("role") in ("dialog", "alertdialog"):
            tag.decompose()
        elif tag.name in CHROME_TAGS and not tokens & RECIPE_TOKENS:
            tag.decompose()
        elif tokens & CHROME_TOKENS and _text_len(tag) / total <= CHROME_MAX_SHARE:
            tag.decompose()
        elif tag.name in ("ul", "ol") and _is_link_list(tag):
            tag.decompose()


def _is_link_list(tag: Tag) -> bool:
    """Menus and teaser lists: nearly all text is link text. Ingredient lists with links are spared by their digits."""
    links = tag.find_all("a")
    if len(links) < MIN_MENU_LINKS:
        return False
    text_len = _text_len(tag)
    link_len = sum(len(a.get_text(" ", strip=True)) for a in links)
    if text_len == 0 or link_len / text_len < 0.9:
        return False
    with_digit = sum(1 for a in links if re.search(r"\d", a.get_text()))
    return with_digit / len(links) < 0.3


def _image_url(img: Tag, base_url: str) -> str | None:
    for attr in IMAGE_ATTRS:
        value = (img.get(attr) or "").strip()
        if value and not value.startswith("data:"):
            return urljoin(base_url, value)
    srcset = (img.get("srcset") or img.get("data-srcset") or "").strip()
    if srcset and not srcset.startswith("data:"):
        return urljoin(base_url, srcset.split(",")[-1].split()[0])
    return None


def _is_small(img: Tag) -> bool:
    for attr in ("width", "height"):
        value = str(img.get(attr) or "")
        if value.isdigit() and int(value) < MIN_IMAGE_SIDE:
            return True
    return False


def _normalise_images(body: Tag, base_url: str) -> None:
    seen: set[str] = set()
    for img in body.find_all("img"):
        url = _image_url(img, base_url)
        if url is None or url in seen or _is_small(img) or re.search(r"\.(svg|gif)(\?|$)", url):
            img.decompose()
            continue
        seen.add(url)
        alt = re.sub(r"\s+", " ", img.get("alt") or "").strip()
        img.attrs = {"src": url, "alt": alt}
    for source in body.find_all("source"):
        source.decompose()
    for picture in body.find_all("picture"):
        if picture.parent is not None:
            picture.unwrap()


def _to_markdown(body: Tag) -> str:
    converter = html2text.HTML2Text()
    converter.body_width = 0
    converter.ignore_links = True
    converter.ignore_emphasis = True
    converter.unicode_snob = True
    converter.ul_item_mark = "-"
    text = converter.handle(str(body))

    lines: list[str] = []
    for line in text.splitlines():
        line = line.rstrip()
        if re.fullmatch(r"\s*([-*]|\d+\.)?\s*", line) and line.strip():
            continue  # bullets left empty by removed controls
        if line and lines and line == lines[-1]:
            continue
        if not line and lines and not lines[-1]:
            continue
        lines.append(line)
    return "\n".join(lines).strip()


def reduce_html(html: str, url: str, title: str | None = None, lang: str | None = None, og_image: str | None = None) -> str:
    soup = BeautifulSoup(html, "lxml")
    body = soup.body or soup
    heading = body.find("h1")
    protected = {id(parent) for parent in heading.parents} if heading else set()

    _strip_noise(body, protected)
    _normalise_images(body, url)
    markdown = _to_markdown(body)
    if len(markdown) > MAX_CHARS:
        print(f"warning: {url}: markdown cut from {len(markdown)} to {MAX_CHARS} chars", file=sys.stderr)
        markdown = markdown[:MAX_CHARS]

    header = [f"URL: {url}"]
    if title:
        header.append(f"Page title: {title}")
    if lang:
        header.append(f"Page language: {lang}")
    if og_image:
        header.append(f"Main image: {og_image}")
    return "\n".join(header) + "\n\n" + markdown + "\n"


def reduce_dump(dump_dir: Path) -> str:
    meta = json.loads((dump_dir / "meta.json").read_text(encoding="utf-8"))
    html = (dump_dir / "page.html").read_text(encoding="utf-8", errors="replace")
    return reduce_html(html, meta.get("finalUrl") or meta["url"], meta.get("title"), meta.get("lang"), meta.get("ogImage"))


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("dump_dir", type=Path)
    print(reduce_dump(parser.parse_args().dump_dir))
