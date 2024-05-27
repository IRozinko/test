
package fintech.dowjones.model.search.name;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for risk-iconsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="risk-iconsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="risk-icon">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="SAN"/>
 *               &lt;enumeration value="RCA"/>
 *               &lt;enumeration value="PEP"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "risk-iconsType", propOrder = {
    "riskIcon"
})
@Data
public class RiskIcons {

    @XmlElement(name = "risk-icon", required = true)
    protected String riskIcon;

    /**
     * Gets the value of the riskIcon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiskIcon() {
        return riskIcon;
    }

    /**
     * Sets the value of the riskIcon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiskIcon(String value) {
        this.riskIcon = value;
    }

}
