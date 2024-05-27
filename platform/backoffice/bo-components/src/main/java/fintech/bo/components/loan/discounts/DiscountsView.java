package fintech.bo.components.loan.discounts;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.Refreshable;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.layouts.GridViewLayout;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SpringView(name = DiscountsView.NAME)
public class DiscountsView extends VerticalLayout implements View, Refreshable {

    public static final String NAME = "discounts";

    @Autowired
    private ClientComponents clientComponents;

    @Autowired
    private DiscountComponents discountComponents;

    private DiscountDataProvider dataProvider;

    private ComboBox<ClientDTO> client;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Discounts");

        dataProvider = discountComponents.dataProvider();

        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        client = clientComponents.clientsComboBox();
        client.setCaption("Client");
        client.setWidth(200, Unit.PIXELS);
        client.addValueChangeListener(event -> refresh());

        Upload upload = discountComponents.generateUploadButton(this);

        layout.addTopComponent(client);
        layout.addTopComponent(upload);
        layout.setRefreshAction((e) -> refresh());
    }

    private void buildGrid(GridViewLayout layout) {
        Grid<Record> grid = discountComponents.grid(dataProvider);

        layout.setContent(grid);
    }

    @Override
    public void refresh() {
        dataProvider.setClientId(client.getSelectedItem().map(ClientDTO::getId).orElse(null));
        dataProvider.refreshAll();
    }
}
