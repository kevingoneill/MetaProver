package gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import expression.Expression;
import expression.metasentence.MetaSentence;
import expression.metasentence.MetaSentenceReader;
import expression.sentence.Sentence;
import expression.sentence.SentenceReader;
import logicalreasoner.prover.SemanticProver;
import metareasoner.MetaProver;

public class Controller {
	ByteArrayOutputStream baos;
	
	public Controller() {
		baos = new ByteArrayOutputStream();
		
	}
	
	public String MetaProve(List<String> premises, String goal) {
		System.setOut(new PrintStream(baos));
        ArrayList<MetaSentence> p = new ArrayList<MetaSentence>() {
			private static final long serialVersionUID = 6077224465030638138L;
		{
            premises.forEach(premise -> {
                Expression e = MetaSentenceReader.parse(premise);
                if (e instanceof MetaSentence)
                    this.add((MetaSentence) e);
                else
                    throw new RuntimeException("Cannot input a Sentence into the MetaProver");
            });
        }};

        MetaSentence i = (MetaSentence) MetaSentenceReader.parse(goal);

        MetaProver prover = new MetaProver(p, i);
        prover.run();
        if (!prover.proofFound()) {
            throw new RuntimeException("Proof could not be found!");
        }
		
		String toReturn = baos.toString();
		System.out.flush();
		System.setOut(System.out);
		return toReturn;
	}
	
	public String TruthFunctionalProve(List<String> premises, String goal) {
		System.setOut(new PrintStream(baos));
		Set<Sentence> p = new HashSet<Sentence>() {
			private static final long serialVersionUID = 8376931001151027146L;

		{
            premises.forEach(premise -> this.add(SentenceReader.parse(premise)));
        }};
        
        SemanticProver prover = new SemanticProver(p, SentenceReader.parse(goal));
        prover.run();
        
        String toReturn = baos.toString();
        System.out.flush();
        System.setOut(System.out);
        return toReturn;
	}
}
