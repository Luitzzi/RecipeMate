---
name: code-interview-prep
description: Prepare the user for a technical interview about their own code. Builds a questionnaire from a user-chosen scope (whole repo, everything the user wrote/touched per git history, changes on the current branch, specific paths or commits), asks the questions one at a time, then rates each answer and produces a scored report with study pointers. Use when the user wants to be quizzed on, rehearse explaining, or prepare an interview/review about their code.
argument-hint: "[scope: repo | mine | branch | <paths/commit range>] [count: N] [level: junior|mid|senior]"
---

# Code Interview Prep

Act as a technical interviewer. Quiz the user on code in a chosen scope, then rate the answers honestly.

Arguments (optional, free text): `$ARGUMENTS`

## 1. Settle the setup

Parse `$ARGUMENTS`. For anything missing, ask with **one** `AskUserQuestion` call (max 4 questions):

| Setting | Options | Default |
|---|---|---|
| Scope | Whole repo · Everything I wrote/touched · Changes on this branch · Specific paths / commit range (via "Other") | — (must ask) |
| Question count | 8 · 12 · 20 | 12 |
| Seniority level | Junior · Mid · Senior · Staff | Senior |
| Mode | One question at a time (Recommended) · All questions at once | One at a time |

Do not ask about settings already given in the arguments.

## 2. Collect the material for the scope

Resolve the identity from `git config user.name` / `git config user.email`. Determine the base branch from `git symbolic-ref refs/remotes/origin/HEAD`, falling back to `develop`, `main`, `master` (in that order, first that exists). Check project instructions (CLAUDE.md / AGENTS.md) for a stated main branch.

**Whole repo**
- Read top-level docs (README, AGENTS.md/CLAUDE.md in root and subprojects), build files, entry points.
- Map the module structure; read the core domain, service and API layers. Skip generated code, lockfiles, vendored code.

**Everything I wrote/touched**
- `git log --author="<name>" --author="<email>" --no-merges --pretty=format:'%h %ad %s' --date=short`
- Files ranked by how much the user changed them:
  `git log --author="<name>" --author="<email>" --no-merges --numstat --pretty=format: | awk 'NF==3 {a[$3]+=$1+$2} END {for (f in a) print a[f], f}' | sort -rn`
- Drop deleted files, generated files and lockfiles. Focus on the top files plus the commits with the largest or most interesting changes (`git show <sha>`).
- If the user has many aliases (several emails in `git shortlog -sne`), ask which ones are theirs.

**Changes on this branch**
- `git log <base>..HEAD --oneline`, `git diff <base>...HEAD --stat`, then the full diff `git diff <base>...HEAD`.
- Include uncommitted work if present (`git status`, `git diff HEAD`) and say so.

**Specific paths / commit range**
- Read the given paths, or `git diff`/`git log -p` for the range.

For large scopes, delegate the sweep to an `Explore` agent and ask it for a summary of architecture, key decisions, risky spots and file references. Read the most important files yourself before writing questions.

Briefly tell the user what the scope contains (commits, files, main areas) before starting.

## 3. Build the questionnaire (keep the answer key private)

Write questions that an interviewer who has read the code would ask. Each question must be grounded in concrete code in scope. Spread them across these categories, weighted toward what the scope actually contains:

1. **Architecture & overview** – explain the component, data flow, how parts interact.
2. **Design decisions & trade-offs** – "why X over Y", what alternatives existed, what it costs.
3. **Implementation details** – how a specific function/mechanism works, non-obvious behaviour.
4. **Edge cases & failure modes** – what happens when input is invalid, a dependency fails, concurrency, transactions.
5. **Testing & quality** – how this is/should be tested, what is missing.
6. **Security, performance & scalability** – where it breaks under load, attack surfaces, N+1 queries, etc.
7. **Extension / what-if** – how to change or extend it for a new requirement.
8. **Weak spots** – code smells, bugs or debt you found; ask whether the user sees the problem and how they would fix it.

Rules:
- Mix difficulty according to the seniority level (roughly 30 % easy, 50 % medium, 20 % hard).
- Prefer open "explain/why/what-if" questions over trivia. No questions about things that are not in the code.
- Mention the file or feature the question refers to, but do not quote the code in a way that gives the answer away.
- For each question, privately prepare: the reference answer, key points a strong answer covers, and file references (`path:line`). **Never reveal these before the user answers.**

## 4. Run the interview

- **One at a time mode:** Ask question `n/N` with its category and difficulty. Wait for the answer. Give no rating yet, only a brief neutral acknowledgement, then ask the next question. One short follow-up question is allowed if an answer is vague, as a real interviewer would do.
- **All at once mode:** Present the numbered list, wait for all answers in one reply.
- The user may answer "skip" / "don't know" → score 0, continue.
- The user may say "stop" at any time → rate what was answered so far.
- Do not look things up or change code during the interview.

## 5. Rate the answers

Score every question 0–5:

| Score | Meaning |
|---|---|
| 5 | Complete, correct, explains the *why*, mentions trade-offs / edge cases unprompted |
| 4 | Correct and clear, minor gaps |
| 3 | Mostly correct, misses an important point or stays shallow |
| 2 | Partially correct, notable errors or confusion |
| 1 | Mostly wrong or very vague |
| 0 | No answer / skipped / fully wrong |

Be strict and honest; the goal is preparation, not reassurance. Judge correctness against the actual code, not against general best practice.

Output the report in this structure:

```
## Result: <total>/<max> (<percent> %) – <verdict: Not ready | Needs work | Ready | Strong>

| # | Category | Difficulty | Score |
|---|---|---|---|

### Per question
**Q<n>. <question>** – <score>/5
- Your answer: <one-line summary>
- What was good / missing: <concise>
- Reference answer: <concise, with file links>

### Strengths
### Gaps
### Study before the interview
- <file/topic> – <why, what to be able to explain>
### Likely follow-up questions
- <3–5 harder questions an interviewer may ask next>
```

Verdict thresholds: < 50 % Not ready, 50–69 % Needs work, 70–84 % Ready, ≥ 85 % Strong.

Finally offer to: (a) run another round focusing on the weakest categories, or (b) save the report as Markdown in the scratchpad directory (or a path the user names). Do not write files into the repository unless asked.
