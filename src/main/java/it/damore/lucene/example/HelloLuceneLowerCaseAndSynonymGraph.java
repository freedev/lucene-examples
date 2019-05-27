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

public class HelloLuceneLowerCaseAndSynonymGraph {

    private static SynonymGraphFilterFactory localSyf;
    private static LowerCaseFilterFactory lowerCaseFilterFactory;

    public static void main(String[] args) throws IOException {
        lowerCaseFilterFactory = getLowerCaseFilterFactory();
        localSyf = getSynonymGraphFilterFactory();
        applyLuceneLowerCaseAndSynonymsGraph("DVD-RW 4,7 GB - 120 Min 1-2 X Jewel Case Confezione 5 Pezzi");
        applyLuceneLowerCaseAndSynonymsGraph("DVD-RW 4,7 GB - 120 Min 1-2 X Jewel Case Confezione 5 Pezzi");
    }

    public static LowerCaseFilterFactory getLowerCaseFilterFactory()
    {
        // create the LowerCaseFilterFactory
        LowerCaseFilterFactory syf = new LowerCaseFilterFactory(Collections.emptyMap());
        return syf;
    }

    public static SynonymGraphFilterFactory getSynonymGraphFilterFactory() throws IOException {
        // prepare the list of parameters
        Map<String, String> args = new HashMap<>();
        args.put("synonyms", "synonyms.txt");
        args.put("ignoreCase", Boolean.toString(true));
        args.put("expand", Boolean.toString(true));
        // create the SynonymGraphFilterFactory and the ResourceLoader
        SynonymGraphFilterFactory syf = new SynonymGraphFilterFactory(args);
        ResourceLoader rl = new FilesystemResourceLoader(Paths.get("."), HelloLuceneLowerCaseAndSynonymGraph.class
                .getClassLoader());
        syf.inform(rl);
        return syf;
    }

    public static String applyLuceneLowerCaseAndSynonymsGraph(String input) {
        StringBuilder sb = new StringBuilder();

        // Specify the Whitespace Tokenizer divides text at whitespace characters
        try (Tokenizer wt = new WhitespaceTokenizer()) {
            // Set the input string as Reader
            wt.setReader(new StringReader(input));

            try (TokenStream lowf = lowerCaseFilterFactory.create(wt)) {
                try (TokenStream synf = localSyf.create(lowf)) {

                    // This method has to called by a consumer before it begins consumption
                    synf.reset();
                    // Define a couple of attributes
                    CharTermAttribute term = synf.addAttribute(CharTermAttribute.class);
                    OffsetAttribute offset = synf.addAttribute(OffsetAttribute.class);
                    // this method advance the stream to the next token
                    if (synf.incrementToken()) {
                        // read the first token
                        sb.append(term.toString());
                        // print start/end offsets and term
                        System.out.println(offset.startOffset() + " " + offset.endOffset() + " " + term.toString());
                        // again, advance the stream to the next token and prints
                        while (synf.incrementToken()) {
                            System.out.println(offset.startOffset() + " " + offset.endOffset() + " " + term.toString());
                            sb.append(" ");
                            sb.append(term.toString());
                        }
                    }
                    // called by the consumer after the last token has been consumed
                    synf.end();
                }
                lowf.end();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

}
