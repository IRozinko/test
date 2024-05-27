package fintech.bo.components.task;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.Validate;
import fintech.bo.api.model.task.CompleteTaskRequest;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static fintech.bo.components.task.TaskResolution.POSTPONE_FOREVER;

public class CompleteTaskComponent extends VerticalLayout {

    private final Button completeButton;
    private final ComboBox<TaskResolution> resolutionComboBox;
    private final ComboBox<String> detailComboBox;
    private final TextField subDetailText;
    private final TextArea commentText;
    private final TaskRecord task;
    private final Label resolutionInfo;
    private final VerticalLayout customComponentPlaceholder;
    private final ComboBox<Long> postponeByHours;
    private Component customComponent;
    private Function<TaskInput, Optional<String>> preCompleteValidation;

    public CompleteTaskComponent(TaskRecord task, List<TaskResolution> resolutions) {
        this.task = task;

        Label titleLabel = new Label("Complete task");
        titleLabel.addStyleName(ValoTheme.LABEL_H4);
        titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.addStyleName(ValoTheme.LABEL_COLORED);
        addComponent(titleLabel);

        resolutionComboBox = new ComboBox<>("Resolution");
        resolutionComboBox.setItems(resolutions);
        resolutionComboBox.setTextInputAllowed(false);
        resolutionComboBox.setPlaceholder("Select resolution");
        resolutionComboBox.setWidth(100, Unit.PERCENTAGE);
        resolutionComboBox.setRequiredIndicatorVisible(true);
        addComponent(resolutionComboBox);

        postponeByHours = new ComboBox<>("Postpone by hours");
        postponeByHours.setWidth(100, Unit.PERCENTAGE);
        postponeByHours.setRequiredIndicatorVisible(true);
        postponeByHours.setVisible(false);
        postponeByHours.setTextInputAllowed(false);
        postponeByHours.setItemCaptionGenerator(hours -> {
            if (hours == POSTPONE_FOREVER) {
                return "Move out of task queue";
            } else if (hours < 24) {
                return hours + " hour";
            } else {
                return (hours / 24) + " day";
            }
        });
        postponeByHours.setEmptySelectionAllowed(false);
        addComponent(postponeByHours);

        resolutionInfo = new Label();
        resolutionInfo.addStyleName(ValoTheme.LABEL_SMALL);
        resolutionInfo.setVisible(false);
        addComponent(resolutionInfo);

        detailComboBox = new ComboBox<>("Detail");
        detailComboBox.setTextInputAllowed(false);
        detailComboBox.setPlaceholder("Select resolution");
        detailComboBox.setWidth(100, Unit.PERCENTAGE);
        detailComboBox.setVisible(false);
        detailComboBox.setRequiredIndicatorVisible(true);
        addComponent(detailComboBox);

        customComponentPlaceholder = new VerticalLayout();
        customComponentPlaceholder.setMargin(false);
        customComponentPlaceholder.setVisible(false);
        customComponentPlaceholder.setWidth(100, Unit.PERCENTAGE);
        addComponent(customComponentPlaceholder);

        subDetailText = new TextField("Other");
        subDetailText.setWidth(100, Unit.PERCENTAGE);
        subDetailText.setVisible(false);
        subDetailText.setRequiredIndicatorVisible(true);
        addComponent(subDetailText);

        commentText = new TextArea("Comment");
        commentText.setWidth(100, Unit.PERCENTAGE);
        commentText.setRows(3);

        addComponent(commentText);

        completeButton = new Button("Complete");
        completeButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        completeButton.setEnabled(false);
        addComponent(completeButton);

        resolutionComboBox.addValueChangeListener(e -> completeButton.setEnabled(e.getValue() != null));
        resolutionComboBox.addValueChangeListener(e -> {
            TaskResolution resolution = e.getValue();
            resolutionChanged(resolution);
        });

        detailComboBox.addValueChangeListener(e -> {
            subDetailText.setVisible(TaskResolution.OTHER_RESOLUTION_DETAIL.equals(e.getValue()));
            subDetailText.setValue("");
            subDetailText.focus();
        });

        commentText.addValueChangeListener(e -> {
            Boolean commentRequired = Optional.ofNullable(resolutionComboBox.getValue())
                .map(TaskResolution::isCommentRequired)
                .orElse(false);
            completeButton.setEnabled(!commentRequired || !e.getValue().isEmpty());
        });

        setMargin(false);
        setWidth(400, Sizeable.Unit.PIXELS);
    }

    private void resolutionChanged(TaskResolution resolution) {
        detailComboBox.setVisible(false);
        detailComboBox.setValue(null);
        resolutionInfo.setVisible(false);
        customComponentPlaceholder.removeAllComponents();
        customComponentPlaceholder.setVisible(false);
        customComponent = null;
        postponeByHours.setValue(null);
        postponeByHours.setVisible(false);

        if (resolution == null) {
            return;
        }

        preCompleteValidation = resolution.getPreCompleteValidation();

        CompleteTaskRequest request = resolution.getRequestBuilder().apply(buildTaskInput());
        if (request.isPostpone()) {
            postponeByHours.setItems(resolution.getPostponeHours());
            postponeByHours.setValue(request.getPostponeByHours());
            postponeByHours.setVisible(true);

            resolutionInfo.setValue(String.format("Task postponed %s times before", task.getTimesPostponed()));
        } else {
            postponeByHours.setItems(Collections.emptyList());
            postponeByHours.setValue(null);
            postponeByHours.setVisible(false);
            resolutionInfo.setValue("Task will be closed");
        }
        resolutionInfo.setVisible(true);

        List<String> details = resolution.getResolutionDetails();
        detailComboBox.setItems(details);
        detailComboBox.setVisible(!details.isEmpty());


        if (resolution.getCustomComponentSupplier() != null) {
            customComponent = resolution.getCustomComponentSupplier().get();
            customComponentPlaceholder.addComponent(customComponent);
            customComponentPlaceholder.setVisible(true);
        }

        commentText.setRequiredIndicatorVisible(resolution.isCommentRequired());

        completeButton.setEnabled(!resolution.isCommentRequired() || !commentText.getValue().isEmpty());
    }

    public Button getCompleteButton() {
        return completeButton;
    }

    public Function<TaskInput, Optional<String>> getPreCompleteValidation() {
        return preCompleteValidation;
    }

    public ComboBox<TaskResolution> getResolutionComboBox() {
        return resolutionComboBox;
    }

    public TaskInput buildTaskInput() {
        TaskInput input = new TaskInput();
        input.setTask(task);
        input.setResolution(resolutionComboBox.getValue());
        input.setResolutionDetail(detailComboBox.getValue());
        input.setResolutionSubDetail(subDetailText.getValue());
        input.setComment(commentText.getValue());
        input.setCustomComponent(customComponent);

        Long postponeByHoursValue = Optional.ofNullable(postponeByHours.getValue())
            .orElseGet(() -> resolutionComboBox.getValue().getPostponeHours().iterator().next());

        Validate.notNull(postponeByHoursValue, "Postpone hours not entered");
        input.setPostponeByHours(postponeByHoursValue);
        return input;
    }

}
