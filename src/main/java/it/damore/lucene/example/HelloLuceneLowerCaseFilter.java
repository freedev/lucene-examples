package it.damore.lucene.example;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoader;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HelloLuceneLowerCaseFilter {

  public static void main(String[] args) throws IOException
  {
    LowerCaseFilterFactory lowerCaseFilterFactory = getLowerCaseFilterFactory();
    applyFilter(lowerCaseFilterFactory, "TESTO di Prova");
    applyFilter(lowerCaseFilterFactory, "Incremento di vendita in Italia");
    applyFilter(lowerCaseFilterFactory, "United States of America");
  }

  public static LowerCaseFilterFactory getLowerCaseFilterFactory()
  {
    // create the LowerCaseFilterFactory
    LowerCaseFilterFactory syf = new LowerCaseFilterFactory(Collections.emptyMap());
    return syf;
  }

  public static String applyFilter(LowerCaseFilterFactory localSyf, String input)
  {
    StringBuilder sb = new StringBuilder();
    
    // Specify the Whitespace Tokenizer divides text at whitespace characters    
    try (Tokenizer wt = new WhitespaceTokenizer()) {
      // Set the input string as Reader
      wt.setReader(new StringReader(input));
      try (TokenStream syn = localSyf.create(wt)) {
        // This method has to called by a consumer before it begins consumption
        syn.reset();
        // Define a couple of attributes 
        CharTermAttribute term = syn.addAttribute(CharTermAttribute.class);
        OffsetAttribute offset = syn.addAttribute(OffsetAttribute.class);
        // this method advance the stream to the next token
        if (syn.incrementToken()) {
          // read the first token
          sb.append(term.toString());
          // print start/end offsets and term
          System.out.println(offset.startOffset() + " " + offset.endOffset() + " " + term.toString());
          // again, advance the stream to the next token and prints 
          while (syn.incrementToken()) {
            System.out.println(offset.startOffset() + " " + offset.endOffset() + " " + term.toString());
            sb.append(" ");
            sb.append(term.toString());
          }
        }
        // called by the consumer after the last token has been consumed
        syn.end();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(sb.toString());
    return sb.toString();
  }

}
