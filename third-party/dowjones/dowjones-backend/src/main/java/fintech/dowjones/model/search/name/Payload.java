
package fintech.dowjones.model.search.name;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for payloadType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="payloadType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="risk-icons" type="{}risk-iconsType"/>
 *         &lt;element name="primary-name">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Medvedev, Ablyalim Ametovich"/>
 *               &lt;enumeration value="Medvedev, Aleksandr Aleksandrovich"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="country-code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="title">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Federal Financial Monitoring Services (Russia) Domestic Sanctions List"/>
 *               &lt;enumeration value=""/>
 *               &lt;enumeration value="See Previous Roles"/>
 *               &lt;enumeration value="Judge, Federal Arbitration Court"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="subsidiary" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="matched-name" type="{}matched-nameType"/>
 *         &lt;element name="dates-of-birth" type="{}dates-of-birthType"/>
 *         &lt;element name="countries" type="{}countriesType"/>
 *         &lt;element name="gender" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payloadType", propOrder = {
    "riskIcons",
    "primaryName",
    "countryCode",
    "title",
    "subsidiary",
    "matchedName",
    "datesOfBirthType",
    "countryType",
    "gender"
})
@Data
public class Payload {

    @XmlElement(name = "risk-icons", required = true)
    protected RiskIcons riskIcons;
    @XmlElement(name = "primary-name", required = true)
    protected String primaryName;
    @XmlElement(name = "country-code", required = true)
    protected String countryCode;
    @XmlElement(required = true)
    protected String title;
    @XmlElement(required = true)
    protected String subsidiary;
    @XmlElement(name = "matched-name", required = true)
    protected MatchedName matchedName;
    @XmlElement(name = "dates-of-birth", required = true)
    protected DatesOfBirthType datesOfBirthType;
    @XmlElement(required = true)
    protected CountryType countryType;
    @XmlElement(required = true)
    protected String gender;

    /**
     * Gets the value of the riskIcons property.
     * 
     * @return
     *     possible object is
     *     {@link RiskIcons }
     *     
     */
    public RiskIcons getRiskIcons() {
        return riskIcons;
    }

    /**
     * Sets the value of the riskIcons property.
     * 
     * @param value
     *     allowed object is
     *     {@link RiskIcons }
     *     
     */
    public void setRiskIcons(RiskIcons value) {
        this.riskIcons = value;
    }

    /**
     * Gets the value of the primaryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryName() {
        return primaryName;
    }

    /**
     * Sets the value of the primaryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryName(String value) {
        this.primaryName = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the subsidiary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubsidiary() {
        return subsidiary;
    }

    /**
     * Sets the value of the subsidiary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubsidiary(String value) {
        this.subsidiary = value;
    }

    /**
     * Gets the value of the matchedName property.
     * 
     * @return
     *     possible object is
     *     {@link MatchedName }
     *     
     */
    public MatchedName getMatchedName() {
        return matchedName;
    }

    /**
     * Sets the value of the matchedName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MatchedName }
     *     
     */
    public void setMatchedName(MatchedName value) {
        this.matchedName = value;
    }

    /**
     * Gets the value of the datesOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link DatesOfBirthType }
     *     
     */
    public DatesOfBirthType getDatesOfBirthType() {
        return datesOfBirthType;
    }

    /**
     * Sets the value of the datesOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatesOfBirthType }
     *     
     */
    public void setDatesOfBirthType(DatesOfBirthType value) {
        this.datesOfBirthType = value;
    }

    /**
     * Gets the value of the countries property.
     * 
     * @return
     *     possible object is
     *     {@link CountryType }
     *     
     */
    public CountryType getCountryType() {
        return countryType;
    }

    /**
     * Sets the value of the countries property.
     * 
     * @param value
     *     allowed object is
     *     {@link CountryType }
     *     
     */
    public void setCountryType(CountryType value) {
        this.countryType = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGender(String value) {
        this.gender = value;
    }

}
