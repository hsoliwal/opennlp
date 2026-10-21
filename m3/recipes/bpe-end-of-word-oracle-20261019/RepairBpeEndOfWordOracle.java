/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Fixed-point, exact-source recipe for the independently verified BPE end-of-word test oracle.
 * Production BPETokenizer/BPEModel and their runtime APIs are intentionally not modified.
 */
public final class RepairBpeEndOfWordOracle {
  private static final String OLD = "        new SymbolPair(\"b\", \"a\"),";
  private static final String NEW =
      "        new SymbolPair(\"b\", \"a\" + BPETokenizer.END_OF_WORD),";
  private static final String TEST = "preservesDuplicateRanksAndOverlappingPairMerges";

  private RepairBpeEndOfWordOracle() {}

  private static int occurrences(String source, String needle) {
    int count = 0;
    int offset = 0;
    while ((offset = source.indexOf(needle, offset)) >= 0) {
      count++;
      offset += needle.length();
    }
    return count;
  }

  static String rewrite(String source) {
    if (!source.contains(TEST)) {
      throw new IllegalArgumentException("BPE_TEST_ORACLE_IDENTITY_MISMATCH");
    }
    int oldCount = occurrences(source, OLD);
    int newCount = occurrences(source, NEW);
    if (oldCount == 1 && newCount == 0) {
      return source.replace(OLD, NEW);
    }
    if (oldCount == 0 && newCount == 1) {
      return source;
    }
    throw new IllegalArgumentException(
        "BPE_TEST_ORACLE_SOURCE_DRIFT:old=" + oldCount + ":new=" + newCount);
  }

  private static String sha(byte[] content) throws Exception {
    return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
  }

  private static void selfTest() {
    String old = "void " + TEST + "() {\n" + OLD + "\n}";
    String fixed = rewrite(old);
    if (!fixed.equals("void " + TEST + "() {\n" + NEW + "\n}")
        || !rewrite(fixed).equals(fixed)) {
      throw new AssertionError("BPE_RECIPE_NOT_FIXED_POINT");
    }
    try {
      rewrite(old + "\n" + OLD);
      throw new AssertionError("BPE_RECIPE_ACCEPTED_DUPLICATE_ANCHOR");
    } catch (IllegalArgumentException expected) {
      if (!expected.getMessage().startsWith("BPE_TEST_ORACLE_SOURCE_DRIFT")) {
        throw expected;
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 1 && "--self-test".equals(args[0])) {
      selfTest();
      System.out.println("BPE_RECIPE_SELF_TEST_PASS=true");
      return;
    }
    if (args.length != 2 || (!"--check".equals(args[0]) && !"--apply".equals(args[0]))) {
      throw new IllegalArgumentException(
          "usage: RepairBpeEndOfWordOracle --self-test | --check|--apply <M3BpePreparedPairLookupTest.java>");
    }
    Path target = Path.of(args[1]).toAbsolutePath().normalize();
    if (!"M3BpePreparedPairLookupTest.java".equals(target.getFileName().toString())
        || !Files.isRegularFile(target) || Files.isSymbolicLink(target)) {
      throw new IllegalArgumentException("BPE_SOURCE_PATH_NOT_OWNED");
    }
    byte[] before = Files.readAllBytes(target);
    String source = StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
        .decode(ByteBuffer.wrap(before)).toString();
    String result = rewrite(source);
    byte[] after = result.getBytes(StandardCharsets.UTF_8);
    if ("--check".equals(args[0]) && !source.equals(result)) {
      throw new IllegalStateException("BPE_RECIPE_NOT_APPLIED");
    }
    if ("--apply".equals(args[0]) && !source.equals(result)) {
      Path temporary = Files.createTempFile(target.getParent(), ".bpe-recipe-", ".tmp");
      try {
        Files.write(temporary, after);
        try {
          Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE,
              StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException unsupported) {
          Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        }
      } finally {
        Files.deleteIfExists(temporary);
      }
    }
    System.out.println("BPE_RECIPE_FIXED_POINT=" + rewrite(result).equals(result));
    System.out.println("BPE_ORACLE_PRE_SHA256=" + sha(before));
    System.out.println("BPE_ORACLE_POST_SHA256=" + sha(after));
    System.out.println("BPE_ORACLE_CHANGED=" + !source.equals(result));
  }
}
