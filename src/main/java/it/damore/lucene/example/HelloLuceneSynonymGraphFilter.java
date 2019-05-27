package it.damore.lucene.example;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoader;

public class HelloLuceneSynonymGraphFilter {

  public static void main(String[] args) throws IOException
  {
    SynonymGraphFilterFactory sgf = init();
    applySynonyms(sgf, "testo di prova");
    applySynonyms(sgf, "incremento di vendita in Italia");
    applySynonyms(sgf, "United States of America");

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
    ResourceLoader rl = new FilesystemResourceLoader(Paths.get("."), HelloLuceneSynonymGraphFilter.class.getClassLoader());
    syf.inform(rl);
    return syf;
  }

  public static String applySynonyms(SynonymGraphFilterFactory localSyf, String input)
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
