<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements. See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License. You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

# Source-sealed BPE end-of-word oracle repair (2026-10-19)

## Failure

OpenNLP PR #13, GitHub Actions job 117603126490 (Java 21 runtime matrix), reported
`M3BpePreparedPairLookupTest.preservesDuplicateRanksAndOverlappingPairMerges`:
expected initial token `a` but observed `ab`.

The test branch had `new SymbolPair("b", "a")`; current `main` had
`new SymbolPair("b", "a" + BPETokenizer.END_OF_WORD)`. The latter correctly
targets the word-final symbol created by `BPETokenizer.splitToSymbols`.

## Exact source ownership

- Owner: `opennlp-core/opennlp-runtime/src/test/java/opennlp/tools/tokenize/M3BpePreparedPairLookupTest.java`
- Before Git blob: `7c17ed8f15a5a3204356ec1eb961246462a3cef8`
- After Git blob: `d52c687aa38683017709c90a9ddc8df9cb66d5cf`
- Independent current `main` oracle blob: `d52c687aa38683017709c90a9ddc8df9cb66d5cf`
- Frozen pre-repair PR head: `0887e1510ebc00e7bb54739cc320349fa85f42c5`
- Preservation ref: `preserve/pr13-before-bpe-eow-repair-20261019`
- Qualifying recipe: `RepairBpeEndOfWordOracle.java`

Only the prepared BPE test oracle is corrected; `BPETokenizer`, `BPEModel`,
runtime behavior, public APIs, and donor/original baseline are unchanged.

## Mechanical qualification

```sh
java m3/recipes/bpe-end-of-word-oracle-20261019/RepairBpeEndOfWordOracle.java --self-test
java m3/recipes/bpe-end-of-word-oracle-20261019/RepairBpeEndOfWordOracle.java --check opennlp-core/opennlp-runtime/src/test/java/opennlp/tools/tokenize/M3BpePreparedPairLookupTest.java
mvn -B -ntp -pl opennlp-core/opennlp-runtime -am -Dtest=M3BpePreparedPairLookupTest -Dsurefire.failIfNoSpecifiedTests=false test
```

The recipe is idempotent, fails on wrong method/zero/duplicate anchors, and preserves every
other byte. The repair is a test-input correction, not a BPE algorithm substitution;
LeetCode/HackerRank/GeeksforGeeks implementations are not applicable donors here.

Full PR #13 admission remains separate: synchronize the four files changed independently
on `main`, re-run the entire affected reactor, ASF checks, and required tests before merging.
Do not treat this one successful oracle repair as an all-forks release proof.
