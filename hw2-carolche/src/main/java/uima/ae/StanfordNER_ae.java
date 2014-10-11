package uima.ae;

import java.util.Iterator;
import java.util.Map;

import ner.PosTagNamedEntityRecognizer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import carolche.types.Sentence;
import carolche.types.Annotation_stanford;

/**
 * StanfordNER annotator is not used in this aggregate analysis engines due to its low performance.
 * The annotator uses StanfordCoreNLP to identify name tags in a sentence and save the results in
 * Annotation_stanford.
 * 
 * @author Carol Cheng
 * 
 */
public class StanfordNER_ae extends JCasAnnotator_ImplBase {
  PosTagNamedEntityRecognizer stanfordNER = null;

  /**
   * The method initialize PosTagNamedEntityRecognizer to perform name entity recognition.
   * 
   * @throws ResourceInitializationException
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    try {
      stanfordNER = new PosTagNamedEntityRecognizer();
    } catch (ResourceInitializationException e) {
      e.printStackTrace();
    }
    System.out.println("StanfordNER annotator is ready...:D");
  }

  /**
   * The method processes sentences to create Annotation_stanford and put the annotations into JCAS.
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
      // Use a Map, begin2end to save the spans return by stnfordNER
      Map<Integer, Integer> begin2end = stanfordNER.getGeneSpans(text);
      // Iterate begin2end to get the mentions and save them to Annotation_stanford
      Iterator<Map.Entry<Integer, Integer>> mapIter = begin2end.entrySet().iterator();
      while (mapIter.hasNext()) {
        Map.Entry<Integer, Integer> span = mapIter.next();
        int begin = span.getKey();
        int end = span.getValue();
        Annotation_stanford a = new Annotation_stanford(aJCas);
        a.setBegin(begin);
        a.setEnd(end);
        a.setGeneName(text.substring(begin, end));
        a.setId(id);
        a.setCasProcessorId("stanfordNER");
        a.setConfidence(0.1);
        a.addToIndexes();
      }
    }
  }

}
