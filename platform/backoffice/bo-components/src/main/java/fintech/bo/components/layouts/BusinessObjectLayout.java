package fintech.bo.components.layouts;

import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.common.Tab;
import fintech.bo.components.tabs.LazyTabSheet;
import fintech.bo.components.utils.UrlUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.function.Supplier;

public class BusinessObjectLayout extends CustomComponent {

    private final MenuBar.MenuItem refreshMenuItem;
    private final MenuBar.MenuItem actionsMenuItem;
    private final Label title;
    private final LazyTabSheet tabSheet;
    private final VerticalLayout left;
    private final HorizontalSplitPanel splitPanel;
    private RefreshAction refreshAction;

    private final Params params;
    private boolean autoSelectedFromUriFragment;

    public BusinessObjectLayout() {
        this(new Params().setAutoSelectFromUriFragment(true));
    }

    public BusinessObjectLayout(Params params) {
        this.params = params;
        left = new VerticalLayout();
        left.setMargin(false);
        left.setSpacing(false);
        left.setSizeUndefined();
        left.setWidth(100, Unit.PERCENTAGE);

        Panel leftPanel = new Panel();
        leftPanel.setContent(left);
        leftPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        leftPanel.setSizeFull();
        leftPanel.addStyleName(BackofficeTheme.BACKGROUND_GRAY);

        tabSheet = new LazyTabSheet();
        tabSheet.setSizeFull();
        if (params.isAutoSelectFromUriFragment()) {
            tabSheet.addSelectedTabChangeListener((TabSheet.SelectedTabChangeListener) event -> {
                if (event.isUserOriginated()) {
                    Page.getCurrent().setUriFragment(UrlUtils.appendToFragment(Page.getCurrent().getUriFragment(), UrlUtils.TAB, event.getTabSheet().getSelectedTab().getCaption()), false);
                }
            });
        }

        VerticalLayout right = new VerticalLayout();
        right.setSizeFull();
        right.setMargin(true);
        right.addComponentsAndExpand(tabSheet);

        splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(500, Unit.PIXELS);
        splitPanel.setFirstComponent(leftPanel);
        splitPanel.setSecondComponent(right);
        splitPanel.setSizeFull();

        MenuBar menu = new MenuBar();
        actionsMenuItem = menu.addItem("Actions", null);
        actionsMenuItem.setVisible(false);
        refreshMenuItem = menu.addItem("Refresh", null);
        refreshMenuItem.setVisible(false);
        refreshMenuItem.setCommand(e -> this.refresh());

        title = new Label();
        title.addStyleName(ValoTheme.LABEL_LARGE);
        title.addStyleName(ValoTheme.LABEL_BOLD);

        HorizontalLayout top = new HorizontalLayout();
        top.addStyleName("bo-form-top-menu-layout");
        top.setSpacing(true);
        top.setMargin(new MarginInfo(true, true, false, true));
        top.addComponentsAndExpand(title);
        top.addComponent(menu);

        VerticalLayout root = new VerticalLayout();
        root.addComponent(top);
        root.addComponentsAndExpand(splitPanel);
        root.setMargin(false);
        root.setSpacing(false);
        setCompositionRoot(root);
    }

    @Override
    public void attach() {
        super.attach();
        if (params.isAutoSelectFromUriFragment()) {
            String tabUriFragment = UrlUtils.getParam(Page.getCurrent().getUriFragment(), UrlUtils.TAB);
            if (tabUriFragment != null) {
                autoSelectedFromUriFragment = selectTab(tabUriFragment);
            }
        }
    }

    public boolean selectTab(String caption) {
        for (Component tab : tabSheet) {
            if (StringUtils.equals(UrlUtils.toParamValue(tab.getCaption()), UrlUtils.toParamValue(caption))) {
                tabSheet.setSelectedTab(tab);
                return true;
            }
        }
        return false;
    }

    public MenuBar.MenuItem setRefreshAction(RefreshAction refreshAction) {
        this.refreshAction = refreshAction;
        refreshMenuItem.setVisible(true);
        return refreshMenuItem;
    }

    public MenuBar.MenuItem addActionMenuItem(String caption, MenuBar.Command command) {
        actionsMenuItem.setVisible(true);
        return actionsMenuItem.addItem(caption, command);
    }

    public void setTitle(String title) {
        this.title.setValue(title);
    }

    public void setTitleRed(String title) {
        this.title.setValue(title);
        this.title.addStyleName(ValoTheme.LABEL_COLORED);
        this.title.addStyleName(BackofficeTheme.TEXT_DANGER);
    }

    public TabSheet.Tab addTab(String caption, Supplier<Component> tabSupplier) {
        return tabSheet.addTab(LazyTabSheet.lazyTab(() -> {
            VerticalLayout layout = new VerticalLayout(tabSupplier.get());
            layout.setSizeFull();
            layout.setMargin(new MarginInfo(true, false, false, false));
            return layout;
        }, caption), caption);
    }

    public TabSheet.Tab addTab(String caption, Tab tab) {
        return tabSheet.addTab(LazyTabSheet.lazyTab(() -> {
            VerticalLayout layout = new VerticalLayout(tab.build());
            layout.setSizeFull();
            layout.setMargin(new MarginInfo(true, false, false, false));
            return layout;
        }, caption), caption);
    }

    public void addLeftComponent(Component component) {
        left.addComponent(component);
    }

    public void setSplitPosition(long pixels) {
        splitPanel.setSplitPosition(pixels, Unit.PIXELS);
    }

    public VerticalLayout getLeft() {
        return left;
    }

    public boolean isAutoSelectedFromUriFragment() {
        return autoSelectedFromUriFragment;
    }

    public void refresh() {
        if (refreshAction != null) refreshAction.refresh();
    }

    @FunctionalInterface
    public interface RefreshAction extends Serializable {
        void refresh();
    }

    @Data
    @Accessors(chain = true)
    public static class Params {
        private boolean autoSelectFromUriFragment = true;
    }
}
