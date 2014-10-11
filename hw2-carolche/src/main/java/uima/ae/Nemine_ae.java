package uima.ae;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import carolche.types.Annotation_nemine;
import carolche.types.Sentence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Nemine annotator is not used in this aggregate analysis engines due to its low speed. The
 * annotator sends input texts to a web service, retrieve gene mentions and parse results into
 * Annotation_nemine. The type of annotations can also be taken by merger annotator. About nemine
 * service, see http://nactem7.mib.man.ac.uk/nemine/nemine.html
 * 
 * @author Carol Cheng
 * 
 */
public class Nemine_ae extends JCasAnnotator_ImplBase {
  // The url of the web service
  String nemine = "http://nactem7.mib.man.ac.uk/nemine/nemine.cgi?";

  String tag1 = "Input=";

  // Need to obtain a subscription ID for intensive use of Nemine
  String tag2 = "&SubscriptionID=carol";

  URL url = null;

  /**
   * The method processes sentences to create Annotation_nemine and put annotations into JCAS.
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
      // The input text have to be encoded before used as a part of url
      String input = null;
      try {
        input = URLEncoder.encode(text, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      // Combine string to a query url
      try {
        url = new URL(nemine + tag1 + input + tag2);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
      // Connect to the url and use BufferedReader to read the results line by line
      URLConnection uc = null;
      try {
        uc = url.openConnection();
        uc.setUseCaches(false);
        BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String aLine = null;
        // The previous 5 lines are not a part of results, so just skip them
        for (int i = 0; i < 5; i++) {
          aLine = br.readLine();
        }
        // Results would be in this format:
        // <Output>
        // <NE start="42" end="56" ne="5-nucleotidase" type="PROTEIN" prob="0.4379"/>
        // </Output>
        
        // Before we meet </Output>, keep read a line
        while (aLine.charAt(1) != '/') {
          // Split the line by " and save the attributes.
          String[] result = aLine.split("\"");
          int begin = Integer.parseInt(result[1]);
          int end = Integer.parseInt(result[3]);
          String geneName = result[5];
          double conf = Double.parseDouble(result[9]);
          // Create Annotation_nemine to save the mention
          Annotation_nemine a = new Annotation_nemine(aJCas);
          a.setBegin(begin);
          a.setEnd(end);
          a.setGeneName(geneName);
          a.setId(id);
          a.setCasProcessorId("nemine");
          a.setConfidence(conf);
          a.addToIndexes();
          
          aLine = br.readLine();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
