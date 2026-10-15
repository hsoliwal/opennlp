# Optional M3 composite regex input in OpenNLP

This is an additive experimental integration. RegexNameFinder.find(String[]) retains its public contract and the existing map of UTF-16 regex offsets to token boundaries. The private M3RegexTextBuilder replaces one local StringBuilder variable and is compatible with every operation used at that site: append(String), append(char), length(), CharSequence.charAt() and CharSequence.subSequence().

Default behavior is to delegate to the original StringBuilder and pass it directly to Pattern.matcher via sentenceString.input(); the segmented path is disabled unless -Dopennlp.m3.compositeRegex=true is set. When enabled, incoming immutable token Strings and explicit space atoms are stored as separate references, avoiding a joined character-buffer copy. java.util.regex.Pattern and Matcher remain authoritative for expression semantics, zero-width matches, groups, offsets and replacements. A cursor accelerates repeated sequential charAt reads; random seeks use a binary search over segment endpoints.

This code does **not** require Synexia at runtime, nor access to the package-private java.lang.M3String of M3JDK21. M3JDK21's String facade can provide canonical storage for the same input tokens. JNI/native regex execution is not enabled in this patch.

Canonical donor/template and Maven/OpenRewrite recipe:
- Synexia: synexia-openrewrite-recipes/src/main/java/com/synexia/rewrite/M3OpenNlpRegexCompositeInputRecipe.java
- Synexia template: synexia-openrewrite-recipes/src/main/resources/com/synexia/rewrite/opennlp-regex-composite/M3RegexTextBuilder.java.txt

Verification before merging:
- Run ./mvnw -pl opennlp-core/opennlp-runtime -am -Dtest=RegexNameFinderTest,M3RegexTextBuilderTest -Dsurefire.failIfNoSpecifiedTests=false test with all necessary dependencies.
- Run the module's full regression suite and stress cases for Unicode, empty/null tokens, empty regex matches, capturing groups, and token-boundary mapping.
- Run JMH comparing baseline vs enabled on stock Java and M3JDK21. **No throughput/allocation improvement is claimed without benchmarking.**
- Rerun Synexia OpenRewrite recipe and verify fixed point/source drift; do not patch generated code manually.

Original ASF source headers, Apache-2.0 LICENSE/NOTICE, and upstream public APIs are preserved.
