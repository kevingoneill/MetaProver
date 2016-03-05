package prover;

import sentence.Sentence;
import sentence.SentenceReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kevin on 3/4/16.
 */
public class SemanticProverMain {

    /**
     * Run the prover given premises and interests
     *
     * args[1] - premises txt file (one premise per line)
     * ars[2] - interests txt file (one interest per line)
     *
     * @param args input files for reasoning
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(usage(args[0]));
            return;
        }

        Set<Sentence> premises, interests;

        try {
            premises = readSentences(args[0]);
        } catch (IOException ioe){
            System.out.println("Exception while reading premises: \n");
            ioe.printStackTrace();
            return;
        }

        try {
            interests = readSentences(args[1]);
        } catch (IOException ioe){
            System.out.println("Exception while reading premises: \n");
            ioe.printStackTrace();
            return;
        }

        SemanticProver prover = new SemanticProver(premises, interests);
        prover.run();
    }

    private static String usage(String arg0) {
        return "usage: java " + arg0 + " <premisesFile> <interestsFile>";
    }

    private static Set<Sentence> readSentences(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        Set<Sentence> sentences = new HashSet<>();

        while ((line = reader.readLine()) != null) {
            sentences.add(SentenceReader.parse(line));
        }
        return sentences;
    }
}
