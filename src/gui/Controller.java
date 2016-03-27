package gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import expression.Expression;
import expression.metasentence.MetaSentence;
import expression.metasentence.MetaSentenceReader;
import metareasoner.MetaProver;

public class Controller {
	ByteArrayOutputStream baos;
	
	public Controller() {
		baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
	}
	
	public String MetaProve(List<String> premises, String goal) {
        ArrayList<MetaSentence> p = new ArrayList<MetaSentence>() {{
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
		return toReturn;
	}
}
