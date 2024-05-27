
package fintech.dowjones.model.search.name;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for headType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="headType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="total-hits" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="hits-from" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="hits-to" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="truncated" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cached-results-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "headType", propOrder = {
    "totalHits",
    "hitsFrom",
    "hitsTo",
    "truncated",
    "cachedResultsId"
})
@Data
public class Head {

    @XmlElement(name = "total-hits", required = true)
    protected String totalHits;
    @XmlElement(name = "hits-from", required = true)
    protected String hitsFrom;
    @XmlElement(name = "hits-to", required = true)
    protected String hitsTo;
    @XmlElement(required = true)
    protected String truncated;
    @XmlElement(name = "cached-results-id", required = true)
    protected String cachedResultsId;

    /**
     * Gets the value of the totalHits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalHits() {
        return totalHits;
    }

    /**
     * Sets the value of the totalHits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalHits(String value) {
        this.totalHits = value;
    }

    /**
     * Gets the value of the hitsFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHitsFrom() {
        return hitsFrom;
    }

    /**
     * Sets the value of the hitsFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHitsFrom(String value) {
        this.hitsFrom = value;
    }

    /**
     * Gets the value of the hitsTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHitsTo() {
        return hitsTo;
    }

    /**
     * Sets the value of the hitsTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHitsTo(String value) {
        this.hitsTo = value;
    }

    /**
     * Gets the value of the truncated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTruncated() {
        return truncated;
    }

    /**
     * Sets the value of the truncated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTruncated(String value) {
        this.truncated = value;
    }

    /**
     * Gets the value of the cachedResultsId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCachedResultsId() {
        return cachedResultsId;
    }

    /**
     * Sets the value of the cachedResultsId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCachedResultsId(String value) {
        this.cachedResultsId = value;
    }

}
