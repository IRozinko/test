package fintech.bo.components.tabs;


import com.google.common.collect.ImmutableList;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import fintech.bo.components.ErrorView;
import fintech.bo.components.HomeView;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class TabSheetNavigator {

    private UI ui;
    private TabSheet tabSheet;

    public void connect(UI ui, SpringViewProvider viewProvider, SpringNavigator navigator, TabSheet tabSheet) {
        this.ui = ui;
        this.tabSheet = tabSheet;
        navigator.init(ui, viewDisplay(ui, tabSheet));
        navigator.addViewChangeListener(viewChangeListener(tabSheet));
        tabSheet.setCloseHandler(tabCloseHandler(ui, tabSheet));
        tabSheet.addSelectedTabChangeListener(selectedTabListener(ui));
        navigator.setErrorView(ErrorView.class);
        navigator.addProvider(viewProvider);
    }

    private TabSheet.SelectedTabChangeListener selectedTabListener(UI ui) {
        return event -> {
            AbstractComponent selectedTab = (AbstractComponent) event.getTabSheet().getSelectedTab();
            String state = (String) selectedTab.getData();
            ui.getPage().setUriFragment("!" + state, false);
        };
    }

    private TabSheet.CloseHandler tabCloseHandler(UI ui, TabSheet tabSheet) {
        return (tabsheet, tabContent) -> {
            TabSheet.Tab tabToClose = tabSheet.getTab(tabContent);
            if (tabToClose == null) {
                return;
            }
            final int currentPosition = tabsheet.getTabPosition(tabToClose);
            final boolean closingCurrentTab = Objects.equals(tabsheet.getSelectedTab(), tabContent);
            tabsheet.removeTab(tabToClose);
            if (closingCurrentTab) {
                // select next tab
                tabsheet.setSelectedTab(currentPosition);
            }
            if (tabsheet.getComponentCount() == 0) {
                // last tab removed
                ui.getPage().setUriFragment("", false);
            }
        };
    }

    private ViewChangeListener viewChangeListener(final TabSheet tabSheet) {
        return new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                Component view = (Component) event.getNewView();
                TabSheet.Tab tab = tabSheet.getTab(view);
                if (tab != null && !StringUtils.isBlank(view.getCaption())) {
                    tabSheet.setSelectedTab(view);
                    tab.setCaption(view.getCaption());
                }
            }
        };
    }

    private ViewDisplay viewDisplay(UI ui, TabSheet tabSheet) {
        return view -> {
            if (view instanceof ErrorView) {
                Notifications.errorNotification("View not found");
                return;
            }
            String state = ui.getNavigator().getState();
            for (Component tabComponent : tabSheet) {
                AbstractComponent otherview = (AbstractComponent) tabComponent;
                if (Objects.equals(state, otherview.getData())) {
                    tabSheet.setSelectedTab(tabComponent);
                    return;
                }
            }
            AbstractComponent viewAsComponent = (AbstractComponent) view;
            viewAsComponent.setData(state);
            TabSheet.Tab newTab = tabSheet.addTab(viewAsComponent, viewAsComponent instanceof HomeView ? 0 : tabSheet.getComponentCount());
            newTab.setCaption("...");
            newTab.setClosable(true);
        };
    }

    public void closeAllTabs() {
        ImmutableList<Component> components = ImmutableList.copyOf(tabSheet);
        components.forEach(c -> tabSheet.removeComponent(c));
        if (tabSheet.getComponentCount() == 0) {
            // last tab removed
            clearUriFragment();
        }
    }

    private void clearUriFragment() {
        ui.getPage().setUriFragment("", false);
    }

    public void closeOtherTabs() {
        ImmutableList<Component> components = ImmutableList.copyOf(tabSheet);
        components.stream().filter(c -> tabSheet.getSelectedTab() != c).forEach(c -> tabSheet.removeComponent(c));
    }

    public void closeCurrentTab() {
        String navigateTo = UrlUtils.getParam(ui.getPage().getUriFragment(), UrlUtils.NAVIGATE_TO);

        tabSheet.removeComponent(tabSheet.getSelectedTab());

        if (navigateTo != null) {
            ui.getNavigator().navigateTo(navigateTo);

            return;
        }

        if (tabSheet.getComponentCount() == 0) {
            // last tab removed
            clearUriFragment();
        } else {
            tabSheet.setSelectedTab(0);
        }
    }
}
