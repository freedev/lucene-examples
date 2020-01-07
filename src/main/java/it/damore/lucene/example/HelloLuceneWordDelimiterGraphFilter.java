package it.damore.lucene.example;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HelloLuceneWordDelimiterGraphFilter {

    public static void main(String[] args) throws IOException {
//    List<String> words = Arrays.asList("wi-fi", "iPhone 5S", "+39-02-123456689", " ecc1dp0u samsung");
        List<String> words = Arrays.asList(" ecc1dp0u samsung");
        GenericPermutations<WordDelimiters> perm = new GenericPermutations<>();
        List<WordDelimiters[]> wordDelimitersPermutatons = perm.perm1(WordDelimiters.values());
        wordDelimitersPermutatons.forEach(wd -> {
            words.stream()
                 .forEach(s -> {
                     System.out.println(s + " ===> " + Arrays.asList(wd).stream().map(w -> w.toString())
                                                             .collect(Collectors.joining(" - ", "---", "===")));
                     applyWordDelimiter(s, Arrays.asList(wd));
                 });
        });

//    applyWordDelimiter("iPhone 5S", GENERATE_WORD_PARTS);
//    applyWordDelimiter("+39-02-123456689", GENERATE_WORD_PARTS);
//    System.out.println("== GENERATE_WORD_PARTS ==");
//
//    applyWordDelimiter("wi-fi", GENERATE_NUMBER_PARTS);
//    applyWordDelimiter("iPhone 5S", GENERATE_NUMBER_PARTS);
//    applyWordDelimiter("+39-02-123456689", GENERATE_NUMBER_PARTS);
    }

    public static String applyWordDelimiter(String input, List<WordDelimiters> configurationFlags) {
        StringBuilder sb = new StringBuilder();

        int flags = configurationFlags.stream().mapToInt(f -> f.getValue()).sum();

        // Specify the Whitespace Tokenizer divides text at whitespace characters
        try (Tokenizer wt = new WhitespaceTokenizer()) {
            // Set the input string as Reader
            wt.setReader(new StringReader(input));
            try (WordDelimiterGraphFilter localWordDelimFilter = new WordDelimiterGraphFilter(wt, flags, null)) {
                // This method has to called by a consumer before it begins consumption
                localWordDelimFilter.reset();
                // Define a couple of attributes
                CharTermAttribute term = localWordDelimFilter.addAttribute(CharTermAttribute.class);
                OffsetAttribute offset = localWordDelimFilter.addAttribute(OffsetAttribute.class);
                // this method advance the stream to the next token
                if (localWordDelimFilter.incrementToken()) {
                    // read the first token
                    sb.append(term.toString());
                    // print start/end offsets and term
                    System.out.println(offset.startOffset() + " " + offset.endOffset() + " " + term.toString());
                    // again, advance the stream to the next token and prints
                    while (localWordDelimFilter.incrementToken()) {
                        System.out.println(offset.startOffset() + " " + offset.endOffset() + " " + term.toString());
                        sb.append(" ");
                        sb.append(term.toString());
                    }
                }
                // called by the consumer after the last token has been consumed
                localWordDelimFilter.end();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    enum WordDelimiters {
        /*
         * Configuration flags: GENERATE_WORD_PARTS | GENERATE_NUMBER_PARTS | CATENATE_WORDS | CATENATE_NUMBERS |
         * CATENATE_ALL | PRESERVE_ORIGINAL | SPLIT_ON_CASE_CHANGE | SPLIT_ON_NUMERICS | STEM_ENGLISH_POSSESSIVE
         */
        GENERATE_WORD_PARTS(WordDelimiterGraphFilter.GENERATE_WORD_PARTS),
        GENERATE_NUMBER_PARTS(WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS),
        CATENATE_WORDS(WordDelimiterGraphFilter.CATENATE_WORDS),
        CATENATE_NUMBERS(WordDelimiterGraphFilter.CATENATE_NUMBERS),
        CATENATE_ALL(WordDelimiterGraphFilter.CATENATE_ALL),
        PRESERVE_ORIGINAL(WordDelimiterGraphFilter.PRESERVE_ORIGINAL),
        SPLIT_ON_CASE_CHANGE(WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE),
        SPLIT_ON_NUMERICS(WordDelimiterGraphFilter.SPLIT_ON_NUMERICS),
        STEM_ENGLISH_POSSESSIVE(WordDelimiterGraphFilter.STEM_ENGLISH_POSSESSIVE);

        private int value;

        private WordDelimiters(int v) {
            value = v;
        }

        int getValue() { return value; }

    }

}
