package fintech.spain.alfa.product.scoring;

import fintech.ScoringProperties;
import fintech.TimeMachine;
import fintech.crm.address.ClientAddress;
import fintech.crm.address.ClientAddressService;
import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.lending.core.discount.DiscountService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.scoring.values.spi.ScoringValuesProvider;
import fintech.spain.alfa.product.AlfaConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Component
@Transactional
public class ClientScoringValuesProvider implements ScoringValuesProvider {

    @Autowired
    private ClientBankAccountService clientBankAccountService;

    @Autowired
    private ClientAddressService clientAddressService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private DiscountService discountService;

    @Override
    public Properties provide(long clientId) {
        Client client = clientService.get(clientId);
        ScoringProperties properties = new ScoringProperties("user_data");
        properties.put("client_first_created_at_date", client.getCreatedAt());
        properties.put("email", client.getEmail());
        properties.put("date_of_birth", client.getDateOfBirth());
        properties.put("age", ChronoUnit.YEARS.between(client.getDateOfBirth(), TimeMachine.today()));
        properties.put("gender", client.getGender());
        properties.put("accept_marketing", String.valueOf(client.isAcceptMarketing()));

        Optional<ClientAddress> actualAddress = clientAddressService.getClientPrimaryAddress(client.getId(), AlfaConstants.ADDRESS_TYPE_ACTUAL);
        if (actualAddress.isPresent()) {
            properties.put("address_zipcode", actualAddress.get().getPostalCode());
            properties.put("address_city", actualAddress.get().getCity());
            properties.put("address_street", actualAddress.get().getStreet());
            properties.put("address_housenumber", actualAddress.get().getHouseNumber());
        }

        clientBankAccountService.findPrimaryByClientId(clientId).ifPresent(account -> properties.put("iban", account.getAccountNumber()));

        List<Loan> loans = loanService.findLoans(LoanQuery.allLoans(clientId));

        if (!loans.isEmpty()) {
            Loan first = loans.get(0);
            properties.put("first_loan_issued_date", first.getIssueDate());
            Loan last = loans.get(loans.size() - 1);
            properties.put("last_loan_issued_date", last.getIssueDate());
            if (last.getCloseDate() != null) {
                properties.put("last_loan_closed_date", last.getCloseDate());
            }
            if (last.getDiscountId() != null) {
                properties.put("discount_percent", discountService.get(last.getDiscountId()).getRateInPercent());
            }
        }

        return properties;
    }
}
