package fintech.bo.components.loan.promocodes;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TwinColSelect;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.client.PromoCodeApiClient;
import fintech.bo.api.model.loan.EditPromoCodeRequest;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.loan.LoanComponents;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.components.loan.LoanComponents.loanStatusStyle;
import static fintech.bo.db.jooq.affiliate.Tables.PARTNER;
import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.lending.Tables.LOAN;
import static fintech.bo.db.jooq.lending.tables.PromoCode.PROMO_CODE;

@Component
public class PromoCodesComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private PromoCodeQueries promoCodeQueries;

    @Autowired
    private PromoCodeApiClient promoCodeApi;

    @Autowired
    private FileApiClient fileApiClient;

    public Grid<Record> promoCodesGrid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "promo-code/" + r.get(PROMO_CODE.ID));
        builder.addColumn(PROMO_CODE.CODE).setWidth(200);
        builder.addColumn(PROMO_CODE.DESCRIPTION).setWidth(200);
        builder.addColumn(PROMO_CODE.EFFECTIVE_FROM).setWidth(110);
        builder.addColumn(PROMO_CODE.EFFECTIVE_TO).setWidth(110);
        builder.addColumn(PROMO_CODE.RATE_IN_PERCENT).setWidth(115);
        builder.addColumn(PROMO_CODE.MAX_TIMES_TO_APPLY).setWidth(140);
        builder.addColumn(promoCodeQueries.timesUsedField()).setWidth(100);
        builder.addColumn(PROMO_CODE.NEW_CLIENTS_ONLY).setWidth(125);
        builder.addColumn(PROMO_CODE.ACTIVE).setWidth(100);
        builder.addColumn(promoCodeQueries.sourcesField());
        return builder.build(new PromoCodesDataProvider(db, promoCodeQueries));
    }

    public Grid<Record> clientPromoCodesGrid(ClientDTO client) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "promo-code/" + r.get(PROMO_CODE.ID));
        builder.addColumn(PROMO_CODE.CODE).setWidth(200);
        builder.addColumn(PROMO_CODE.EFFECTIVE_FROM).setWidth(110);
        builder.addColumn(PROMO_CODE.EFFECTIVE_TO).setWidth(110);
        builder.addColumn(PROMO_CODE.RATE_IN_PERCENT).setWidth(115);
        builder.addColumn(promoCodeQueries.redeemedField()).setWidth(140);
        return builder.build(new ClientPromoCodesDataProvider(db, client, promoCodeQueries));
    }

    Grid<Record> usedByGrid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();

        builder.addLinkColumn(CLIENT.CLIENT_NUMBER, r -> ClientComponents.clientLink(r.get(LOAN.CLIENT_ID))).setCaption("Client");
        builder.addColumn(CLIENT.FIRST_NAME);
        builder.addColumn(CLIENT.LAST_NAME);
        builder.addLinkColumn(LOAN.LOAN_NUMBER, r -> LoanComponents.loanLink(r.get(LOAN.ID))).setCaption("Loan number");
        builder.addColumn(LOAN.ISSUE_DATE);
        builder.addColumn(LOAN.STATUS).setStyleGenerator(loanStatusStyle());
        builder.addColumn(LOAN.STATUS_DETAIL);

        UsedByDataProvider dataProvider = new UsedByDataProvider(db);
        Grid<Record> grid = builder.build(dataProvider);
        grid.setSizeFull();
        return grid;
    }

    Grid<Record> availableForGrid() {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();

        builder.addLinkColumn(CLIENT.CLIENT_NUMBER, r -> ClientComponents.clientLink(r.get(CLIENT.ID))).setCaption("Client");
        builder.addColumn(CLIENT.FIRST_NAME);
        builder.addColumn(CLIENT.LAST_NAME);

        AvailableForDataProvider dataProvider = new AvailableForDataProvider(db);
        Grid<Record> grid = builder.build(dataProvider);
        grid.setSizeFull();
        return grid;
    }

    ComboBox<String> affiliateNames() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setCaption("Affiliate");
        comboBox.setItems(db.select(PARTNER.NAME)
            .from(PARTNER)
            .where(PARTNER.ACTIVE)
            .orderBy(PARTNER.NAME.asc())
            .fetchInto(String.class));
        return comboBox;
    }

    TwinColSelect<String> sourceSelector() {
        TwinColSelect<String> select = new TwinColSelect<>("Affiliates");
        select.setItems(db.select(PARTNER.NAME)
            .from(PARTNER)
            .where(PARTNER.ACTIVE)
            .orderBy(PARTNER.NAME.asc())
            .fetchInto(String.class));
        return select;
    }

    CreatePromoCodeDialog newPromoCodeDialog() {
        return new CreatePromoCodeDialog(promoCodeApi, fileApiClient, this);
    }

    UpdateClientsDialog updateClientsDialog(Long promoCodeId) {
        return new UpdateClientsDialog(promoCodeId, promoCodeApi, fileApiClient);
    }

    EditPromoCodeDialog editPromoCodeDialog(EditPromoCodeRequest request) {
        return new EditPromoCodeDialog(promoCodeApi, this, request);
    }
}
