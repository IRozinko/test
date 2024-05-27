
package fintech.dowjones.model.search.name;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for matchType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="matchType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="score" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="match-type" type="{}match-typeType"/>
 *         &lt;element name="payload" type="{}payloadType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="peid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="revision" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="record-type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "matchType", propOrder = {
    "score",
    "matchType",
    "payload"
})
@Data
public class Match {

    @XmlElement(required = true)
    protected String score;
    @XmlElement(name = "match-type", required = true)
    protected MatchType matchType;
    @XmlElement(required = true)
    protected Payload payload;
    @XmlAttribute(name = "peid")
    protected String peid;
    @XmlAttribute(name = "revision")
    protected String revision;
    @XmlAttribute(name = "record-type")
    protected String recordType;

    /**
     * Gets the value of the score property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScore() {
        return score;
    }

    /**
     * Sets the value of the score property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScore(String value) {
        this.score = value;
    }

    /**
     * Gets the value of the matchType property.
     * 
     * @return
     *     possible object is
     *     {@link MatchType }
     *     
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * Sets the value of the matchType property.
     * 
     * @param value
     *     allowed object is
     *     {@link MatchType }
     *     
     */
    public void setMatchType(MatchType value) {
        this.matchType = value;
    }

    /**
     * Gets the value of the payload property.
     * 
     * @return
     *     possible object is
     *     {@link Payload }
     *     
     */
    public Payload getPayload() {
        return payload;
    }

    /**
     * Sets the value of the payload property.
     * 
     * @param value
     *     allowed object is
     *     {@link Payload }
     *     
     */
    public void setPayload(Payload value) {
        this.payload = value;
    }

    /**
     * Gets the value of the peid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeid() {
        return peid;
    }

    /**
     * Sets the value of the peid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeid(String value) {
        this.peid = value;
    }

    /**
     * Gets the value of the revision property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Sets the value of the revision property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevision(String value) {
        this.revision = value;
    }

    /**
     * Gets the value of the recordType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecordType() {
        return recordType;
    }

    /**
     * Sets the value of the recordType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecordType(String value) {
        this.recordType = value;
    }

}
