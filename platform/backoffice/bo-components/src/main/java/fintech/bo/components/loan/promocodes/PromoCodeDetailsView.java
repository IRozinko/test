package fintech.bo.components.loan.promocodes;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.PromoCodeApiClient;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.loan.EditPromoCodeRequest;
import fintech.bo.components.AbstractBackofficeUI;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.Refreshable;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ConfirmDialog;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.utils.UrlUtils;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringView(name = PromoCodeDetailsView.NAME)
public class PromoCodeDetailsView extends VerticalLayout implements View, Refreshable {

    public static final String NAME = "promo-code";

    @Autowired
    private PromoCodesComponents promoCodesComponents;

    @Autowired
    private PromoCodeQueries promoCodeQueries;

    @Autowired
    private PromoCodeApiClient promoCodeApiClient;

    private Long promoCodeId;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        promoCodeId = Long.parseLong(UrlUtils.getParam(event.getParameters(), UrlUtils.ID));
        refresh();
    }

    @Override
    public void refresh() {
        removeAllComponents();

        PromoCodeDetails promoCodeDetails = promoCodeQueries.fetchDetails(promoCodeId);

        setCaption("PC: " + promoCodeDetails.getCode());

        BusinessObjectLayout layout = new BusinessObjectLayout();

        layout.setTitle("Promo Code: " + promoCodeDetails.getCode());

        layout.addLeftComponent(createProperties(promoCodeDetails));

        layout.addTab("Used by", () -> {
            Grid<Record> grid = promoCodesComponents.usedByGrid();
            UsedByDataProvider dataProvider = (UsedByDataProvider) grid.getDataProvider();
            dataProvider.setPromoCodeId(promoCodeId);
            return grid;
        });

        if (PromoCodeType.TYPE_REPEATING_CLIENTS.equals(promoCodeDetails.getType())) {
            layout.addTab("Available for", () -> {
                Grid<Record> grid = promoCodesComponents.availableForGrid();
                AvailableForDataProvider dataProvider = (AvailableForDataProvider) grid.getDataProvider();
                dataProvider.setPromoCodeId(promoCodeId);
                return grid;
            });
        }

        layout.setRefreshAction(this::refresh);

        layout.addActionMenuItem("Edit", selectedItem -> editPromoCode(promoCodeId, promoCodeDetails));

        if (PromoCodeType.TYPE_REPEATING_CLIENTS.equals(promoCodeDetails.getType())) {
            layout.addActionMenuItem("Update client list", selectedItem -> updateClientList(promoCodeId));
        }

        if (promoCodeDetails.isActive()) {
            layout.addActionMenuItem("Deactivate", selectedItem -> deactivatePromoCode(promoCodeId));
        } else {
            layout.addActionMenuItem("Activate",  selectedItem -> activatePromoCode(promoCodeId));
        }

        if (!promoCodeDetails.isActive() && promoCodeDetails.getTimesUsed() == 0) {
            layout.addActionMenuItem("Delete", selectedItem -> deletePromoCode(promoCodeId));
        }

        addComponentsAndExpand(layout);
    }

    private void editPromoCode(Long promoCodeId, PromoCodeDetails details) {
        EditPromoCodeRequest request = new EditPromoCodeRequest()
            .setPromoCodeId(promoCodeId)
            .setDescription(details.getDescription())
            .setEffectiveFrom(details.getEffectiveFrom())
            .setEffectiveTo(details.getEffectiveTo())
            .setRateInPercent(details.getRateInPercent())
            .setMaxTimesToApply(details.getMaxTimesToApply());

        if (details.getSource() != null) {
            Set<String> sourceSet = Stream.of(details.getSource().split(", "))
                .collect(Collectors.toSet());
            request.setSources(sourceSet);
        }

        EditPromoCodeDialog dialog = promoCodesComponents.editPromoCodeDialog(request);
        UI.getCurrent().addWindow(dialog);
        dialog.addCloseListener(e -> refresh());
    }

    private void updateClientList(Long promoCodeId) {
        UpdateClientsDialog dialog = promoCodesComponents.updateClientsDialog(promoCodeId);
        UI.getCurrent().addWindow(dialog);
        dialog.addCloseListener(e -> refresh());
    }

    private void deactivatePromoCode(Long promoCodeId) {
        ConfirmDialog confirm = new ConfirmDialog("Deactivate promo code?", (Button.ClickListener) event -> {
            Call<Void> call = promoCodeApiClient.deactivate(new IdRequest(promoCodeId));
            BackgroundOperations.callApi("Deactivating promo code", call, t -> {
                Notifications.trayNotification("Promo code deactivated!");
                refresh();
            }, Notifications::errorNotification);
        });
        getUI().addWindow(confirm);
    }

    private void activatePromoCode(Long promoCodeId) {
        ConfirmDialog confirm = new ConfirmDialog("Activate promo code?", (Button.ClickListener) event -> {
            Call<Void> call = promoCodeApiClient.activate(new IdRequest(promoCodeId));
            BackgroundOperations.callApi("Activating promo code", call, t -> {
                Notifications.trayNotification("Promo code activated!");
                refresh();
            }, Notifications::errorNotification);
        });
        getUI().addWindow(confirm);
    }

    private void deletePromoCode(Long promoCodeId) {
        ConfirmDialog confirm = new ConfirmDialog("Delete promo code?", (Button.ClickListener) event -> {
            Call<Void> call = promoCodeApiClient.delete(new IdRequest(promoCodeId));
            BackgroundOperations.callApi("Deleting promo code", call, t -> {
                Notifications.trayNotification("Promo code deleted!");
                AbstractBackofficeUI ui = (AbstractBackofficeUI) UI.getCurrent();
                ui.getTabSheetNavigator().closeCurrentTab();
            }, Notifications::errorNotification);
        });
        getUI().addWindow(confirm);
    }

    private PropertyLayout createProperties(PromoCodeDetails promoCodeDetails) {
        PropertyLayout properties = new PropertyLayout();
        properties.add("Promo code", promoCodeDetails.getCode());
        properties.add("Description", promoCodeDetails.getDescription());
        properties.add("Type", promoCodeDetails.getType());
        properties.add("Source", promoCodeDetails.getSource());
        properties.add("Effective from", promoCodeDetails.getEffectiveFrom());
        properties.add("Effective to", promoCodeDetails.getEffectiveTo());
        properties.add("Rate in percent", promoCodeDetails.getRateInPercent());
        properties.add("Max times to apply", promoCodeDetails.getMaxTimesToApply());
        properties.add("Times used", promoCodeDetails.getTimesUsed());
        properties.add("Active", promoCodeDetails.isActive());
        return properties;
    }
}
