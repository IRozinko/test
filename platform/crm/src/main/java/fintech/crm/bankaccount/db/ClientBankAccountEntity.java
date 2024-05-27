package fintech.crm.bankaccount.db;

import fintech.crm.bankaccount.ClientBankAccount;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.db.Entities;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "client_bank_account", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "client_id", name = "idx_client_bank_account_client_id"),
})
public class ClientBankAccountEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountOwnerName;

    private String currency;

    private BigDecimal balance;

    @Column(nullable = false)
    private Long numberOfTransactions = 0L;

    public ClientBankAccount toValueObject() {
        ClientBankAccount bankAccount = new ClientBankAccount();
        bankAccount.setId(this.id);
        bankAccount.setClientId(client.getId());
        bankAccount.setBankName(bankName);
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setAccountOwnerName(accountOwnerName);
        bankAccount.setPrimary(primary);
        bankAccount.setCurrency(this.currency);
        bankAccount.setBalance(this.balance);
        bankAccount.setNumberOfTransactions(this.numberOfTransactions);
        return bankAccount;
    }
}
