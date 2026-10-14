# OpenNLP M3 text receiver

Draft candidate from `hsoliwal/opennlp@4713d82577dd3e9e48cd9855ccd891c2739324a4`.
Master recipe/tests: [Synexia PR 11438](https://github.com/hsoliwal/com.synexia/pull/11438), pinned commit `e3af85dac71bfac047163286ae42bda326df549f`.

## Generated changes

The built-in context list starts at capacity 18. Fixed category spellings use literal backing via `new String(known)`, deliberately preserving fresh String wrapper identity. Custom/null keys retain concatenation. Classification, whitespace delegation, feature contents/order, abbreviation lookup and public/protected hooks remain intact. No interning table, model cache or public M3String type substitution is introduced.

The actual source and full postimage template are byte-identical and SHA-256 sealed; the original licensed source is kept as the preimage test fixture. Reusable recipe implementation stays in Synexia, not a downstream fork. `source-packet.tsv` pins every source ref/path/hash. `receiver.sh` verifies bytes before compilation/execution; an offline checkout cannot bypass those checks.

```sh
mvn -B -ntp -f .m3/text-integration/pom.xml verify
# Apply only to the exact admitted preimage in an exclusive checkout:
mvn -B -ntp -f .m3/text-integration/pom.xml -Dm3.mode=apply verify
# Offline source handoff, same checksum enforcement:
M3_CANONICAL_CHECKOUT=/path/to/synexia bash .m3/text-integration/receiver.sh . verify
# After receiver execution, search the canonical task catalogue:
grep -i 'prefix' .m3/text-integration/target/canonical/catalogue.jsonl
```

## Evidence and limits

Local direct-script replay passed 1,052,956 checks: source/template drift refusal, fixed point, compiler-extracted context methods, and actual classification branches over 7 keys x 2 injected whitespace outcomes x every UTF-16 unit. Fresh wrapper identity, null/custom keys, feature ordering, bounds and exceptions are tested. Context hooks use a documented test double; production whitespace libraries are unchanged. Tests are checksum-pinned from Synexia; immutable preimage/postimage fixtures and receipts remain here.

These are not full OpenNLP model/tokenizer/JUnit or numerical-model tests. Maven was not installed in the authoring environment; the exact receiver/qualification scripts ran directly. The CI workflow requests the Maven gate, but missing/skipped/zero-job CI is unverified. Full product reactor/lint/JUnit, M3JDK runtime, representative allocation/throughput benchmarks and canonical promotion remain gates. Keep draft; do not equate payload sharing with measured end-to-end speedup.

The 15-category master catalogue records trie/prefix/suffix candidates and decisions. LeetCode/HackerRank/GeeksforGeeks are discovery only, not copied implementations. Existing Synexia AGENTS.md and recipe-mastery invariant remain normative. Fix the canonical recipe/template and rerun; never hand-patch generated targets.

Not implemented: token-local hierarchical feature preparation, frozen-model feature IDs, shared sentence ownership, interner policy changes, JNI model evaluation or complete repository coverage.
