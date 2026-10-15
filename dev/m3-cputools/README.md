# M3 composite-text receiver: hsoliwal/opennlp

This is an additive candidate from the canonical `hsoliwal/com.synexia` recipe owner.
It does not establish whole-project, M3JDK, performance or canonical-promotion success.

Recipe: `com.synexia.m3.OpenNlpCompositeSpans`. Receiving base: `4713d82577dd3e9e48cd9855ccd891c2739324a4`.
Canonical task: `synexia-openrewrite-recipes/recipes/cputools-native-20261014`.
Public String entrypoints remain present. No alternate text/interner owner is introduced.

The original Maven build remains product authority.
This bounded Maven POM is a review/control plane only. It invokes the existing Synexia
source-seal owner; it never downloads, copies or forks a rewrite implementation here.
Supply a locally available, reviewed Synexia recipe checkout:

```sh
mvn -o -f dev/m3-cputools/pom.xml -Dsynexia.recipe.home=/path/to/com.synexia verify
```

That command performs an exact hash/fixed-point check, not a product build/test.
It was not executed locally because Maven was unavailable. For source-seal application,
run the canonical Synexia `receive.py --kind opennlp --target /path/to/opennlp --mode apply`;
all target/template hashes are checked before the first serial per-file write.

## Qualification boundary

`M3TokenizersTest` uses the real OpenNLP scanner, `Span` and JUnit APIs. It was authored but
not compiled or executed locally because genuine donor/logging/test dependencies were
unavailable. No fake provider classes substituted for them. Run it through the existing
OpenNLP reactor and retain its non-skipped test report before promotion.

`M3Tokenizers.simpleSpans(CharSequence)` reuses `SimpleTokenizer`'s scanner; the optional
Runnable overload supplies cancellation/progress checkpoints. Inputs must remain immutable.
The existing whitespace-mode/newline configuration is retained rather than "corrected".
This is span tokenization only, not POS, NER, OCR or model inference.
