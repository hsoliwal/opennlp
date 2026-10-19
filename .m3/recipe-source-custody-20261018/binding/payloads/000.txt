# OpenNLP M3 text receiver

Draft candidate: source base `4713d82577dd3e9e48cd9855ccd891c2739324a4`.
Canonical recipe/test owner: [Synexia PR 11438](https://github.com/hsoliwal/com.synexia/pull/11438). Runtime postimage SHA-256: `81893c7ab931646e364390006b5a57733ec8e0e9386c6ecaa4145cee1c086a6b`.

## Implemented

The built-in feature list starts at capacity18. Fixed category spellings reuse literal backing with `new String(known)`, preserving fresh String wrapper identities; custom/null keys retain concatenation. Unicode classification, whitespace delegation, feature order/content, abbreviation behavior and public/protected hooks stay unchanged. No new text pool or model cache is introduced.

## Recipe and source custody

`source-packet/` is a **generated immutable source handoff**, not a separately maintained downstream recipe. Every source is pinned by origin commit/path/SHA-256 in source-packet.tsv and checked before compilation or execution. Canonical source stays in Synexia. The receiving source-copy exception is explicit for review; fix canonical source/templates, regenerate and rerun rather than editing these snapshots or generated runtime output. Preimage/postimage fixtures and proof receipts remain alongside the changes.

The original anonymous-download receiver failed in GitHub Actions run38593868305 because com.synexia is private. It was replaced using the canonical `public-handoff/manifest.tsv` recipe and HandoffQualification test at Synexia commit `b392d2e2ce03997f01231cb7208a887c73d77731`. No credentials or access-policy changes are needed. This supersedes earlier download/offline-checkout instructions and the initial receipt's delivery mode; production source hashes are unchanged.

```sh
mvn -B -ntp -f .m3/text-integration/pom.xml verify
# Exact admitted preimage only, exclusive checkout:
mvn -B -ntp -f .m3/text-integration/pom.xml -Dm3.mode=apply verify
# Search the frozen task catalogue:
grep -i 'prefix' .m3/text-integration/source-packet/catalogue.jsonl
```

## Evidence and remaining gates

Both original and revised receivers passed locally: **1,052,956 checks**, covering source/template drift, fixed point, compiler-extracted context methods and actual classification branches over7keys x2injected whitespace outcomes xallUTF-16units. Fresh wrapper identity is tested. Context hooks use a documented test double; this is not a full model/tokenizer suite.

Maven is absent locally; direct scripts were used. CI now requests the same gate through Maven, and its actual results must be inspected. Full product reactor/lint/JUnit/model tests, patched OpenRewrite/canonical admission, M3JDK runtime and representative performance qualification remain separate. No end-to-end speedup is asserted.

Not implemented: token-local hierarchical feature preparation, frozen-model feature IDs, shared sentence ownership, interner-policy changes, JNI model evaluation or whole-repository coverage. Donor/category decisions are recorded in the15-entry catalogue. No editorial/submission code or GPL OpenJDK source was copied.

## Current canonical recipe source custody (2026-10-18)

The reusable recipe source closure from this fork is held in `hsoliwal/com.synexia` at `215546d1ca0e724eb2c8e2e8ef6b4adce724733a`. The [dated custody binding](../SYNEXIA_RECIPE_SOURCE_CUSTODY_20261018.json) records the exact public source revisions, canonical snapshot paths, source seals and the separate execution limits. The current source-custody ledger is `synexia-openrewrite-recipes/recipe-crates/public-fork-home-20261018/SOURCE_CUSTODY.tsv` (SHA-256 `ebcfbd42a49eec806f0a4572af731828deda41f22c17250d34d49af985d78f1d`).

Existing public source and SDK/proof mirrors retain their recorded provider revisions because their build and proof callers have not yet migrated to a qualified canonical handoff. Keep improvements in the canonical recipe source and return qualified postimages to this target. Existing native builds, product APIs, runtime acceptance and promotion remain target-owned.

The retained-mirror source revision is the frozen custody origin. Separate current packet applications may update target paths under those roots; their exact inputs, outputs and qualification limits are recorded independently below.

The binding's original-packet verification covers only its listed packets. The [current recipe census](https://github.com/hsoliwal/com.synexia/blob/215546d1ca0e724eb2c8e2e8ef6b4adce724733a/synexia-openrewrite-recipes/recipe-crates/public-fork-home-20261018/evidence/CURRENT_RECIPE_CENSUS.json) (SHA-256 `cfa9edb42a99cb2e54a988b28de7078a3233fdc667c5e4d8f979675e0f767ee0`) records the separate admission, application and gate evidence for current packets. Canonical kernel checks do not establish SDK or product runtime qualification: the canonical SDK attempt was blocked before compilation with zero tests. The binding-only application does not run native product qualification; gate attempts for separate product packet applications are recorded in the current census.

The sealed binding and execution-receipt packets under `.m3/recipe-source-custody-20261018/` are pinned proof mirrors. They grant no competing recipe ownership or product promotion authority.
