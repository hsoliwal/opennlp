/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
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

package opennlp.tools.tokenize;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import opennlp.tools.util.Span;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Real OpenNLP integration tests using actual Span, StringUtil and tokenizer dependencies. */
class M3TokenizersTest {
  private record Composite(String left, String right) implements CharSequence {
    Composite(String text, int split) {
      this(text.substring(0, split), text.substring(split));
    }

    public int length() {
      return left.length() + right.length();
    }

    public char charAt(int i) {
      Objects.checkIndex(i, length());
      return i < left.length() ? left.charAt(i) : right.charAt(i - left.length());
    }

    public CharSequence subSequence(int start, int end) {
      throw new AssertionError("must not slice input");
    }

    @Override
    public String toString() {
      throw new AssertionError("must not flatten input");
    }
  }

  private static Object outcome(Supplier<Span[]> call) {
    try {
      return Arrays.asList(call.get());
    } catch (RuntimeException failure) {
      return List.of(failure.getClass().getName(), Objects.toString(failure.getMessage()));
    }
  }

  @Test
  void sharedStateMachinePreservesCompositeBoundaryAndCharacterClasses() {
    String[] examples = {"", "Cats running...", "é\u0301", "A\uD83D\uDE00B", "\uD800\uDC00\uDC00",
        "\u0000\t\n\r\f\u0085\u00a0\u2028", "१२३abc９９", "+++--==", "a\u001cb"};
    synchronized (SimpleTokenizer.INSTANCE) {
      boolean previous = SimpleTokenizer.INSTANCE.keepNewLines;
      try {
        for (boolean newlines : new boolean[] {false, true}) {
          SimpleTokenizer.INSTANCE.setKeepNewLines(newlines);
          for (String text : examples) {
            for (int split = 0; split <= text.length(); split++) {
              Composite source = new Composite(text, split);
              assertEquals(outcome(() -> SimpleTokenizer.INSTANCE.tokenizePos(text)),
                  outcome(() -> M3Tokenizers.simpleSpans(source)));
            }
          }
        }
      } finally {
        SimpleTokenizer.INSTANCE.setKeepNewLines(previous);
      }
    }
  }

  @Test
  void randomizedUtf16AndCheckpointCancellation() {
    var random = new Random(20261014);
    for (int trial = 0; trial < 10000; trial++) {
      char[] units = new char[trial % 129];
      for (int i = 0; i < units.length; i++) {
        units[i] = (char) random.nextInt(65536);
      }
      String text = new String(units);
      assertArrayEquals(SimpleTokenizer.INSTANCE.tokenizePos(text),
          M3Tokenizers.simpleSpans(new Composite(text, random.nextInt(units.length + 1)), () -> { }));
    }
    assertThrows(NullPointerException.class, () -> M3Tokenizers.simpleSpans(null));
    assertThrows(CancellationException.class,
        () -> M3Tokenizers.simpleSpans("abc", () -> { throw new CancellationException("fixture"); }));
    Thread.currentThread().interrupt();
    try {
      assertThrows(CancellationException.class, () -> M3Tokenizers.simpleSpans("abc"));
    } finally {
      Thread.interrupted();
    }
  }
}
