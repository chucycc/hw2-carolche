package uima.ae;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import carolche.types.Sentence;

/**
 * Sentence annotator separates identifier and text of a input line and save them into Sentence
 * annotations.
 * 
 * @author Carol Cheng
 * 
 */
public class Sentence_ae extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // Get a line of input data and build a Sentence type annotation.
    String line = aJCas.getDocumentText();
    Sentence sentence = new Sentence(aJCas);
    // Split a line into the parts of identifier and text.
    String[] parts = line.split(" ", 2);
    sentence.setBegin(0);
    sentence.setEnd(line.length());
    sentence.setId(parts[0]);
    sentence.setText(parts[1]);
    sentence.addToIndexes();
  }

}
