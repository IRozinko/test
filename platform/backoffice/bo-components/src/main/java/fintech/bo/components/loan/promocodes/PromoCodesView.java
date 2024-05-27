package fintech.bo.components.loan.promocodes;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.Refreshable;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.common.Fields;
import fintech.bo.components.layouts.GridViewLayout;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@SpringView(name = PromoCodesView.NAME)
public class PromoCodesView extends VerticalLayout implements View, Refreshable {

    public static final String NAME = "promo-codes";

    @Autowired
    private ClientComponents clientComponents;

    @Autowired
    private PromoCodesComponents promoCodesComponents;

    private PromoCodesDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Promo codes");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        createSearchForm(layout);
        layout.addActionMenuItem("New Promo Code", (MenuBar.Command) selectedItem -> showNewPromoCodePopup());
        layout.setRefreshAction(e -> refresh());
    }

    private void showNewPromoCodePopup() {
        CreatePromoCodeDialog dialog = promoCodesComponents.newPromoCodeDialog();
        UI.getCurrent().addWindow(dialog);
        dialog.addCloseListener(e -> refresh());
    }

    private void createSearchForm(GridViewLayout layout) {
        TextField code = new TextField("Code");
        code.addValueChangeListener(e -> dataProvider.setCode(e.getValue()));
        code.addValueChangeListener(e -> refresh());
        layout.addTopComponent(code);

        DateRangeField effective = Fields.dateRangeField("Effective");
        effective.addValueChangeListener(e -> {
            dataProvider.setEffectiveFrom(e.getValue().getBeginDate());
            dataProvider.setEffectiveTo(e.getValue().getEndDate());
        });
        effective.addValueChangeListener(e -> refresh());
        layout.addTopComponent(effective);

        List<String> values = Arrays.asList(PromoCodeType.TYPE_NEW_CLIENTS, PromoCodeType.TYPE_REPEATING_CLIENTS);
        ComboBox<String> codeType = new ComboBox<>("Type", values);
        codeType.addValueChangeListener(e -> dataProvider.setCodeType(e.getValue()));
        codeType.addValueChangeListener(e -> refresh());
        layout.addTopComponent(codeType);

        ComboBox<ClientDTO> client = clientComponents.clientsComboBox();
        client.setCaption("Client");
        client.setWidth(200, Unit.PIXELS);
        client.addValueChangeListener(e -> dataProvider.setClient(e.getValue()));
        client.addValueChangeListener(e -> refresh());
        layout.addTopComponent(client);

        ComboBox<String> source = promoCodesComponents.affiliateNames();
        source.addValueChangeListener(e -> dataProvider.setSource(e.getValue()));
        source.addValueChangeListener(e -> refresh());
        layout.addTopComponent(source);
    }

    private void buildGrid(GridViewLayout layout) {
        Grid<Record> promoCodesGrid = promoCodesComponents.promoCodesGrid();
        dataProvider = (PromoCodesDataProvider) promoCodesGrid.getDataProvider();
        layout.setContent(promoCodesGrid);
    }

    @Override
    public void refresh() {
        dataProvider.refreshAll();
    }

}
