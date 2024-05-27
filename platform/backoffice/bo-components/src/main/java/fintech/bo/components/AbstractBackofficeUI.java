package fintech.bo.components;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.model.product.ProductType;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.payments.disbursement.DisbursementQueueCache;
import fintech.bo.components.payments.disbursement.DisbursementQueueComponent;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.security.SecurityComponents;
import fintech.bo.components.tabs.TabSheetNavigator;
import fintech.bo.components.task.TaskComponents;
import fintech.bo.components.task.TaskQueueComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractBackofficeUI extends UI {

    @Autowired
    private SpringViewProvider viewProvider;

    @Autowired
    private SpringNavigator navigator;

    @Autowired
    private ClientComponents clientComponents;

    @Autowired
    private LoginService loginService;

    @Autowired
    private TaskComponents taskComponents;

    @Autowired
    private SecurityComponents securityComponents;

    @Autowired
    private PollingScheduler pollingScheduler;

    @Autowired
    private DisbursementQueueCache disbursementQueueCache;

    private CustomErrorHandler errorHandler = new CustomErrorHandler();

    private TabSheetNavigator tabSheetNavigator = new TabSheetNavigator();

    private String title;

    public AbstractBackofficeUI(ProductType productType, String title) {
        this.title = title;
        ProductResolver.init(productType);
    }

    @Override
    protected final void init(VaadinRequest request) {
        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(30 * 60);

        setErrorHandler(errorHandler);
        if (LoginService.isLoggedIn()) {
            setup();
            setContent(buildUi());
            addDetachListener((DetachListener) event -> log.info("Detached UI, user {}", LoginService.getLoginData().getUser()));
        } else {
            loginService.setInitialUriFragment(getPage().getUriFragment());
            setContent(securityComponents.buildLoginUi(title));
        }
    }

    protected abstract void setup();

    protected abstract MenuBar buildMainMenu();

    private VerticalLayout buildUi() {
        AbstractLayout top = buildTop();
        TabSheet tabSheet = buildTabSheet();
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(false);
        root.setSpacing(false);
        root.addComponent(top);

        VerticalLayout tabSheetLayout = new VerticalLayout();
        tabSheetLayout.addComponentsAndExpand(tabSheet);
        tabSheetLayout.setMargin(false);
        tabSheetLayout.setSizeFull();
        root.addComponentsAndExpand(tabSheetLayout);

        root.addStyleName(BackofficeTheme.APP_SCREEN);
        return root;
    }

    private TabSheet buildTabSheet() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        tabSheet.setWidth(100, Unit.PERCENTAGE);
        tabSheet.addStyleName("bo-main-tabsheet");
        tabSheetNavigator.connect(this, viewProvider, navigator, tabSheet);
        return tabSheet;
    }

    private AbstractLayout buildTop() {
        ComboBox<ClientDTO> clientsComboBox = clientsComboBox();
        Component userInfo = topRight();

        HorizontalLayout left = new HorizontalLayout();
        left.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        MenuBar menuBar = buildMainMenu();
        menuBar.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        MenuBar.MenuItem windowMenu = menuBar.addItem("Window", null);
        windowMenu.addItem("Close all tabs", e -> tabSheetNavigator.closeAllTabs());
        windowMenu.addItem("Close other tabs", e -> tabSheetNavigator.closeOtherTabs());
        windowMenu.addItem("Logout", e -> {
            LoginService.logout(this);
            getPage().setLocation("/");
        });

        left.addComponents(menuBar, clientsComboBox);

        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(left);
        layout.addComponentsAndExpand(new HorizontalLayout());
        layout.addComponent(userInfo);
        layout.setComponentAlignment(left, Alignment.MIDDLE_LEFT);
        layout.setComponentAlignment(userInfo, Alignment.MIDDLE_RIGHT);
        layout.addStyleName("bo-top-navigation");
        return layout;
    }

    private ComboBox<ClientDTO> clientsComboBox() {
        ComboBox<ClientDTO> clientsComboBox = clientComponents.clientsComboBox();
        clientsComboBox.setWidth(250, Unit.PIXELS);
        clientsComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                navigator.navigateTo("client/" + event.getValue().getId());
            }
            ((ComboBox) event.getComponent()).clear();
        });
        clientsComboBox.setPlaceholder("Search client...");
        return clientsComboBox;
    }

    private Component topRight() {
        Label user = new Label();
        user.setStyleName(ValoTheme.LABEL_TINY);
        user.addStyleName(BackofficeTheme.TEXT_WHITE);
        user.setValue(LoginService.getLoginData().getUser());

        TaskQueueComponent taskQueueComponent = taskComponents.taskQueueComponent();
        DisbursementQueueComponent disbursementQueueComponent = new DisbursementQueueComponent(getUI(), disbursementQueueCache, pollingScheduler);

        HorizontalLayout right = new HorizontalLayout(user, disbursementQueueComponent, taskQueueComponent);
        right.setComponentAlignment(user, Alignment.MIDDLE_LEFT);
        return right;
    }

    public TabSheetNavigator getTabSheetNavigator() {
        return tabSheetNavigator;
    }
}
