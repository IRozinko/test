package fintech.crm.country.db;

import fintech.crm.country.Country;
import fintech.crm.db.Entities;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "country", schema = Entities.SCHEMA)
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false)
    private String nationalityDisplayName;

    @Column(nullable = false)
    private boolean homeCountry;

    public Country toValueObject() {
        Country country = new Country();
        country.setCode(code);
        country.setName(name);
        country.setDisplayName(displayName);
        country.setNationality(nationality);
        country.setNationalityDisplayName(nationalityDisplayName);
        country.setHomeCountry(homeCountry);
        return country;
    }

}
