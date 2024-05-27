package fintech.bo.spain.alfa.marketing;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.ActivityApiClient;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.jooq.JooqGrid;
import fintech.bo.db.jooq.crm.tables.records.MarketingConsentLogRecord;
import fintech.bo.spain.alfa.api.MarketingConsentApiClient;
import org.jooq.DSLContext;

import static fintech.bo.components.jooq.JooqGridSortOrder.desc;
import static fintech.bo.db.jooq.crm.Tables.MARKETING_CONSENT_LOG;
import static java.util.Optional.ofNullable;

public class MarketingConsentTab extends VerticalLayout {

    private final ClientRepository clientRepository;
    private final MarketingConsentLogProvider provider;
    private final MarketingConsentApiClient marketingConsentApiClient;
    private final ActivityApiClient activityApiClient;

    private final long clientId;

    public MarketingConsentTab(long clientId, DSLContext db, MarketingConsentApiClient marketingConsentApiClient,
                               ClientRepository clientRepository, ActivityApiClient activityApiClient) {
        this.clientRepository = clientRepository;
        this.marketingConsentApiClient = marketingConsentApiClient;
        this.activityApiClient = activityApiClient;
        this.provider = new MarketingConsentLogProvider(clientId, db);
        this.clientId = clientId;
        render();
    }

    private void render() {
        removeAllComponents();
        ClientDTO client = clientRepository.getRequired(clientId);
        addComponent(new ToggleMarketingBtn(ofNullable(client.getAcceptMarketing()).orElse(false)));
        addComponentsAndExpand(new MarketingConsentGrid());
    }

    private class ToggleMarketingBtn extends Button {

        private final boolean newValue;

        ToggleMarketingBtn(boolean currentValue) {
            String caption = currentValue ? "Disable marketing consent" : "Enable marketing consent";
            this.newValue = !currentValue;
            setCaption(caption);
            addClickListener(this::handleClick);
        }

        private void handleClick(ClickEvent event) {
            MarketingConsentDialog dialog = new MarketingConsentDialog(marketingConsentApiClient, activityApiClient, clientId,
                newValue, () -> Page.getCurrent().reload());
            getUI().addWindow(dialog);
        }

    }

    private class MarketingConsentGrid extends JooqGrid<MarketingConsentLogRecord> {

        MarketingConsentGrid() {
            setWidth(100, Unit.PERCENTAGE);

            addColumn(dateTime(MARKETING_CONSENT_LOG.CREATED_AT));
            addColumn(text(MARKETING_CONSENT_LOG.CREATED_BY));
            addColumn(text(MARKETING_CONSENT_LOG.VALUE)).setWidth(100);
            addColumn(text(MARKETING_CONSENT_LOG.SOURCE));
            addColumn(text(MARKETING_CONSENT_LOG.NOTE)).setWidth(500);

            setSortOrder(desc(MARKETING_CONSENT_LOG.CREATED_AT));

            setDataProvider(provider);
        }


    }

}
