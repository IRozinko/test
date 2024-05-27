package fintech.spain.consents.bo;

import com.vaadin.data.HasValue;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.spain.consents.db.jooq.tables.Terms;
import fintech.spain.consents.db.jooq.tables.records.TermsRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringView(name = ConsentVersioningView.NAME)
public class ConsentVersioningView extends VerticalLayout implements View {

    public static final String NAME = "consent-versioning";

    @Autowired
    private DSLContext db;

    private TextArea terms;
    private ComboBox<String> consentType;
    private ComboBox<TermsRecord> consentVersion;
    private Map<String, List<TermsRecord>> data;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Consent versions");

        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildContent(layout);
        addComponentsAndExpand(layout);
        refresh();
    }

    private void buildTop(GridViewLayout layout) {
        layout.setRefreshAction(e -> refresh());

        consentType = new ComboBox<>("Consent type");
        consentType.setWidth(300, Unit.PIXELS);
        consentType.setEmptySelectionAllowed(false);
        consentType.setTextInputAllowed(false);
        consentType.setPlaceholder("Select consent type...");
        consentType.addValueChangeListener(this::showVersions);
        layout.addTopComponent(consentType);

        consentVersion = new ComboBox<>("Version");
        consentVersion.setWidth(200, Unit.PIXELS);
        consentVersion.setEmptySelectionAllowed(false);
        consentVersion.setTextInputAllowed(false);
        consentVersion.setPlaceholder("Select version...");
        consentVersion.addValueChangeListener(this::showTerms);
        consentVersion.setItemCaptionGenerator(TermsRecord::getVersion);
        layout.addTopComponent(consentVersion);

        layout.addMenuBarItem("Update terms", i -> {
            UpdateTermsDialog dialog = new UpdateTermsDialog();
            dialog.addCloseListener(e -> refresh());
            UI.getCurrent().addWindow(dialog);
        });
    }

    private void showVersions(HasValue.ValueChangeEvent<String> event) {
        consentVersion.clear();
        String type = event.getValue();
        if (type != null) {
            consentVersion.setItems(data.get(type));
        }
    }

    private void showTerms(HasValue.ValueChangeEvent<TermsRecord> event) {
        terms.clear();
        TermsRecord termsRecord = event.getValue();
        if (termsRecord != null) {
            terms.setValue(termsRecord.getText());
        }
    }

    private void buildContent(GridViewLayout layout) {
        TabSheet tabsheet = new TabSheet();
        tabsheet.setSizeFull();

        terms = new TextArea();
        terms.setReadOnly(true);
        terms.setSizeFull();

        VerticalLayout tabLayout = new VerticalLayout(terms);
        tabLayout.setSizeFull();
        tabsheet.addTab(tabLayout, "Terms");
        layout.setContent(tabsheet);
    }

    private void refresh() {
        data = db.selectFrom(Terms.TERMS).stream()
            .collect(Collectors.groupingBy(TermsRecord::getName, Collectors.toList()));

        consentVersion.clear();
        consentType.clear();
        consentType.setItems(new ArrayList<>(data.keySet()));
    }


}
