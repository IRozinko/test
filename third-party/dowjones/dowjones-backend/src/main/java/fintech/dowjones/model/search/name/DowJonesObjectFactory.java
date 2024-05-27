
package fintech.dowjones.model.search.name;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dowjones package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class DowJonesObjectFactory {

    private final static QName _SearchResults_QNAME = new QName("", "search-results");
    private final static QName _DatesOfBirthTypeDateOfBirth_QNAME = new QName("", "date-of-birth");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dowjones
     * 
     */
    public DowJonesObjectFactory() {
    }

    /**
     * Create an instance of {@link NameSearchResult }
     * 
     */
    public NameSearchResult createSearchResultsType() {
        return new NameSearchResult();
    }

    /**
     * Create an instance of {@link Body }
     * 
     */
    public Body createBodyType() {
        return new Body();
    }

    /**
     * Create an instance of {@link MatchedName }
     * 
     */
    public MatchedName createMatchedNameType() {
        return new MatchedName();
    }

    /**
     * Create an instance of {@link Head }
     * 
     */
    public Head createHeadType() {
        return new Head();
    }

    /**
     * Create an instance of {@link Payload }
     * 
     */
    public Payload createPayloadType() {
        return new Payload();
    }

    /**
     * Create an instance of {@link Match }
     * 
     */
    public Match createMatchType() {
        return new Match();
    }

    /**
     * Create an instance of {@link DatesOfBirthType }
     * 
     */
    public DatesOfBirthType createDatesOfBirthType() {
        return new DatesOfBirthType();
    }

    /**
     * Create an instance of {@link DateOfBirth }
     * 
     */
    public DateOfBirth createDateOfBirthType() {
        return new DateOfBirth();
    }

    /**
     * Create an instance of {@link Country }
     * 
     */
    public Country createCountryType() {
        return new Country();
    }

    /**
     * Create an instance of {@link CountryType }
     * 
     */
    public CountryType createCountriesType() {
        return new CountryType();
    }

    /**
     * Create an instance of {@link MatchType }
     * 
     */
    public MatchType createMatchTypeType() {
        return new MatchType();
    }

    /**
     * Create an instance of {@link RiskIcons }
     * 
     */
    public RiskIcons createRiskIconsType() {
        return new RiskIcons();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NameSearchResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "search-results")
    public JAXBElement<NameSearchResult> createSearchResults(NameSearchResult value) {
        return new JAXBElement<NameSearchResult>(_SearchResults_QNAME, NameSearchResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DateOfBirth }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "date-of-birth", scope = DatesOfBirthType.class)
    public JAXBElement<DateOfBirth> createDatesOfBirthTypeDateOfBirth(DateOfBirth value) {
        return new JAXBElement<DateOfBirth>(_DatesOfBirthTypeDateOfBirth_QNAME, DateOfBirth.class, DatesOfBirthType.class, value);
    }

}
