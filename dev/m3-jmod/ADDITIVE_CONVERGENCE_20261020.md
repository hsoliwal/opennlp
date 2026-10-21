<!-- SPDX-License-Identifier: Apache-2.0 -->
<!-- Copyright 2026 Hitesh Soliwal and Contributors to the Synexia Project -->

# OpenNLP M3 JMOD / immutable-source additive convergence — 2026-10-20

## Frozen Git coordinates
- Merge base: `368337311ad94b665fd178f8d3ced005a73b2370`
- Main preimage: `ad87b6979b00b3fff41ba6b3bda31d0140778092`
- Incoming [PR #14](https://github.com/hsoliwal/opennlp/pull/14): `3c1d6a9a1067e2c4511c45a7cc3e9458caf16ba6`
- Source copyright/license: retain existing Apache/ASF notices, original file modes and blob SHAs.
- Target: additive 2-parent review branch; **no default branch promotion until combined gates pass**.

## Three-way path reconciliation
Incoming PR changed 40 paths. The 19 commits already on main also changed seven of those paths.
- 33 disjoint changed files: all exact incoming Git blobs and modes are materialized.
- Four intersections are byte-identical at both heads: `M3RegexTextBuilder.java`, `M3RegexTextBuilderTest.java`, `M3TokenizerDifferentialTest.java`, `M3TokenizerNullProbe.java`. Retain shared exact blob SHA.
- `dev/m3-cputools/pom.xml`: keep main's **newer** public-fork replay verifier rather than restore the historical cputools qualification verifier. Original PR's variant remains reachable via second parent.
- `opennlp-api/.../util/ext/Providers.java`: retain main's named-module conditional `addUses(spi)` rather than incoming unconditional call; preserves the registered SPI while avoiding unnamed-module mutation.
- `M3BpePreparedPairLookupTest.java`: preserve main's end-of-word duplicate-pair fixture; add a distinct test for the incoming PR's competing duplicate ranks on `abab`, including its exact oracle and expected `[a,ba,b]` assertion. Neither test is removed.

## Mandatory checks before promotion
1. Verify root tree SHA, both commit parents, 40 incoming path coverage and no deleted paths from main.
2. `git diff --check`, then Maven Java 21 compile/tests for OpenNLP API/runtime, model train/serialize/reload, null-probe and BPE parity.
3. JMOD/module-info closure, 17 selected modules, source lock, source-hash custody and real M3JDK21 62-JMOD image when available.
4. Compare each generated postimage against recipe in Synexia `#12314` and source mode; do not hand-patch generated SDK outputs.
5. On any regression, hold the integration PR and refine/rerun the recipe. Never label the original PR as merged based solely on ancestry.

**Status:** proposal tree built from Git blobs. Original PR reports 8,658 Maven test executions plus integration probes; those are historical evidence, not a passing result for this new combined tree. Full combined Maven/JMOD run remains NOT_RUN.
