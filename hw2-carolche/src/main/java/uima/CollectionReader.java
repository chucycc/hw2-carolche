package uima;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

/**
 * A simple collection reader that reads documents from a file. It can be configured with the
 * following parameters:
 * <ul>
 * <li><code>InputFile</code> - path to the input file</li>
 * </ul>
 * 
 * 
 */
public class CollectionReader extends CollectionReader_ImplBase {

  private File file;

  // The arrayList is used to save input lines from input file
  private ArrayList<String> lines = new ArrayList<String>();

  private int currentLine;

  @Override
  public void initialize() throws ResourceInitializationException {
    // Set up file path
    file = new File(((String) getConfigParameterValue("InputFile")).trim());

    // Read input sentences line by line and save them to a arrayList.
    try {
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while ((line = br.readLine()) != null) {
        lines.add(line);
      }
      fr.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    currentLine = 0;
  }

  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }
    // Retrieve a line from arraryList
    String temp = lines.get(currentLine++);
    // put the contents into CAS
    jcas.setDocumentText(temp);
  }

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return currentLine < lines.size();
  }

  @Override
  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(currentLine, lines.size(), Progress.ENTITIES) };
  }

  @Override
  public void close() throws IOException {
  }

}
