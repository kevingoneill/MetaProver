package gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import expression.Expression;
import expression.metasentence.MetaSentence;
import expression.metasentence.MetaSentenceReader;
import expression.sentence.Sentence;
import expression.sentence.SentenceReader;
import gui.truthtreevisualization.TruthTree;
import logicalreasoner.prover.SemanticProver;
import metareasoner.MetaProver;

public class Controller {
	ByteArrayOutputStream baos;
	
	public Controller() {
		baos = new ByteArrayOutputStream();
		
	}
	
	public ProofInfo MetaProve(List<String> premises, String goal) {
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
		
		String text = baos.toString();
		baos.reset();
		System.out.flush();
		System.setOut(System.out);
		return new ProofInfo(text, prover.getTruthTrees());
	}
	
	public ProofInfo TruthFunctionalProve(List<String> premises, String goal) {
		System.setOut(new PrintStream(baos));
		Set<Sentence> p = new HashSet<Sentence>() {
			private static final long serialVersionUID = 8376931001151027146L;

		{
            premises.forEach(premise -> this.add(SentenceReader.parse(premise)));
        }};
        
        SemanticProver prover = new SemanticProver(p, SentenceReader.parse(goal));
        prover.run();
        
        String text = baos.toString();
        baos.reset();
        System.out.flush();
        System.setOut(System.out);
        return new ProofInfo(text, new LinkedHashMap<Integer, List<TruthTree>>() {{
        	put(-1, new ArrayList<TruthTree>() {{
        		add(prover.getTruthAssignment().makeTruthTree());
        	}});
        }});
	}
}

class ProofInfo {
	String text;
	Map<Integer, List<TruthTree>> trees;
	
	public ProofInfo(String text, Map<Integer, List<TruthTree>> trees) {
		this.text = text;
		this.trees = trees;
	}
}
