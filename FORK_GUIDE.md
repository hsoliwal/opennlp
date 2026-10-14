<!-- SPDX-License-Identifier: Apache-2.0 -->

# Fork guide: projects, packages and API boundaries

This public fork of [apache/opennlp](https://github.com/apache/opennlp) is documented at [4713d82577dd](https://github.com/hsoliwal/opennlp/tree/4713d82577dd3e9e48cd9855ccd891c2739324a4), inspected 2026-10-14. It is the 3.0.0-SNAPSHOT development layout. Use the [README](README.md) for migration context and distinguish this source snapshot from a published stable release.

[Package evidence and gaps](PACKAGE_INVENTORY.md) · [Manual source](opennlp-docs/src/docbkx/opennlp.xml) · [Contribution guide](.github/CONTRIBUTING.md) · [Security](SECURITY.md) · [License](LICENSE) · [Notices](NOTICE)

## Complete Maven project/module map

All 23 POM projects (root plus 22 descendants) were inspected. A Maven module is not the same as a Java namespace: the same namespace can be split between API and implementation artifacts. The package inventory preserves owning source roots rather than collapsing those boundaries.

| Manifest | Responsibility and integration boundary |
| --- | --- |
| [opennlp-api/pom.xml](opennlp-api/pom.xml) | Public interfaces, data structures and shared contracts; applications and implementation modules depend on these abstractions |
| [opennlp-core/opennlp-cli/pom.xml](opennlp-core/opennlp-cli/pom.xml) | Command dispatch and task-specific train/evaluate/run tools; entry point opennlp.tools.cmdline.CLI; operates on files, models and streams |
| [opennlp-core/opennlp-formats/pom.xml](opennlp-core/opennlp-formats/pom.xml) | Corpus and training-data readers/converters, including CoNLL-U, Brat, MASC and language-specific datasets; converts external formats to task samples |
| [opennlp-core/opennlp-ml/opennlp-dl-gpu/pom.xml](opennlp-core/opennlp-ml/opennlp-dl-gpu/pom.xml) | Dependency variant: reuses opennlp-dl, excludes CPU onnxruntime and selects onnxruntime_gpu; scans the DL test JAR instead of owning Java source packages |
| [opennlp-core/opennlp-ml/opennlp-dl/pom.xml](opennlp-core/opennlp-ml/opennlp-dl/pom.xml) | ONNX inference adapters for categorization, name finding and vectors; does not train ONNX models |
| [opennlp-core/opennlp-ml/opennlp-ml-bayes/pom.xml](opennlp-core/opennlp-ml/opennlp-ml-bayes/pom.xml) | Naive Bayes implementation, model I/O and probability helpers; optional algorithm choice |
| [opennlp-core/opennlp-ml/opennlp-ml-commons/pom.xml](opennlp-core/opennlp-ml/opennlp-ml-commons/pom.xml) | Shared trainer/model infrastructure and training parameters used by concrete ML implementations |
| [opennlp-core/opennlp-ml/opennlp-ml-libsvm/pom.xml](opennlp-core/opennlp-ml/opennlp-ml-libsvm/pom.xml) | SVM document-classification implementation backed by zlibsvm; separate dependency/configuration boundary |
| [opennlp-core/opennlp-ml/opennlp-ml-maxent/pom.xml](opennlp-core/opennlp-ml/opennlp-ml-maxent/pom.xml) | Maximum-entropy model training/inference and binary model I/O; contains GIS and quasi-Newton implementation packages |
| [opennlp-core/opennlp-ml/opennlp-ml-perceptron/pom.xml](opennlp-core/opennlp-ml/opennlp-ml-perceptron/pom.xml) | Perceptron training/model implementation and binary I/O; optional algorithm choice |
| [opennlp-core/opennlp-ml/pom.xml](opennlp-core/opennlp-ml/pom.xml) | ML reactor aggregator; separates shared training infrastructure from algorithm implementations and ONNX adapters |
| [opennlp-core/opennlp-model-resolver/pom.xml](opennlp-core/opennlp-model-resolver/pom.xml) | Model discovery/loading via classpath, directory and ClassGraph finders; crosses the resource I/O boundary |
| [opennlp-core/opennlp-runtime/pom.xml](opennlp-core/opennlp-runtime/pom.xml) | Task implementations, model wrappers, tokenizers, normalization, feature generation and training utilities; README specifies MaxEnt as default ML dependency |
| [opennlp-core/pom.xml](opennlp-core/pom.xml) | Core reactor aggregator for ML, runtime, model resolution, formats and CLI; POM packaging |
| [opennlp-distr/pom.xml](opennlp-distr/pom.xml) | POM-packaged assembly project for distribution artifacts; not another Java API library |
| [opennlp-docs/pom.xml](opennlp-docs/pom.xml) | DocBook manual build; authoritative task guides and examples under src/docbkx |
| [opennlp-eval-tests/pom.xml](opennlp-eval-tests/pom.xml) | Evaluation harness with externally supplied datasets; tests skipped by default and enabled by explicit profiles; install/deploy skipped |
| [opennlp-extensions/opennlp-morfologik/pom.xml](opennlp-extensions/opennlp-morfologik/pom.xml) | Dictionary construction, tag dictionaries and lemmatization through Morfologik; includes separate CLI tools |
| [opennlp-extensions/opennlp-spellcheck/pom.xml](opennlp-extensions/opennlp-spellcheck/pom.xml) | SymSpell dictionary/model loading, distance calculations, lookup and compound correction; dictionary population and safe publication are caller responsibilities |
| [opennlp-extensions/opennlp-uima/pom.xml](opennlp-extensions/opennlp-uima/pom.xml) | UIMA annotators and model resources; translates task output into CAS annotations under UIMA lifecycle/configuration |
| [opennlp-extensions/pom.xml](opennlp-extensions/pom.xml) | Extension reactor aggregator; keeps optional dictionaries, spelling and UIMA integration separate |
| [opennlp-tools/pom.xml](opennlp-tools/pom.xml) | Full toolkit compatibility/executable artifact aggregating core dependencies; main class CLI and runtime ML providers; not the location of all implementation source |
| [pom.xml](pom.xml) | Root POM: version 3.0.0-SNAPSHOT, dependency/plugin policy and seven direct reactor children; not an application artifact |

## Dependency and selection guide

Start with public contracts in opennlp-api, then select an implementation via opennlp-runtime and the algorithm/model dependencies actually required. The [README](README.md) describes the modular 3.x migration and opennlp-tools compatibility artifact. The [tools POM](opennlp-tools/pom.xml) shows that CLI, formats, runtime, model resolver and ML providers are distinct dependencies.

Model binaries are separate resources; adding a runtime JAR does not create a trained language model. The [model-loading manual](opennlp-docs/src/docbkx/model-loading.xml) and resolver package describe discovery. ONNX adapters consume externally trained models; CPU/GPU packaging choices do not prove accelerator availability. See the [DL guide](opennlp-core/opennlp-ml/opennlp-dl/README.md) and [GPU manifest](opennlp-core/opennlp-ml/opennlp-dl-gpu/pom.xml).

## Important API contracts and lifecycle

### Text offsets and token offsets

[Tokenizer](opennlp-api/src/main/java/opennlp/tools/tokenize/Tokenizer.java) exposes tokenize(String) for token strings and tokenizePos(String) for offsets into the input string. [SentenceDetector](opennlp-api/src/main/java/opennlp/tools/sentdetect/SentenceDetector.java) accepts CharSequence and returns sentences or character-sequence spans. In contrast, [TokenNameFinder.find(String[])](opennlp-api/src/main/java/opennlp/tools/namefind/TokenNameFinder.java) returns token spans. Preserve the token-to-character mapping when joining results; a token index is not a character offset.

The name-finder interface includes clearAdaptiveData(), normally used at a document boundary. [NameFinderME](opennlp-core/opennlp-runtime/src/main/java/opennlp/tools/namefind/NameFinderME.java) and [TokenizerME](opennlp-core/opennlp-runtime/src/main/java/opennlp/tools/tokenize/TokenizerME.java) document thread-safe instances in this snapshot, with a classloader-lifetime warning for ThreadLocal-backed state. Do not extend that claim to older release lines or every model/extension. A tokenizer model reproduces conventions of its training data; input language and normalization choices matter.

### Streams and resource ownership

[ObjectStream<T>](opennlp-api/src/main/java/opennlp/tools/util/ObjectStream.java) is AutoCloseable. read() returns null at exhaustion and can throw IOException. reset() is optional and defaults to UnsupportedOperationException. After close(), callers must not read or reset. Training that requires repeated passes needs a reset-capable stream; it must not silently assume ordinary Iterator behavior. Close streams under caller ownership, including on failures.

### Loading models

[ClassPathModelLoader.load(ClassPathModelEntry)](opennlp-core/opennlp-model-resolver/src/main/java/opennlp/tools/models/ClassPathModelLoader.java) requires non-null entry, metadata Optional and model URI. It opens/closes metadata and binary streams, loads the entire binary into a byte array and can throw IOException. Resource absence, invalid model bytes and incompatible model types are separate integration concerns.

Its set/language/type overloads are package-private, not public extension APIs. The implementation rejects null/blank language with IllegalArgumentException, despite a comment saying such language yields null; treat this as a native documentation discrepancy for a separate correction. No code or inline comments were changed here.

### Spelling and optional integrations

[SpellChecker](opennlp-extensions/opennlp-spellcheck/src/main/java/opennlp/spellcheck/SpellChecker.java) distinguishes concurrent lookup after a fully populated dictionary has been safely published from dictionary population itself, which is not required to be thread-safe. Review [the spelling guide](opennlp-extensions/opennlp-spellcheck/README.md) for lookup verbosity, edit distance and compound correction. UIMA clients instead need model resources and annotation-type configuration; see [the UIMA chapter](opennlp-docs/src/docbkx/uima-integration.xml).

## Task-to-manual navigation

- [Tokenization](opennlp-docs/src/docbkx/tokenizer.xml), [sentences](opennlp-docs/src/docbkx/sentdetect.xml), [POS](opennlp-docs/src/docbkx/postagger.xml), [lemmas](opennlp-docs/src/docbkx/lemmatizer.xml), [named entities](opennlp-docs/src/docbkx/namefinder.xml)
- [Chunking](opennlp-docs/src/docbkx/chunker.xml), [parsing](opennlp-docs/src/docbkx/parser.xml), [dependency parsing](opennlp-docs/src/docbkx/dependency.xml)
- [Categorization](opennlp-docs/src/docbkx/doccat.xml), [language detection](opennlp-docs/src/docbkx/langdetect.xml), [sentiment](opennlp-docs/src/docbkx/sentiment.xml)
- [Documents](opennlp-docs/src/docbkx/document.xml), [normalization](opennlp-docs/src/docbkx/normalizer.xml), [stemming](opennlp-docs/src/docbkx/stemmer.xml), [stopwords](opennlp-docs/src/docbkx/stopword.xml)
- [ML](opennlp-docs/src/docbkx/machine-learning.xml), [corpora](opennlp-docs/src/docbkx/corpora.xml), [CLI](opennlp-docs/src/docbkx/cli.xml), [extensions](opennlp-docs/src/docbkx/extension.xml)

These links locate existing authoritative task documentation; each chapter's completeness was not independently certified in this editorial pass.

## Setup and verification boundaries

The current [README](README.md) and [root POM](pom.xml) require JDK 21 and Maven 3.9.x (POM minimum 3.9.0). This is different from the separately maintained 2.x line. Run from the repository root:

- mvn install: documented reactor build and local installation
- mvn -pl opennlp-core/opennlp-runtime -am test: focused runtime and prerequisite unit-test lifecycle; not full verification
- mvn clean verify: reaches configured verification/integration-test phases; still does not enable every optional evaluation profile

[opennlp-eval-tests/pom.xml](opennlp-eval-tests/pom.xml) skips its tests by default. The eval-tests profile selects *Eval classes, uses a 4 GB heap and excludes the high-memory group; high-memory-tests uses a 20 GB heap and its own group selection. The [evaluation guide](opennlp-docs/src/docbkx/evaltest.xml) describes external data and OPENNLP_DATA_DIR. Do not count ordinary unit-test completion as external-dataset or high-memory evaluation. GPU and ONNX dependencies add separate resource/runtime requirements.

No Maven, Java, training, download, lint, compilation, tests or runtime verification were executed for this documentation change. Commands above are instructions derived from source, not receipts.

## Proposed M3 use, not implemented

A future M3 system could use explicit NLP adapters for text segmentation, entity candidates or language classification. Preserve original text and offset mapping; record model/runtime versions and distinguish probabilistic annotations from authoritative repository structure. Define adapter inputs, outputs, failure behavior and evaluation before integration. This repository audit does not establish an implemented M3 parser, recognizer, proof engine or orchestration layer.

## Coverage and remaining work

All 23 Maven projects and 154 production-source package directories are indexed. One representative type per production package was read, and all 136 test-source package directories are accounted for. Eight production package directories contain native package docs. The [package ledger](PACKAGE_INVENTORY.md) links existing descriptions and flags missing native docs; it is not complete per-package semantic documentation.

Detailed contracts above cover important integration boundaries. Every package still needs a full surface/dependency/state/error/test review to meet a strict all-package completion gate. Native package-info/Javadoc changes, the model-loader comment discrepancy, missing examples and executable verification remain separate work. No package is claimed fully verified by this Markdown-only draft.
