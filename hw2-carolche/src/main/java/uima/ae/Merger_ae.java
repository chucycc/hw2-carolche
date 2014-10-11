package uima.ae;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import carolche.types.Annotation_nemine;
import carolche.types.Sentence;
import carolche.types.Annotation_abner;
import carolche.types.Annotation_gene;
import carolche.types.Annotation_lingpipe;
import tool.Span;

/**
 * Merger annotator evaluates and combines the gene mentions found by Abner and Lingpipe. It will
 * save the identified mentions as Annotation_gene type. Annotation_gene is the type which CAS
 * consumer will print out as output.
 * 
 * @author Carol Cheng
 * 
 */
public class Merger_ae extends JCasAnnotator_ImplBase {

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    System.out.println("Merger annotator is ready...:D");
  }

  /**
   * The method processes annotations produced by Lingpipe and Abner annotators. Only when
   * confidence of the annotation is higher than 0.5, the mention will be saved as Annotation_gene,
   * which is the final result of the gene name entity recognition.
   * 
   * @throws AnalysisEngineProcessException
   * 
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // Use a HashMap, index, to re-organize the identified annotations from Lingpipe and Abner.
    // The keys and values of the HashMap are span and confidence of gene mentions.
    Map<Span, Double> index = new HashMap<Span, Double>();
    // Retrieve sentence annotations.
    FSIterator<org.apache.uima.jcas.tcas.Annotation> stIter = aJCas.getAnnotationIndex(
            Sentence.type).iterator();
    Sentence s = (Sentence) stIter.next();
    // Retrieve Lingpipe annotations and save them into index.
    FSIterator<Annotation> linIter = aJCas.getAnnotationIndex(
            Annotation_lingpipe.type).iterator();
    while (linIter.hasNext()) {
      Annotation_lingpipe a = (Annotation_lingpipe) linIter.next();
      Span span = new Span(a.getBegin(), a.getEnd(), a.getGeneName());
      Double conf = a.getConfidence();
      index.put(span, conf);
    }
    // Retrieve Abner annotations and save them into index.
    FSIterator<Annotation> abIter = aJCas.getAnnotationIndex(
            Annotation_abner.type).iterator();
    while (abIter.hasNext()) {
      Annotation_abner a = (Annotation_abner) abIter.next();
      Span span = new Span(a.getBegin(), a.getEnd(), a.getGeneName());
      Double conf = a.getConfidence();
      // If the key already exists, update the confidence.
      if (!index.containsKey(span)) {
        index.put(span, conf);
      } else {
        Double value = index.get(span);
        index.put(span, value + conf);
      }
    }
    // Nemine annotator is no more used due to the slow retrieving results from the Internet.
    
/*    FSIterator<org.apache.uima.jcas.tcas.Annotation> neIter = aJCas.getAnnotationIndex(
            Annotation_nemine.type).iterator();
    while (neIter.hasNext()) {
      Annotation_nemine a = (Annotation_nemine) neIter.next();
      Span span = new Span(a.getBegin(), a.getEnd(), a.getGeneName());
      Double conf = a.getConfidence();
      if (!index.containsKey(span)) {
        index.put(span, conf);
      } else {
        Double value = index.get(span);
        index.put(span, value + conf);
      }
    }*/
    
    // Iterate the index to save the mentions whose confidence is higher than 0.55 as gene
    // annotations.
    Iterator<Map.Entry<Span, Double>> iter = index.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<Span, Double> candidate = iter.next();
      Double value = candidate.getValue();
      Span key = candidate.getKey();
      if (value >= 0.5) {
        Annotation_gene g = new Annotation_gene(aJCas);
        g.setBegin(key.getBegin());
        g.setEnd(key.getEnd());
        g.setGeneName(key.getGeneName());
        g.setId(s.getId());
        g.addToIndexes();
      }
    }
  }
}
