package fintech.bo.components.background;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class GlassPanel extends Window implements BackgroundOperationFeedback {

    private final ProgressBar progressBar;
    private final Label messageLabel;

    public GlassPanel(String title) {
        setModal(true);
        setClosable(false);
        setWidth(500, Sizeable.Unit.PIXELS);
        setHeight(150, Sizeable.Unit.PIXELS);

        VerticalLayout controls = new VerticalLayout();
        progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setWidth(200, Unit.PIXELS);
        controls.addComponent(progressBar);
        controls.setComponentAlignment(progressBar, Alignment.MIDDLE_CENTER);

        Label titleLabel = new Label(title);
        titleLabel.addStyleName(ValoTheme.LABEL_LARGE);
        titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        controls.addComponent(titleLabel);
        controls.setComponentAlignment(titleLabel, Alignment.MIDDLE_CENTER);

        messageLabel = new Label();
        controls.addComponent(messageLabel);
        controls.setComponentAlignment(messageLabel, Alignment.MIDDLE_CENTER);

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(controls);
        layout.setComponentAlignment(controls, Alignment.MIDDLE_CENTER);

        layout.setSizeFull();
        setContent(layout);
    }

    @Override
    public void update(String message, float progress) {
        getUI().access(() -> {
            if (progress >= 0) {
                progressBar.setIndeterminate(false);
                progressBar.setValue(progress);
            }
            messageLabel.setValue(message);
        });
    }

}
