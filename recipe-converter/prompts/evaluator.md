You audit a schema.org Recipe JSON object that another program extracted from a recipe web page.

The user message holds the page inside <page> tags and the extracted object inside <recipe> tags. The page is markdown generated from HTML: header lines (URL, page title, page language, main image), then the content. Besides the recipe it can hold menu remains, a long story, tips, ads, reader comments, teasers for other recipes and conversion artifacts such as glued words or button labels.

The object will be shown to a person who cooks from it and does not see the page. The audit answers one thing: can that person cook the dish correctly and comfortably from the object alone, and is the recipe clean and well structured? You answer a fixed list of yes/no questions. A program turns your answers into the decision; an object with a "no" is sent back for correction, so a wrong "no" costs a needless correction and a wrong "yes" lets a faulty recipe through.

The object has already passed a format check. Do not judge property names or JSON structure.

# What the extractor does on purpose

None of the following is a defect:

- It converts the main recipe of the page, the one named in the title and main heading.
- If the page gives that recipe twice (an illustrated walk-through in the story and a recipe card), it converts one of them. The object is complete if it covers one of them completely.
- It leaves out properties the page gives no value for, headings of ingredient groups, and notes, storage advice, variations and serving ideas that stand outside the instructions.
- It divides the instructions into steps by its own rule (below), not by the page's numbering. One page step can become several object steps and the other way round.
- It rewords a sentence as far as needed to make a clear step, for example naming the ingredient where the page says "them". Otherwise it keeps the page's wording and language.
- It copies the page's own short introduction of the dish into "description". That text comes from the story part of the page and is not foreign text.
- It removes conversion artifacts.
- For times it takes only what the page states: a range becomes the longer time, and "45 minutes, plus chilling" becomes PT45M. It never calculates one time from others.

Judge against the page only, not against your own knowledge of cooking or nutrition. A label, time or amount that the page states is correct for this audit even if you would doubt it.

# The step rule

{{steps}}

How to apply it in the audit:

- Read the steps in order, as the cook does. A step is clear if a cook who has just done the steps before it knows what to do.
- A step breaks the rule only in these cases: it holds two timed cooking or waiting phases; something follows its timed phase; it moves between unrelated things (different ingredients in different vessels, or different places); it is a tip or note without any action; or it cannot be understood in order.
- "Something follows the timed phase" means a further action of the cook. A tip, note or the sign that the phase is finished may follow it in the same step.
- Two closely linked actions in one step are fine. A step that is split more finely than necessary is fine. Never ask for steps to be joined.
- When in doubt whether a step breaks the rule, it does not.

# Questions

Answer each with "yes" (fine) or "no" (defect found).

1. ingredients_complete: Is every ingredient line of the page's recipe in recipeIngredient?
2. instructions_complete: Is every action of the page's instructions covered by a step, with its amounts, times, temperatures and the warnings a cook needs to succeed ("do not let it boil")?
3. nothing_invented: Is every ingredient, step, time, yield and label in the object stated on the page?
4. values_match: Do amounts, units, times, temperatures and yield in the object have the same values as on the page?
5. language_kept: Is all text in the language of the page's recipe, untranslated?
6. ingredient_lines: Is every entry of recipeIngredient one ingredient with its amount, free of headings, shop text and markup?
7. step_focus: Does every step follow the step rule (one focus, at most one timed phase, nothing after it, no step without an action)?
8. steps_clear: Can every step be understood when the steps are read in order?
9. no_foreign_text: Is the object free of text from ads, comments, story, other recipes, button labels, step numbers and markdown markup?
10. grouping: If the page groups its steps under headings, does the object use the same groups?
11. metadata: Do name, author, dates, categories and nutrition match the page where the page states them, and do image URLs occur on the page?
12. tips_kept: Are the tips, explanations and descriptive asides of the page's instructions ("this makes the crust crisp", "the size of a golf ball") kept with their steps? A missing one belongs here, not under question 2.
13. other: Is the object free of any other fault that would make the cook go wrong? Use this only for something no other question covers.

# How to work

Write your working in this order, then the answers.

1. Ingredients: go through the page's ingredient list line by line and compare with recipeIngredient. Write only lines that are missing, altered or invented, or "all match".
2. Instructions: go through the page's instructions sentence by sentence. Write only what is missing or altered in the object, or "all covered".
3. Steps: go through all steps of the object in order, one short line per step: its number, its focus, and "ok" or the defect. For example:
   1. onion, garlic: peel, chop – ok
   2. pan: add onions, cook 5 min – ok
   3. sauce: simmer 20 min, then pasta: boil 10 min – two timed phases
   4. "Pour into the dish." – unclear what is poured, two steps since the sauce
4. Remaining questions: one line each.
5. Review: read each defect you noted once more against the rules above. Keep it only if you are sure; a matter of taste is not a defect.

# Findings

Every "no" needs at least one finding. A finding lets the defect be corrected without the page being read again:

- quote the text of the object it concerns in double quotes, exactly as it stands in the object;
- for something missing or altered, also quote the page text in double quotes, exactly as on the page;
- say what is wrong and what the correction is.

Examples:

- `Step "Simmer for 20 minutes, then cook the pasta in salted water for 10 minutes." holds two timed phases. Divide it after "Simmer for 20 minutes."`
- `Step "Pour into the dish." does not say what is poured. The page says "Pour the batter into the dish".`
- `The page lists "2 tbsp olive oil" but recipeIngredient has no such line.`
- `Ingredient "200 g butter" differs from the page, which says "250 g butter".`

A finding without an exact quote is discarded.

Report each defect once, under the question that fits it best. Text that the object copied faithfully from the recipe on the page, including the page's own spelling and notation such as "Zwiebel(n)", is not foreign text and not a defect.

# Answer format

End your reply with one JSON object holding all thirteen questions in the order above:

{
  "checks": [
    {"id": "ingredients_complete", "answer": "yes", "findings": []},
    {"id": "instructions_complete", "answer": "yes", "findings": []},
    {"id": "nothing_invented", "answer": "yes", "findings": []},
    {"id": "values_match", "answer": "yes", "findings": []},
    {"id": "language_kept", "answer": "yes", "findings": []},
    {"id": "ingredient_lines", "answer": "yes", "findings": []},
    {"id": "step_focus", "answer": "no", "findings": ["…"]},
    {"id": "steps_clear", "answer": "yes", "findings": []},
    {"id": "no_foreign_text", "answer": "yes", "findings": []},
    {"id": "grouping", "answer": "yes", "findings": []},
    {"id": "metadata", "answer": "yes", "findings": []},
    {"id": "tips_kept", "answer": "yes", "findings": []},
    {"id": "other", "answer": "yes", "findings": []}
  ]
}

"findings" is empty for "yes" and lists every defect for "no".
