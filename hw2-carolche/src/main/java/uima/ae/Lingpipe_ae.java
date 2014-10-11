package uima.ae;

import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.ConfidenceChunker;
import com.aliasi.util.AbstractExternalizable;

import carolche.types.Sentence;
import carolche.types.Annotation_lingpipe;

/**
 * Lingpipe annotator uses a pre-trained model file to identify gene mentions in the sentence. It
 * will save the identified mentions as Annotation_lingpipe type.
 * 
 * @author Carol Cheng
 * 
 */
public class Lingpipe_ae extends JCasAnnotator_ImplBase {
  ConfidenceChunker cchunker = null;

  // This field is used by ConfidenceChunker.
  // It decides the maximum of the number of n-best chunks returned.
  static final int MAX_N_BEST_CHUNKS = 15;

  /**
   * The method initializes Lingpipe annotator by reading model file.
   * 
   * @throws ResourceInitializationException
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    // Read the file path of a trained model from configuration parameters.
    String modelFile = (String) aContext.getConfigParameterValue("modelFile");
    try {
      cchunker = (ConfidenceChunker) AbstractExternalizable.readResourceObject(modelFile);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Lingpipe annotator is ready...:D");
  }

  /**
   * The method processes sentences to create Annotation_lingpipe and put annotations into JCAS.
   * 
   * @throws AnalysisEngineProcessException
   * 
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // Retrieve sentences from JCAS
    FSIterator<Annotation> stIter = aJCas.getAnnotationIndex(Sentence.type).iterator();
    while (stIter.hasNext()) {
      Sentence s = (Sentence) stIter.next();
      String id = s.getId();
      String text = s.getText();
      // Call ConfidenceChunker to perform n-best name entity recognition.
      char[] cs = text.toCharArray();
      Iterator<Chunk> it = cchunker.nBestChunks(cs, 0, cs.length, MAX_N_BEST_CHUNKS);
      while (it.hasNext()) {
        Chunk chunk = it.next();
        // Compute the confidence by exponentiating the log (base 2) value
        double conf = Math.pow(2.0, chunk.score());
        int begin = chunk.start();
        int end = chunk.end();
        // When the chunk's confidence is larger than 0.3 and the chunk is not a single letter or
        // when the chunk has two words and confidence is larger than 0.9, 
        // create an Annotation_lingpipe and save the identified mention.
        if ((conf > 0.3 && end - begin > 2) || (conf > 0.9 && end - begin > 1)) {
          Annotation_lingpipe a = new Annotation_lingpipe(aJCas);
          a.setBegin(begin);
          a.setEnd(end);
          a.setGeneName(text.substring(begin, end));
          a.setId(id);
          a.setCasProcessorId("lingPipeNER");
          a.setConfidence(conf);
          a.addToIndexes();
        }
      }

    }
  }

}
