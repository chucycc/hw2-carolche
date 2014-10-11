package uima;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;

import tool.Span;
import carolche.types.Annotation_gene;
import carolche.types.Sentence;

/**
 * The CASconsumer prints out the gene annotations to a file.
 * 
 * @author Carol Cheng
 * 
 */
public class CASConsumer extends CasConsumer_ImplBase {

  private File file;

  private FileWriter fw;

  private BufferedWriter bw;

  /**
   * This method initialize the file which will be written.
   * 
   * @throws ResourceInitializationException
   */
  @Override
  public void initialize() throws ResourceInitializationException {
    // Set up file path
    file = new File(((String) getUimaContext().getConfigParameterValue("Output")).trim());
    // If file doesn't exist, create a new file
    try {
      if (!file.exists()) {
        file.createNewFile();
        fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method extract gene name entities from CAS and print them to output file in a specified
   * format.
   * 
   * @throws ResourceInitializationException
   */
  @Override
  public void processCas(CAS aCAS) throws ResourceProcessException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }
    // Get the original sentence from jcas to use Span object to calculate a new span.
    FSIterator<Annotation> stIter = jcas.getAnnotationIndex(Sentence.type).iterator();
    Sentence s = (Sentence) stIter.next();
    // Get the gene annotations from jcas
    FSIterator<Annotation> iter = jcas.getAnnotationIndex(Annotation_gene.type).iterator();
    while (iter.hasNext()) {
      Annotation_gene a = (Annotation_gene) iter.next();
      // Span objects save the start and end point of a gene.
      Span oriSpan = new Span(a.getBegin(), a.getEnd(), a.getGeneName());
      // The method ignoreSpace calculates the span of a gene without white spaces.
      Span newSpan = oriSpan.ignoreSpace(s.getText());
      String result = a.getId() + "|" + newSpan.getBegin() + " " + newSpan.getEnd() + "|"
              + a.getGeneName();
      try {
        bw.write(result);
        bw.newLine();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

  /**
   * This method closes the file.
   */
  @Override
  public void destroy() {
    super.destroy();
    try {
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
