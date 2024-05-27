package fintech.bo.components.tabs;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;

import java.util.function.Supplier;

// http://stackoverflow.com/questions/27612837/strategy-for-lazy-loading-tab-content-in-a-tabsheet-in-vaadin-7
public class LazyTabSheet extends TabSheet {

    public LazyTabSheet() {
        addSelectedTabChangeListener(new LazyTabChangeListener());
    }

    public static LazyTab lazyTab(Supplier<Component> componentSupplier, String caption) {
        return new LazyTab(componentSupplier, caption);
    }

    public static class LazyTab extends CustomComponent {

        private Supplier<Component> componentSupplier;

        public LazyTab(Supplier<Component> componentSupplier, String caption) {
            this.componentSupplier = componentSupplier;
            this.setCaption(caption);
            this.setSizeFull();
        }

        public void setComponentSupplier(Supplier<Component> componentSupplier) {
            this.componentSupplier = componentSupplier;
        }

        public final void refresh() {
            setCompositionRoot(componentSupplier.get());
        }
    }

    private static class LazyTabChangeListener implements SelectedTabChangeListener {
        @Override
        public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
            Component selectedTab = event.getTabSheet().getSelectedTab();
            if (selectedTab instanceof LazyTab) {
                ((LazyTab) selectedTab).refresh();
            }
        }
    }
}
