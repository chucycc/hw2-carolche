
/* First created by JCasGen Sun Oct 05 13:21:54 EDT 2014 */
package carolche.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;

/** Sentence type is used for parsing input lines into identifier and text.
 * Updated by JCasGen Fri Oct 10 14:15:02 EDT 2014
 * XML source: /home/carol/git/hw2-carolche/hw2-carolche/src/main/resources/hw2-carolche-aae.xml
 * @generated */
public class Sentence extends Annotation {
  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public final static int typeIndexID = JCasRegistry.register(Sentence.class);

  /**
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public final static int type = typeIndexID;

  /**
   * @generated
   * @return index of the type
   */
  @Override
  public int getTypeIndexID() {return typeIndexID;}
 
  /**
   * Never called. Disable default constructor
   * 
   * @generated
   */
  protected Sentence() {/* intentionally empty block */}
    
  /**
   * Internal - constructor used by generator
   * 
   * @generated
   * @param addr
   *          low level Feature Structure reference
   * @param type
   *          the type of this Feature Structure
   */
  public Sentence(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /**
   * @generated
   * @param jcas
   *          JCas to which this Feature Structure belongs
   */
  public Sentence(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /**
   * @generated
   * @param jcas
   *          JCas to which this Feature Structure belongs
   * @param begin
   *          offset to the begin spot in the SofA
   * @param end
   *          offset to the end spot in the SofA
   */
  public Sentence(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc --> Write your own initialization here <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/* default - does nothing empty block */
  }

  // *--------------*
  // * Feature: id

  /**
   * getter for id - gets The sentence identifier of input data. Different genes from the same
   * sentence will have the same identifier.
   * 
   * @generated
   * @return value of the feature
   */
  public String getId() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "carolche.types.Sentence");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_id);}
    
  /**
   * setter for id - sets The sentence identifier of input data. Different genes from the same
   * sentence will have the same identifier.
   * 
   * @generated
   * @param v
   *          value to set into the feature
   */
  public void setId(String v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "carolche.types.Sentence");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_id, v);}    
   
    
  // *--------------*
  // * Feature: text

  /**
   * getter for text - gets The text of the input data. It would be some contents which may contain
   * different gene names.
   * 
   * @generated
   * @return value of the feature
   */
  public String getText() {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "carolche.types.Sentence");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_text);}
    
  /**
   * setter for text - sets The text of the input data. It would be some contents which may contain
   * different gene names.
   * 
   * @generated
   * @param v
   *          value to set into the feature
   */
  public void setText(String v) {
    if (Sentence_Type.featOkTst && ((Sentence_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "carolche.types.Sentence");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sentence_Type)jcasType).casFeatCode_text, v);}    
  }
