
package fintech.dowjones.model.search.name;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for match-typeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="match-typeType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="linguistic-variation" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="non-linguistic-variation" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="structural-variation" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "match-typeType", propOrder = {
    "value"
})
@Data
public class MatchType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "linguistic-variation")
    protected String linguisticVariation;
    @XmlAttribute(name = "non-linguistic-variation")
    protected String nonLinguisticVariation;
    @XmlAttribute(name = "structural-variation")
    protected String structuralVariation;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the linguisticVariation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinguisticVariation() {
        return linguisticVariation;
    }

    /**
     * Sets the value of the linguisticVariation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinguisticVariation(String value) {
        this.linguisticVariation = value;
    }

    /**
     * Gets the value of the nonLinguisticVariation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNonLinguisticVariation() {
        return nonLinguisticVariation;
    }

    /**
     * Sets the value of the nonLinguisticVariation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNonLinguisticVariation(String value) {
        this.nonLinguisticVariation = value;
    }

    /**
     * Gets the value of the structuralVariation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStructuralVariation() {
        return structuralVariation;
    }

    /**
     * Sets the value of the structuralVariation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStructuralVariation(String value) {
        this.structuralVariation = value;
    }

}
