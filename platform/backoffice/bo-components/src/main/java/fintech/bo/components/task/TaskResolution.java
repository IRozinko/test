package fintech.bo.components.task;

import com.google.common.collect.ImmutableList;
import com.vaadin.ui.Component;
import fintech.bo.api.model.task.CompleteTaskRequest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.collect.ImmutableList.of;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class TaskResolution {

    public static final String OTHER_RESOLUTION_DETAIL = "Other";
    public static final long POSTPONE_FOREVER = 2400L;
    public static final ImmutableList<Long> DEFAULT_POSTPONE_HOURS = of(1L, 2L, 4L, 8L, 24L, 48L, 120L, POSTPONE_FOREVER);

    private final String resolution;
    private final List<String> resolutionDetails;
    private final Function<TaskInput, CompleteTaskRequest> requestBuilder;

    private Supplier<Component> customComponentSupplier;
    private Function<TaskInput, Optional<String>> preCompleteValidation;
    private List<Long> postponeHours = DEFAULT_POSTPONE_HOURS;
    private boolean commentRequired;

    @Override
    public String toString() {
        return resolution;
    }

    public List<Long> getPostponeHours() {
        return Optional.ofNullable(postponeHours).orElse(Collections.emptyList());
    }

    public static class TaskResolutionBuilder {

        private String resolution;
        private List<String> resolutionDetails = of();
        private Function<TaskInput, CompleteTaskRequest> requestBuilder;
        private List<Long> postponeHours = DEFAULT_POSTPONE_HOURS;
        private boolean commentRequired;
        private Supplier<Component> customComponentSupplier;
        private Function<TaskInput, Optional<String>> preCompleteValidation;

        public static TaskResolutionBuilder resolution(String resolution) {
            TaskResolutionBuilder builder = new TaskResolutionBuilder();
            builder.resolution = resolution;
            return builder;
        }

        public TaskResolutionBuilder withDetails(List<String> resolutionDetails) {
            this.resolutionDetails = resolutionDetails;
            return this;
        }

        public TaskResolutionBuilder completeTask() {
            this.requestBuilder = TaskInput::complete;
            return this;
        }

        public TaskResolutionBuilder postponeTask() {
            this.requestBuilder = TaskInput::postpone;
            return this;
        }

        public TaskResolutionBuilder withPostponeHours(List<Long> postponeHours) {
            this.postponeHours = postponeHours;
            return this;
        }

        public TaskResolutionBuilder withCustomComponent(Supplier<Component> customComponentSupplier) {
            this.customComponentSupplier = customComponentSupplier;
            return this;
        }

        public TaskResolutionBuilder withPreCompleteValidation(
            Function<TaskInput, Optional<String>> preCompleteValidation) {
            this.preCompleteValidation = preCompleteValidation;
            return this;
        }

        public TaskResolutionBuilder commentRequired() {
            this.commentRequired = true;
            return this;
        }

        public TaskResolution build() {
            return new TaskResolution(resolution, resolutionDetails, requestBuilder, customComponentSupplier, preCompleteValidation, postponeHours, commentRequired);
        }
    }
}
