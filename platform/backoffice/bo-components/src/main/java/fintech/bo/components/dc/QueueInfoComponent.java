package fintech.bo.components.dc;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.BackofficeTheme;
import lombok.Getter;

public class QueueInfoComponent extends CustomComponent {

    @Getter
    private int count;

    @Getter
    private String portfolio;

    private Label countLabel;
    private Button btn;
    private GridLayout layout;


    public QueueInfoComponent(String portfolio, int count) {

        this.count = count;
        this.portfolio = portfolio;

        layout = new GridLayout(1, 2);
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        layout.setWidth(100, Unit.PERCENTAGE);

        countLabel = new Label();
        countLabel.setWidthUndefined();
        setCount(count);
        countLabel.addStyleNames(BackofficeTheme.TEXT_DANGER, ValoTheme.LABEL_BOLD);
        layout.addComponent(this.countLabel,0, 0);

        btn = new Button(portfolio);
        btn.setWidthUndefined();
        btn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        layout.addComponent(btn, 0, 1);

        setCompositionRoot(layout);
    }

    public void addClickListener(Button.ClickListener clickListener) {
        btn.addClickListener(clickListener);
    }

    public void setCount(int count) {
        countLabel.setValue(String.format("%s debts", count));
    }

    public void disable() {
        btn.setEnabled(false);
    }

    public void enable() {
        btn.setEnabled(true);
    }

    public void show() {
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }


}
