package it.damore.lucene.example;

import static org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter.CATENATE_ALL;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter.CATENATE_NUMBERS;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter.CATENATE_WORDS;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter.GENERATE_WORD_PARTS;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter.PRESERVE_ORIGINAL;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter.SPLIT_ON_NUMERICS;
import static org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter.STEM_ENGLISH_POSSESSIVE;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoader;

public class HelloLuceneSynonymGraphAndWordDelimiterGraph {

  public static void main(String[] args) throws IOException
  {
    SynonymGraphFilterFactory sgf = init();
    applySynonymsGraphAndWordDelimiterGraph(sgf, "DVD-RW 4,7 GB - 120 Min 1-2 X Jewel Case Confezione 5 Pezzi");
    applySynonymsGraphAndWordDelimiterGraph(sgf, "DVD-RW 4,7 GB - 120 Min 1-2 X Jewel Case Confezione 5 Pezzi");
  }

  public static SynonymGraphFilterFactory init() throws IOException
  {
    // prepare the list of parameters
    Map<String, String> args = new HashMap<>();
    args.put("synonyms", "synonyms.txt");
    args.put("ignoreCase", Boolean.toString(true));
    args.put("expand", Boolean.toString(true));
    // create the SynonymGraphFilterFactory and the ResourceLoader
    SynonymGraphFilterFactory syf = new SynonymGraphFilterFactory(args);
    ResourceLoader rl = new FilesystemResourceLoader(Paths.get("."), HelloLuceneSynonymGraphAndWordDelimiterGraph.class.getClassLoader());
    syf.inform(rl);
    return syf;
  }

  public static String applySynonymsGraphAndWordDelimiterGraph(SynonymGraphFilterFactory localSyf, String input)
  {
    StringBuilder sb = new StringBuilder();

    // Specify the Whitespace Tokenizer divides text at whitespace characters
    try (Tokenizer wt = new WhitespaceTokenizer()) {
      // Set the input string as Reader
      wt.setReader(new StringReader(input));
      try (TokenStream synf = localSyf.create(wt)) {

        try (WordDelimiterGraphFilter localWordDelimFilter = new WordDelimiterGraphFilter(synf, GENERATE_WORD_PARTS | 
                                                                                              GENERATE_NUMBER_PARTS | 
                                                                                              CATENATE_WORDS | 
                                                                                              CATENATE_NUMBERS | 
                                                                                              CATENATE_ALL | 
                                                                                              PRESERVE_ORIGINAL | 
                                                                                              SPLIT_ON_CASE_CHANGE | 
                                                                                              SPLIT_ON_NUMERICS | 
                                                                                              STEM_ENGLISH_POSSESSIVE,
                                                                                          null)) {

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
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(sb.toString());
    return sb.toString();
  }

}
