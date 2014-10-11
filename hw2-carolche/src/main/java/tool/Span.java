package tool;

/**
 * Span class is used to save the span of a gene in the sentences. It also provides a method to
 * calculate a new span which ignores white spaces. The class can be saved in hashMap as a key.
 * 
 * @author Carol Cheng
 * 
 */
public class Span {
  private int begin;

  private int end;

  private String geneName;

  /**
   * @param st
   *          Start point of the gene
   * @param ed
   *          End point of the gene
   */
  public Span(int st, int ed, String name) {
    begin = st;
    end = ed;
    geneName = name;
  }

  /**
   * Return beginning of a gene in the sentence
   */
  public int getBegin() {
    return begin;
  }

  /**
   * Return ending of a gene in the sentence
   */
  public int getEnd() {
    return end;
  }

  /**
   * Return name of the gene
   */
  public String getGeneName() {
    return geneName;
  }

  /**
   * Calculate a new span by ignoring white spaces of the sentence
   * 
   * @param text
   *          The original sentence without the identifier
   * @return Span A new span which ignores white spaces of the sentence
   */
  public Span ignoreSpace(String text) {
    int startCount = 0;
    int endCount = 0;
    for (int i = 0; i < end; i++) {
      if (i < begin) {
        if (text.charAt(i) != ' ') {
          startCount++;
          endCount++;
        }
      } else {
        if (text.charAt(i) != ' ') {
          endCount++;
        }
      }
    }
    // The ending is the previous index of the ending -> endCount - 1
    return new Span(startCount, endCount - 1, geneName);
  }

  /**
   * Return hashCode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + end;
    result = prime * result + begin;
    return result;
  }

  /**
   * Return whether the Span object equals another object
   * 
   * @param obj
   *          Another object to be compared
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Span other = (Span) obj;
    if (end != other.end)
      return false;
    if (begin != other.begin)
      return false;
    return true;
  }
}