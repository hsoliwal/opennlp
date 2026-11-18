<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# Notes for AI coding agents

## Priority 0 — additive superset, best-proven algorithms and full-history checkpoints (2026-11-18)

All work on this fork must preserve the **backward-compatible optimized superset** of applicable previously admitted upstream and fork capabilities, stable public API/ABI, behavior, tests and proven fixes/optimizations. Select the best *validated* candidate after comparing incumbent implementations and old/new branch and PR variants for correctness, speed, retained allocation and memory, portability, and maintainability. Add compatible APIs; never silently remove features, change contracts or discard good earlier code. Before every source promotion or merge, inspect earliest reachable ancestors plus relevant current/archived branches, tags, merged/closed/reverted PRs, merge bases and both merge parents for losses, bad commits and reintroduced regressions. Do not rebase `develop`. Record last checked date and exact commit SHA per ref, scan coverage/root, last verified anchor, findings and status; metadata-only or latest HEAD samples stay PENDING until fully examined. Canonical Synexia Maven/OpenRewrite recipes, provenance/history gates and repository-role boundaries govern source-changing work. New, separable first-party material prefers Apache-2.0; preserve all upstream/donor licenses, NOTICE and copyright. LLM-assisted atomization/patternization is not automatic independent authorship or relicensing. Canonical policy and PENDING 236-repo audit inventory: [com.synexia PR #18764](https://github.com/hsoliwal/com.synexia/pull/18764). Product-only forks must obey their existing clean postimage-only AGENTS boundaries.


Short rules for agents working on Apache OpenNLP. Human contributors follow
the same rules.

## Layout

- `opennlp-api`: the public interfaces and shared types such as `ParamChecks`.
- `opennlp-core`: `opennlp-runtime` (the components), `opennlp-ml` (the shared
  `opennlp-ml-commons` plus the maxent, perceptron, naive Bayes and libsvm
  implementations, and `opennlp-dl` and `opennlp-dl-gpu` for ONNX models),
  `opennlp-formats`, `opennlp-cli` and `opennlp-model-resolver`.
- `opennlp-tools`: the end-user toolkit that bundles the core modules.
- `opennlp-extensions`: Morfologik, spell checking and UIMA.
- `opennlp-docs`: the manual in DocBook (`src/docbkx`).
- `opennlp-eval-tests`: evaluations on reference corpora (see Tests).
- `opennlp-distr`: the binary and source distributions.

## Policy

OpenNLP follows the
[ASF generative tooling guidance](https://www.apache.org/legal/generative-tooling.html).
In short:

- AI tools may help write code. The contributor reviews every line, can
  explain it, and is responsible for it, including that the tool's terms allow
  the output to be contributed under the Apache License.
- Never include third-party code or data without a compatible license.
- Disclose AI help in the commit message. The ASF recommends naming the tool;
  OpenNLP requires this form of it: a `Co-authored-by:` trailer naming the tool
  and its version and the model and its version, for example
  `Co-authored-by: ExampleCoder 2.1.0 (example-model 4.5) <coder@example.com>`.
  GitHub only recognizes the trailer with an e-mail address.
  The human is the author and signs off the commit (`git commit -s`).
- An agent may edit, build and commit locally. It should not push, open pull
  requests, file issues, post comments or reply to reviewers on its own, only
  with the explicit approval of the person using it.
- What is posted is that person's own words. AI-generated text is quoted and
  marked as such.
- When in doubt, ask on GitHub Discussions or dev@opennlp.apache.org.

## Build

- JDK 21 and `./mvnw`. With `-pl`, add `-am`.
- Run `./mvnw verify` on the touched modules before proposing a change;
  checkstyle and forbiddenapis run there. Say which commands ran, which
  failed and which were skipped. Never report a check as passed that did
  not run.
- Do not introduce new dependencies without a discussion and approval.

## Code

- No regular expressions in production code: no `java.util.regex`,
  `String.split`, `matches`, `replaceAll`. Scan with an index or code points.
  The one exception, and the build check that enforces the rule, are described
  in [CONTRIBUTING.md](.github/CONTRIBUTING.md#regular-expressions-in-production-code).
- Unicode character sets by default, not ASCII unless a standard feature requires it.
- Use existing helpers before writing new ones. Check the JDK, then
  `ParamChecks`, `StringUtil`, `UnicodeWhitespace` and the helpers of the
  package you work in. Do not duplicate existing functionality without a clear
  technical reason.
- Case mapping and trimming go through `StringUtil`
  (`toLowerCase`, `toUpperCase`, `trimUnicodeWhitespace`), never the
  locale-sensitive `String` methods.
- Whitespace: define what counts (ASCII, Unicode, a single space) and use the
  matching helper or constant. Do not hard-code `' '` or `"\\s+"`.
- Formatting: two-space indents in Java, no tabs, no trailing whitespace,
  files end with a newline. Match the indentation of the surrounding docbkx.
- Reject bad arguments at the public API with `IllegalArgumentException` via
  `ParamChecks`; never NPE from `Objects.requireNonNull`.
- Malformed input data throws `InvalidFormatException`.
- Name repeated literals as constants. Avoid unnecessary allocations in
  performance-critical loops.
- Serializable singletons need `readResolve()`; changed Serializable classes
  need a new `serialVersionUID`.

## Javadoc and comments

- New public and protected APIs, and changes to a documented contract, get
  short, accurate Javadoc. Overrides use `{@inheritDoc}`. Write
  `@throws X Thrown if ...`. New public types get `@since`.
- Describe the contract, not the history. No "replaces the old regex",
  issue numbers, or design narrative in source; that goes in the issue.
- Avoid em dashes in new or changed Javadoc, comments and documentation. Do
  not edit unrelated text only to remove them.
- Use the terms the code and the manual already use; do not invent new ones.

## Tests

- Test first for behavior changes: the test fails before the fix.
- Many inputs go in a `@ParameterizedTest`, not a loop.
- Reuse shared fixtures instead of copying helpers into each test class.
- Assert exception type and message. Do not reimplement the code under test
  as the expected value.
- Do not add tests that repeat a case another test already covers.
- Changes to models, training or default behavior need an eval build: the
  `eval-tests` profile of `opennlp-eval-tests`, run on the ASF Jenkins job
  `OpenNLP/eval-tests-configurable`. Ask a committer to trigger it and link the
  run in the pull request. To run it locally, unpack
  [opennlp-data.zip](https://nightlies.apache.org/opennlp/opennlp-data.zip) and
  pass `-DOPENNLP_DATA_DIR=<dir>`.

## Commits

- Title: `GH-<issue>: <what changes for users>`. Body: a few plain sentences
  on the change, not the history.
- Update `opennlp-docs` when user-visible behavior changes.


## Superset merge and history-audit rule (2026-11-18)

Read [.github/MERGE_RULE.md](.github/MERGE_RULE.md) before changing an existing implementation or integrating any branch or PR.
Preserve the supported additive API/behavior superset and select implementations by correctness, predictability, measured performance, then elegance.
Audit all reachable history back to every root, all branches/tags and all PR states for lost code or regressions; preserve append-only history.
Record actual UTC coverage and remaining work in [.github/HISTORY_AUDIT.tsv](.github/HISTORY_AUDIT.tsv); policy installation is not a completed source-history audit.
Prefer verified Apache-2.0 sources and independent implementations while preserving all existing third-party rights; LLM rewriting does not remove license obligations.
Detailed private recipe/proof records remain in canonical Synexia; the compact local policy and checkpoint make the rule discoverable here.
<!-- MERGE-SUPERSET-HISTORY-20261118-2 -->

## MOP SDK cross-repository agent read gate (2026-11-18)
<!-- MOP-AGENT-2026-11-18-1 -->
Before any source analysis, refactor, PR, merge, licensing decision, or history checkpoint, also read [AGENT.md](AGENT.md). It governs bidirectional atomization/patternization, documented contract-sealed atoms, additive best-of-best API supersets, complete available-history loss checks, dated last-verified proof, Apache-2.0 eligibility and upstream rights. Existing stronger project-specific and locked-canon constraints remain in force. Installation of this pointer does not establish that a source or historical audit has run.
