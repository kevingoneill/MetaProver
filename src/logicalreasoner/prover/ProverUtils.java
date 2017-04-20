package logicalreasoner.prover;

import expression.sentence.Sentence;
import expression.sentence.SentenceReader;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The ProverUtils interface is a way to access the functionality of a
 * prover using various methods for specific tasks. For instance, a single
 * prover can be configured to test for argument validity, consistency, equivalence, and etc.
 */
public class ProverUtils {

  /**
   * Test whether the set of premises logically implies the conclusion
   * under propositional logic
   *
   * @param premises   the set of assumptions
   * @param conclusion the goal of the proof
   * @return true, if the argument is valid, false otherwise
   */
  public static boolean isPropositionallyValid(Set<Sentence> premises, Sentence conclusion) {
    if (!premises.stream().anyMatch(Sentence::isPropositional) || !conclusion.isPropositional())
      throw new RuntimeException("Cannot run propositional proof over a quantified statement.");
    Prover prover = new Prover(premises, conclusion, false);
    prover.run();
    return !prover.isConsistent();
  }

  /**
   * Test whether the set of premises logically implies the conclusion
   * under propositional logic
   *
   * @param premises   the set of assumptions
   * @param conclusion the goal of the proof
   * @return true, if the argument is valid, false otherwise
   */
  public static boolean isPropositionallyValid(Set<String> premises, String conclusion) {
    SentenceReader reader = new SentenceReader();
    Set<Sentence> s = premises.stream().map(reader::parse).collect(Collectors.toSet());
    Sentence c = reader.parse(conclusion);
    return isPropositionallyValid(s, c);
  }

  /**
   * Test to see if all of the sentences can be true at the same time
   * under propositional logic
   *
   * @param sentences the set of sentences to test consistency for
   * @return true if the sentences are consistent, false otherwise
   */
  public static boolean isPropositionallyConsistent(Set<Sentence> sentences) {
    if (!sentences.stream().allMatch(Sentence::isPropositional))
      throw new RuntimeException("Cannot run propositional proof over a quantified statement.");
    Prover prover = new Prover(sentences, false);
    prover.run();
    return prover.isConsistent();
  }

  /**
   * Test to see if it is impossible to satisfy all sentences simultaneously
   * under propositional logic
   *
   * @param sentences the sentences to test
   * @return true if the statements imply a logical contradiction
   */
  public static boolean isPropositionallyContradictory(Set<Sentence> sentences) {
    return !isPropositionallyConsistent(sentences);
  }

  /**
   * Test to see if sentence is a tautology
   *
   * @param sentence the sentence to test
   * @return true if sentence is a tautology, false otherwise
   */
  public static boolean isPropositionalTautology(Sentence sentence) {
    if (!sentence.isPropositional())
      throw new RuntimeException("Cannot run propositional proof over a quantified statement.");
    Prover prover = new Prover(new HashSet<>(), sentence, false);
    prover.run();
    return !prover.isConsistent();
  }

  /**
   * Test to see if sentence is a contradiction
   *
   * @param sentence the sentence to test
   * @return true if sentence is a contradiction, false otherwise
   */
  public static boolean isPropositionalContradiction(Sentence sentence) {
    if (!sentence.isPropositional())
      throw new RuntimeException("Cannot run propositional proof over a quantified statement.");
    HashSet<Sentence> s = new HashSet<>();
    s.add(sentence);
    Prover prover = new Prover(s, Collections.emptySet(), false);
    prover.run();
    return !prover.isConsistent();
  }

  /**
   * Test whether s1 propositionally implies s2
   *
   * @param s1 the antecedent
   * @param s2 the consequent
   * @return true if s1 implies s2, false otherwise
   */
  public static boolean propositionallyImplies(Sentence s1, Sentence s2) {
    HashSet<Sentence> s = new HashSet<>();
    s.add(s1);
    return isPropositionallyValid(s, s2);
  }

  /**
   * Test whether s1 and s2 are propositionally equivalent
   *
   * @param s1 the first sentence
   * @param s2 the second sentence
   * @return true if s1 and s2 are equivalent, false otherwise
   */
  public static boolean isPropositionallyEquivalent(Sentence s1, Sentence s2) {
    return propositionallyImplies(s1, s2) && propositionallyImplies(s2, s1);
  }

  /**
   * Test whether s1 are propositionally contrary, meaning that
   * s1 and s2 cannot both be true (although both could be false).
   * In other words, if s1 is true then s2 is false, and if s2 is true,
   * then s1 is false
   *
   * @param s1 the first sentence
   * @param s2 the second sentence
   * @return true if s1 and s2 are contrary, false otherwise
   */
  public static boolean isPropositionallyContrary(Sentence s1, Sentence s2) {
    HashSet<Sentence> s = new HashSet<>();
    s.add(s1);
    s.add(s2);
    return !isPropositionallyConsistent(s);
  }

  /**
   * Test whether s1 are propositionally subcontrary, meaning that
   * s1 and s2 cannot both be false (although both could be true).
   * In other words, if s1 is false then s2 is true, and if s2 is false,
   * then s1 is true
   *
   * @param s1 the first sentence
   * @param s2 the second sentence
   * @return true if s1 and s2 are subcontrary, false otherwise
   */
  public static boolean isPropositionallySubcontrary(Sentence s1, Sentence s2) {
    if (!(s1.isPropositional() && s2.isPropositional()))
      throw new RuntimeException("Cannot run propositional over quantified sentences.");

    HashSet<Sentence> s = new HashSet<>();
    s.add(s1);
    s.add(s2);
    Prover prover = new Prover(Collections.emptySet(), s, false);
    prover.run();
    return !prover.isConsistent();
  }

  /**
   * Test whether the set of premises logically implies the conclusion
   * under first-order logic
   *
   * @param premises   the set of assumptions
   * @param conclusion the goal of the proof
   * @return true, if the argument is valid, false otherwise
   */
  public static boolean isFOLValid(Set<Sentence> premises, Sentence conclusion) {
    Prover prover = new FOLProver(premises, conclusion, false);
    prover.run();
    return !prover.isConsistent();
  }

  /**
   * Test whether the set of premises logically implies the conclusion
   * under first-order logic
   *
   * @param premises   the set of assumptions
   * @param conclusion the goal of the proof
   * @return true, if the argument is valid, false otherwise
   */
  public static boolean isFOLValid(Set<String> premises, String conclusion) {
    SentenceReader reader = new SentenceReader();
    Set<Sentence> s = premises.stream().map(reader::parse).collect(Collectors.toSet());
    Sentence c = reader.parse(conclusion);
    return isFOLValid(s, c);
  }

  /**
   * Test to see if all of the sentences can be true at the same time
   * under first-order logic
   *
   * @param sentences the set of sentences to test consistency for
   * @return true if the sentences are consistent, false otherwise
   */
  public static boolean isFOLConsistent(Set<Sentence> sentences) {
    Prover prover = new FOLProver(sentences, false);
    prover.run();
    return prover.isConsistent();
  }

  /**
   * Test to see if it is impossible to satisfy all sentences simultaneously
   * under first-order logic
   *
   * @param sentences the sentences to test
   * @return true if the statements imply a logical contradiction
   */
  public static boolean isFOLContradictory(Set<Sentence> sentences) {
    return !isFOLConsistent(sentences);
  }

  /**
   * Test to see if sentence is a tautology
   *
   * @param sentence the sentence to test
   * @return true if sentence is a tautology, false otherwise
   */
  public static boolean isFOLTautology(Sentence sentence) {
    Prover prover = new FOLProver(new HashSet<>(), sentence, false);
    prover.run();
    return !prover.isConsistent();
  }

  /**
   * Test to see if sentence is a contradiction under first-order logic
   *
   * @param sentence the sentence to test
   * @return true if sentence is a contradiction, false otherwise
   */
  public static boolean isFOLContradiction(Sentence sentence) {
    HashSet<Sentence> s = new HashSet<>();
    s.add(sentence);
    Prover prover = new FOLProver(s, Collections.emptySet(), false);
    prover.run();
    return !prover.isConsistent();
  }

  /**
   * Test whether s1 implies s2 under first-order logic
   *
   * @param s1 the antecedent
   * @param s2 the consequent
   * @return true if s1 implies s2, false otherwise
   */
  public static boolean FOLImplies(Sentence s1, Sentence s2) {
    HashSet<Sentence> s = new HashSet<>();
    s.add(s1);
    return isFOLValid(s, s2);
  }

  /**
   * Test whether s1 and s2 are equivalent under first-order logic
   *
   * @param s1 the first sentence
   * @param s2 the second sentence
   * @return true if s1 and s2 are equivalent, false otherwise
   */
  public static boolean isFOLEquivalent(Sentence s1, Sentence s2) {
    return FOLImplies(s1, s2) && FOLImplies(s2, s1);
  }

  /**
   * Test whether s1 are contrary under first-order logic, meaning that
   * s1 and s2 cannot both be true (although both could be false).
   * In other words, if s1 is true then s2 is false, and if s2 is true,
   * then s1 is false
   *
   * @param s1 the first sentence
   * @param s2 the second sentence
   * @return true if s1 and s2 are contrary, false otherwise
   */
  public static boolean isFOLContrary(Sentence s1, Sentence s2) {
    HashSet<Sentence> s = new HashSet<>();
    s.add(s1);
    s.add(s2);
    return !isPropositionallyConsistent(s);
  }

  /**
   * Test whether s1 are subcontrary under first-order logic, meaning that
   * s1 and s2 cannot both be false (although both could be true).
   * In other words, if s1 is false then s2 is true, and if s2 is false,
   * then s1 is true
   *
   * @param s1 the first sentence
   * @param s2 the second sentence
   * @return true if s1 and s2 are subcontrary, false otherwise
   */
  public static boolean isFOLSubcontrary(Sentence s1, Sentence s2) {
    HashSet<Sentence> s = new HashSet<>();
    s.add(s1);
    s.add(s2);
    Prover prover = new FOLProver(Collections.emptySet(), s, false);
    prover.run();
    return !prover.isConsistent();
  }
}
