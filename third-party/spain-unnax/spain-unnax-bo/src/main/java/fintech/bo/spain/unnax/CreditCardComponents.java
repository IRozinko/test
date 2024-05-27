package fintech.bo.spain.unnax;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.UI;
import fintech.ClasspathUtils;
import fintech.RandomUtils;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.dto.ClientDTO;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static fintech.spain.unnax.db.jooq.tables.CreditCard.CREDIT_CARD;


@Component
public class CreditCardComponents {

    @Value("${spain.unnax.apiId:test}")
    private String apiId;

    @Value("${spain.unnax.apiCode:test}")
    private String apiCode;

    @Autowired
    private DSLContext db;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    public CreditCardDataProvider dataProvider() {
        return new CreditCardDataProvider(db, jooqClientDataService);
    }

    public Grid<Record> grid(CreditCardDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(CREDIT_CARD.CLIENT_NUMBER);
        builder.addColumn(CREDIT_CARD.CARD_TOKEN);
        builder.addColumn(CREDIT_CARD.CARD_BANK);
        builder.addColumn(CREDIT_CARD.CALLBACK_TRANSACTION_ID);
        builder.addColumn(CREDIT_CARD.CARD_BRAND);
        builder.addColumn(CREDIT_CARD.ACTIVE);
        builder.addColumn(CREDIT_CARD.PAN);
        builder.addColumn(CREDIT_CARD.CARD_EXPIRE_MONTH);
        builder.addColumn(CREDIT_CARD.CARD_EXPIRE_YEAR);
        builder.addColumn(CREDIT_CARD.CARD_HOLDER_NAME);
        builder.addColumn(CREDIT_CARD.ERROR_DETAILS);
        builder.addAuditColumns(CREDIT_CARD);
        builder.addColumn(CREDIT_CARD.ID);
        builder.sortDesc(CREDIT_CARD.CREATED_AT);
        builder.getGrid().setStyleGenerator(creditCardGridStyle());
        return builder.build(dataProvider);
    }

    private StyleGenerator<Record> creditCardGridStyle() {
        return item -> {
            if (item.get(CREDIT_CARD.ACTIVE)) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else {
                return BackofficeTheme.TEXT_GRAY;
            }
        };
    }

    public com.vaadin.ui.Component addCreditCardButton(ClientDTO client) {
        Button button = new Button("Add Credit Card");
        button.addClickListener(e -> UI.getCurrent().getPage().getJavaScript().execute(getScript(client.getClientNumber())));
        return button;
    }

    public com.vaadin.ui.Component listOfCreditCards(ClientDTO client) {
        CreditCardDataProvider creditCardDataProvider = dataProvider();
        creditCardDataProvider.setClientId(client.getClientNumber());
        return grid(creditCardDataProvider);
    }

    private String getScript(String clientNumber) {
        String script = "";
        script = script.concat(getSha1Script());
        String creditCardScript = getCreditCardPreAuthScript();
        String number = clientNumber.concat(".").concat(RandomUtils.randomDocNumber());
        creditCardScript = creditCardScript.replace("$clientNumber", number);
        creditCardScript = creditCardScript.replace("$apiId", apiId);
        creditCardScript = creditCardScript.replace("$apiCode", apiCode);
        script = script.concat(creditCardScript);
        return script;
    }

    private String getSha1Script() {
        return ClasspathUtils.resourceToString("js/sha1.js");
    }

    private String getCreditCardPreAuthScript() {
        return ClasspathUtils.resourceToString("js/credit-card-preauthorize.js");
    }

}
