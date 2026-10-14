<!-- SPDX-License-Identifier: Apache-2.0 -->

# OpenNLP fork: practical workflows

This page is for the `hsoliwal/opennlp` fork of
[apache/opennlp](https://github.com/apache/opennlp). Start with the root README's
[introduction and requirements](../README.md). The examples below use this
checkout's 3.x module layout; fork branches and snapshots are not Apache releases.

## Choose a starting point

| Goal | Read or inspect |
| --- | --- |
| Understand public interfaces | [opennlp-api](../opennlp-api) |
| Find the NLP implementations | [opennlp-runtime](../opennlp-core/opennlp-runtime) |
| Trace a terminal command | [CLI dispatch](../opennlp-core/opennlp-cli/src/main/java/opennlp/tools/cmdline/CLI.java) |
| Understand model lookup | [model resolver](../opennlp-core/opennlp-model-resolver) and [model-loading chapter](../opennlp-docs/src/docbkx/model-loading.xml) |
| Choose library dependencies | [project-structure chapter](../opennlp-docs/src/docbkx/project-structure.xml) |
| Work on a manual chapter | [DocBook sources](../opennlp-docs/src/docbkx) and [documentation POM](../opennlp-docs/pom.xml) |
| Find optional integrations | [extensions](../opennlp-extensions) |

`opennlp-runtime` and `opennlp-cli` live inside `opennlp-core`; use their complete
paths when selecting Maven projects. API, runtime, CLI, model resources, and
distribution packaging serve different purposes, so first pick the layer you need.

## Focused command cheat sheet

Clone the fork and inspect the Java/Maven versions selected on your machine:

```sh
git clone https://github.com/hsoliwal/opennlp.git
cd opennlp
git rev-parse HEAD
java -version
mvn -version
```

Use the requirements in the [root POM](../pom.xml) and README. From this checkout's root,
choose a command for the part you are working on:

```sh
# API tests and required reactor projects
mvn -pl opennlp-api -am test

# Runtime tests and required reactor projects
mvn -pl opennlp-core/opennlp-runtime -am test

# Generate the manual through its documentation module
mvn -pl opennlp-docs -am package

# Check a local patch for whitespace mistakes
git diff --check
```

`-pl` selects a project; `-am` includes its required reactor projects. Initial builds
need dependency downloads. For the complete build and opt-in evaluation limitations,
use [Choosing build and test scope](../README.md#choosing-build-and-test-scope).
A focused command does not establish whole-project correctness.

## First CLI example: no downloaded model required

After the distribution has been built successfully, extract the binary archive from
`opennlp-distr/target` into a separate directory. Run these POSIX-shell commands from
the extracted distribution's root, which contains `bin` and `lib`:

```sh
bin/opennlp SimpleTokenizer help
printf '%s\n' 'Hello, OpenNLP!' | bin/opennlp SimpleTokenizer
```

This demonstrates input flowing through the supplied launcher. The
[SimpleTokenizer tool](../opennlp-core/opennlp-cli/src/main/java/opennlp/tools/cmdline/tokenizer/SimpleTokenizerTool.java)
uses character classes and does not load a model. For trained tokenization, continue
with the [tokenizer chapter](../opennlp-docs/src/docbkx/tokenizer.xml); for command
selection and argument help, use the [CLI chapter](../opennlp-docs/src/docbkx/cli.xml).

## Troubleshooting by symptom

- **Java release error:** compare `java -version` and `mvn -version`, including
  Maven's Java home. This `main` checkout requires Java 21; older 2.x instructions
  can refer to a different baseline.
- **Maven cannot find the selected project:** return to the repository root and use
  its full path, including `opennlp-core` where applicable. Include `-am` for reactor dependencies.
- **Wrapper download fails:** inspect the pinned distribution in
  [maven-wrapper.properties](../.mvn/wrapper/maven-wrapper.properties) and your network/proxy
  settings. The root README also supports invoking an installed Maven of the required version.
- **Launcher or classpath failure:** use the complete extracted distribution,
  keeping its `bin`, `lib`, and configuration files together. Copying only the script
  or one JAR omits parts of the distribution.
- **Model missing or wrong type:** first try the model-free example above to separate
  launcher problems from model loading. Follow the selected tool's model format and
  language requirements; a model JAR on the classpath and a raw `.bin` argument are
  different loading routes.

When reporting a problem, include the commit, Java/Maven versions, selected command,
and first failing goal. Follow the [contribution guide](../.github/CONTRIBUTING.md)
and [security policy](../SECURITY.md) for the appropriate reporting route.
Commands and paths here were checked against source; the builds and CLI example
were not executed for this documentation.
