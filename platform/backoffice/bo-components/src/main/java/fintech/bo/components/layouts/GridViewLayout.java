package fintech.bo.components.layouts;


import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import fintech.bo.components.common.SearchField;

public class GridViewLayout extends CustomComponent {

    private final VerticalLayout layout;
    private MenuBar.MenuItem actionsMenuItem;
    private MenuBar.MenuItem refreshMenuItem;
    private HorizontalLayout globalFilters;
    private HorizontalLayout topComponents;
    private Label separator;
    private HorizontalLayout top;
    private MenuBar menu;
    private Component content;

    public GridViewLayout() {
        layout = new VerticalLayout();

        globalFilters = new HorizontalLayout();
        globalFilters.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        globalFilters.setVisible(false);

        separator = new Label("<hr />", ContentMode.HTML);
        separator.setWidth(100, Unit.PERCENTAGE);
        separator.setVisible(false);

        layout.addComponent(globalFilters);
        layout.addComponent(separator);

        layout.addComponent(buildTopLayout());

        layout.setMargin(false);
        setCompositionRoot(layout);
    }

    private AbstractLayout buildTopLayout() {
        menu = new MenuBar();
        actionsMenuItem = menu.addItem("Actions", null);
        actionsMenuItem.setVisible(false);
        refreshMenuItem = menu.addItem("Refresh", null);
        refreshMenuItem.setVisible(false);

        topComponents = new HorizontalLayout();
        topComponents.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);

        top = new HorizontalLayout();
        top.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        top.setMargin(false);
        top.addComponent(topComponents);
        top.addComponentsAndExpand(new Label());
        top.addComponent(menu);
        return top;
    }

    public MenuBar.MenuItem addActionMenuItem(String caption, MenuBar.Command command) {
        actionsMenuItem.setVisible(true);
        return actionsMenuItem.addItem(caption, command);
    }

    public MenuBar.MenuItem setRefreshAction(MenuBar.Command command) {
        refreshMenuItem.setVisible(true);
        refreshMenuItem.setCommand(command);
        return refreshMenuItem;
    }

    public MenuBar.MenuItem addMenuBarItem(String caption, MenuBar.Command command) {
        return menu.addItemBefore(caption, null, command, actionsMenuItem);
    }

    public void hideTop(boolean hide) {
        top.setVisible(!hide);
    }

    public void addTopComponent(Component component) {
        topComponents.addComponent(component);
    }

    public void addGlobalFilter(Component component) {
        globalFilters.addComponent(component);

        globalFilters.setVisible(true);
        separator.setVisible(true);
    }

    public TextField searchField() {
        TextField search = new TextField();
        search.setCaption("Search");
        search.setPlaceholder("Search...");
        search.setWidth(200, Unit.PIXELS);
        search.setValueChangeMode(ValueChangeMode.TIMEOUT);
        search.focus();
        return search;
    }

    public SearchField searchFieldWithOptions() {
        SearchField search = new SearchField();
        search.setCaption("Search");
        return search;
    }

    public void setContent(Component component) {
        if (this.content != null) {
            layout.removeComponent(this.content);
        }
        this.content = component;
        this.content.setSizeFull();
        layout.addComponentsAndExpand(this.content);
    }

    public void setRefreshEnabled(boolean enabled) {
        refreshMenuItem.setEnabled(enabled);
    }
}
