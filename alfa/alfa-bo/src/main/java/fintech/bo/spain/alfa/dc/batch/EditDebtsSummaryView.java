package fintech.bo.spain.alfa.dc.batch;

import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import fintech.bo.components.dialogs.InfoDialog;
import fintech.bo.components.notifications.Notifications;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;

public class EditDebtsSummaryView {

    private final List<Error> errors;

    public EditDebtsSummaryView(List<Error> errors) {
        this.errors = errors;
    }

    public void show() {
        if (!errors.isEmpty()) {
            UI.getCurrent().addWindow(new InfoDialog("Errors", createGrid()));
        }
        Notifications.trayNotification("Completed");
    }

    private Grid<Error> createGrid() {
        Grid<Error> grid = new Grid<>();
        grid.setCaption("<span style=\"color: red;\">Failed to change company:<span>");
        grid.setCaptionAsHtml(true);
        grid.setWidth(100, PERCENTAGE);
        grid.addColumn(Error::getDebtId).setCaption("Debt id").setWidth(100);
        grid.addColumn(Error::getErrorMessage).setCaption("Error");
        grid.setItems(errors);
        return grid;
    }

    @Data
    @Builder
    public static class Error {
        private Long debtId;
        private String errorMessage;
    }
}
