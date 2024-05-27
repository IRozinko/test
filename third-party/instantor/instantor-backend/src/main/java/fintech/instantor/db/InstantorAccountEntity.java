package fintech.instantor.db;

import fintech.db.BaseEntity;
import fintech.instantor.model.InstantorAccount;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "account", schema = Entities.SCHEMA)
@DynamicUpdate
public class InstantorAccountEntity extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id")
    private InstantorResponseEntity response;

    private Long clientId;

    @Column(nullable = false)
    private String accountNumber;

    @Column
    private String accountHolderName;

    @Column(nullable = false)
    private String currency;

    private BigDecimal balance;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstantorTransactionEntity> transactions;

    public InstantorAccount toValueObject() {
        InstantorAccount vo = new InstantorAccount();
        vo.setResponseId(response.getId());
        vo.setClientId(clientId);
        vo.setIban(accountNumber);
        vo.setHolderName(accountHolderName);
        vo.setCurrency(currency);
        vo.setBalance(balance);
        vo.setTransactionCount(getTransactions().size());
        vo.setTransactionList(getTransactions().stream().map(InstantorTransactionEntity::toValueObject).collect(Collectors.toList()));
        return vo;
    }
}
