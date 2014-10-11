package uima.ae;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import carolche.types.Sentence;
import carolche.types.Annotation_abner;
import abner.Tagger;

/**
 * Abner annotator uses a model trained on the NLPBA corpora to identify gene or protein names in
 * the sentence. It will save the identified mentions as Annotation_abner type.
 * 
 * @author Carol Cheng
 * 
 */
public class Abner_ae extends JCasAnnotator_ImplBase {
  Tagger abNER = null;

  /**
   * The method initialize Abner Tagger to perform name entity recognition.
   * 
   * @throws ResourceInitializationException
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    abNER = new Tagger();
    System.out.println("Abner annotator is ready...:D");
  }

  /**
   * The method processes sentences to create Annotation_abner and put the annotations into JCAS.
   * 
   * @throws AnalysisEngineProcessException
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // Retrieve sentences from JCAS
    FSIterator<Annotation> stIter = aJCas.getAnnotationIndex(Sentence.type).iterator();
    while (stIter.hasNext()) {
      Sentence s = (Sentence) stIter.next();
      String id = s.getId();
      String text = s.getText();
      // Call Abner Tagger to perform name entity recognition.
      // The identified results will be saved in 2-D String array.
      String[][] entities = abNER.getEntities(text);
      // Abner doesn't provide the span of mentions, so create the index variable to record the
      // index of each identified mention.
      int index = 0;
      // entities[0][i] stores the entities and entities[1][i] stores the mentions' tag.
      for (int i = 0; i < entities[0].length; i++) {
        // Only consider DNA and protein as potential gene mention.
        if (entities[1][i].equalsIgnoreCase("DNA") || entities[1][i].equalsIgnoreCase("protein")) {
          // Use indexOf method to find the index of the entity.
          // It will always start from the index we recorded.
          int begin = text.indexOf(entities[0][i], index);
          int end = begin + entities[0][i].length();
          // Make sure next iteration, index is behind the mention.
          index += entities[0][i].length();
          // Create Annotation_abner and save the identified mention.
          Annotation_abner a = new Annotation_abner(aJCas);
          a.setCasProcessorId("abNER");
          // Since the precision of Abner is not high and it doesn't provide confidence,
          // I use 0.2 as its default confidence.
          a.setConfidence(0.2);
          a.setBegin(begin);
          a.setEnd(end);
          a.setGeneName(entities[0][i]);
          a.setId(id);
          a.addToIndexes();
        }
      }
    }
  }

}
