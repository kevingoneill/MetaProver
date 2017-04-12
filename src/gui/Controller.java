package gui;

import expression.Expression;
import expression.metasentence.MetaSentence;
import expression.metasentence.MetaSentenceReader;
import expression.metasentence.TruthAssignmentVar;
import expression.sentence.Sentence;
import gui.truthtreevisualization.TruthTree;
import logicalreasoner.prover.Prover;
import metareasoner.MetaProver;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

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
      }
    };

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
    Map<String, TruthAssignmentVar> truthAssignments = prover.getTruthAssignments();
    Map<String, TruthTree> trees = new LinkedHashMap<String, TruthTree>();
    truthAssignments.forEach((n, ta) -> {
    	TruthTree tt = ta.getTruthAssignment().makeTruthTree();
    	tt.placeStatements();
    	trees.put(n, tt);
    });
    return new ProofInfo(text, trees);
  }

  public ProofInfo TruthFunctionalProve(List<String> premises, String goal) {
    System.setOut(new PrintStream(baos));
    Set<Sentence> p = new HashSet<Sentence>() {
      private static final long serialVersionUID = 8376931001151027146L;

      {
        premises.forEach(premise -> this.add(Sentence.makeSentence(premise)));
      }
    };

    Prover prover = new Prover(p, Sentence.makeSentence(goal), true);
    prover.run();

    String text = baos.toString();
    baos.reset();
    System.out.flush();
    System.setOut(System.out);
    TruthTree tree = prover.getTruthAssignment().makeTruthTree();
    tree.setInferences(prover.getInferenceList());
    tree.placeStatements();
    
    return new ProofInfo(text, new LinkedHashMap<String, TruthTree>() {{put("Truth Tree", tree);}});
  }
}

class ProofInfo {
  String text;
  Map<String, TruthTree> trees;

  public ProofInfo(String text, Map<String, TruthTree> trees) {
    this.text = text;
    this.trees = trees;
  }
}
