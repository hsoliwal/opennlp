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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/** Independent ASF WordPiece baseline; test by Hitesh Soliwal and Synexia contributors. */
class WordpieceTokenizerM3RangeTest {
  private static final Set<String> VOCAB = Set.of(
      "a", "b", "ab", "x", "##a", "##b", "##ab", "##x",
      "é", "##é", "中", "##中", "😀", "##😀", ".", "!");
  private static String[] baseline(String text, int max) {
    List<String> tokens = new ArrayList<>();
    tokens.add(WordpieceTokenizer.BERT_CLS_TOKEN);
    for (String token : WhitespaceTokenizer.newInstance(false)
        .tokenize(BertNormalization.isolatePunctuation(text))) {
      char[] chars = token.toCharArray();
      if (Character.codePointCount(chars, 0, chars.length) > max) {
        tokens.add(WordpieceTokenizer.BERT_UNK_TOKEN);
        continue;
      }
      List<String> pieces = new LinkedList<>();
      int start = 0;
      boolean found = true;
      while (start < chars.length) {
        int end = chars.length;
        found = false;
        while (start < end) {
          String piece = String.valueOf(chars, start, end - start);
          if (start > 0) piece = BertNormalization.CONTINUATION_PREFIX + piece;
          if (VOCAB.contains(piece)) {
            pieces.add(piece);
            start = end;
            found = true;
            break;
          }
          end -= Character.charCount(Character.codePointBefore(chars, end));
        }
        if (!found) break;
        start = end;
      }
      if (found) tokens.addAll(pieces);
      else tokens.add(WordpieceTokenizer.BERT_UNK_TOKEN);
    }
    tokens.add(WordpieceTokenizer.BERT_SEP_TOKEN);
    return tokens.toArray(String[]::new);
  }
  @Test void regressionsAndUnpairedSurrogates() {
    String[] cases = {"", "ab", "a b", "a!!b", "é", "e\u0301", "中",
        "😀", "\uD800", "\uDC00", "a\uD83D\uDE00b", "\u0000", "\uFFFF", "##a"};
    for (int max : new int[] {0, 1, 2, 3, 20, 100}) {
      var tokenizer = new WordpieceTokenizer(VOCAB, max);
      for (String text : cases) assertArrayEquals(baseline(text, max), tokenizer.tokenize(text), text);
    }
  }
  @Test void deterministicFuzz() {
    Random rng = new Random(20261014L);
    int[] alphabet = {'a', 'b', 'x', '.', '!', ' ', 0x0301, 0x4E2D, 0xD800, 0xDC00, 0xFFFF};
    for (int i = 0; i < 400; i++) {
      StringBuilder text = new StringBuilder();
      for (int n = rng.nextInt(14); n > 0; n--) text.append((char)alphabet[rng.nextInt(alphabet.length)]);
      for (int max : new int[] {0, 1, 2, 3, 20}) {
        var tokenizer = new WordpieceTokenizer(VOCAB, max);
        assertArrayEquals(baseline(text.toString(), max), tokenizer.tokenize(text.toString()));
      }
    }
  }
}
