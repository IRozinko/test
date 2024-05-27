package fintech.crm.address.db;

import fintech.crm.address.ClientAddress;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.db.Entities;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;


@Getter
@Setter
@ToString(callSuper = true)
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Entity
@Table(name = "client_address", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "client_id", name = "idx_client_address_client_id"),
})
public class ClientAddressEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, name = "is_primary")
    private boolean primary;

    private String street;
    private String houseNumber;
    private String province;
    private String city;
    private String postalCode;
    private String housingTenure;
    private String houseFloor;
    private String houseLetter;

    public ClientAddress toValueObject() {
        ClientAddress address = new ClientAddress();
        address.setId(this.id);
        address.setType(this.type);
        address.setStreet(this.street);
        address.setHouseNumber(this.houseNumber);
        address.setProvince(this.province);
        address.setCity(this.city);
        address.setPostalCode(this.postalCode);
        address.setHousingTenure(this.housingTenure);
        address.setHouseFloor(this.houseFloor);
        address.setHouseLetter(this.houseLetter);
        return address;
    }
}
