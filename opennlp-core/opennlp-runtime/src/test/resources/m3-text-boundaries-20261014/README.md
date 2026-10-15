# M3 token code-unit reuse: draft receiver

Canonical recipe/proofs: hsoliwal/com.synexia PR #11448, source commit
67933cbe1181bdbe96a8ac6f349e151d3d3027bc. Reusable logic remains under
synexia-openrewrite-recipes/recipes/text-boundaries-20261014 and the existing
M3HashPinnedJavaSnapshotRecipe owner. No second downstream recipe engine.

## Frozen target contract

One candidate in DefaultTokenContextGenerator.createContext: reuse primitive
UTF-16 units at their original first-read boundaries. Keep public/protected
signatures, feature spelling/order/multiplicity, substring behavior, whitespace
classification, abbreviation lookup, extension hooks and exception ordering.
No interner default, model format, String representation or native runtime change.

## Execution and qualification boundary

The named source-sealed Java task generated the exact candidate. It was not
hand-patched after generation. Source-side finite proofs include 270384 extracted
method observations and 65563 JNI-probe cases; the latter are generic Java21 JNI
compatibility checks, not evidence of OpenNLP or M3 runtime integration.

Maven could not start in the execution environment (missing executable, exit 127).
OpenRewrite SDK tests, full OpenNLP reactor, both whitespace-mode suites, model
inference, M3 runtime/retention and performance remain UNEXECUTED/UNMEASURED.
Keep this PR draft: candidate transport is not SDK execution or merge qualification.
No benchmark speedup is claimed. No changes are made to default branches.

For source recipe, Maven configuration, fixtures, refusals, raw ordered logs,
source hashes and unresolved coverage, review the exact canonical source commit.
Do not copy templates by hand to bypass failed gates; repair the recipe and rerun.
