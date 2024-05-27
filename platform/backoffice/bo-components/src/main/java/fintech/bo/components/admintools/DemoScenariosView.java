package fintech.bo.components.admintools;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fintech.admintools.ScenarioInfo;
import fintech.bo.api.client.AdminToolsApiClient;
import fintech.bo.api.model.admintools.RunDemoScenarioRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import fintech.retrofit.RetrofitHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SecuredView({BackofficePermissions.ADMIN})
@Slf4j
@SpringView(name = DemoScenariosView.NAME)
public class DemoScenariosView extends VerticalLayout implements View {

    public static final String NAME = "demo";

    @Autowired
    private AdminToolsApiClient apiClient;

    private VerticalLayout scenariosLayout;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Demo Scenarios");
        GridViewLayout layout = new GridViewLayout();
        layout.setRefreshAction((e) -> refresh());
        scenariosLayout = new VerticalLayout();
        Panel panel = new Panel();
        panel.setContent(scenariosLayout);
        layout.setContent(panel);
        addComponentsAndExpand(layout);
        scenariosLayout.setSizeUndefined();
        refresh();
    }

    protected void refresh() {
        scenariosLayout.removeAllComponents();
        Call<List<ScenarioInfo>> call = apiClient.listDemoScenarios();
        Optional<List<ScenarioInfo>> scenarios = RetrofitHelper.syncCall(call);
        scenarios.ifPresent(response -> {
            response.forEach(s -> this.addScenario(s.getName(), s.getRequiredParameters()));
        });
    }

    private void addScenario(String name, List<String> requiredProperties) {
        Button button = new Button(name);
        button.addClickListener(e -> onRunClickAction(name, requiredProperties));
        scenariosLayout.addComponent(button);
    }

    private void onRunClickAction(String name, List<String> requiredProperties) {
        if (CollectionUtils.isEmpty(requiredProperties)) {
            runScenario(name);
        } else {
            fetchParametersAndRunScenario(name, requiredProperties);
        }
    }

    private void runScenario(String name) {
        runScenario(name, null);
    }

    private void runScenario(String name, Map<String, String> parameters) {
        Call<Void> call = apiClient.runDemoScenario(new RunDemoScenarioRequest().setName(name).setParameters(parameters));
        BackgroundOperations.callApi("Running scenario", call, t -> {
            Notifications.trayNotification("Completed");
        }, Notifications::errorNotification);
    }

    private void fetchParametersAndRunScenario(String name, List<String> requiredProperties) {
        Window parametersWindow = new Window("Please add test parameters:");
        List<TextArea> textAreas = textAreas(requiredProperties);
        FormLayout content = buildParametersContent(textAreas);
        Button addButton = new Button("Add");
        addButton.addClickListener((e) -> {
            parametersWindow.close();
            runScenario(name, mapToParameterValues(textAreas));
        });
        content.addComponent(addButton);
        parametersWindow.center();
        parametersWindow.setModal(true);
        parametersWindow.setContent(content);
        parametersWindow.setResizable(false);
        getUI().addWindow(parametersWindow);
    }

    private FormLayout buildParametersContent(List<TextArea> textAreas) {
        FormLayout content = new FormLayout();
        content.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        content.setMargin(true);
        content.setSpacing(true);
        textAreas.forEach(content::addComponent);
        return content;
    }

    private Map<String, String> mapToParameterValues(List<TextArea> textAreas) {
        return textAreas.stream().collect(Collectors.toMap(AbstractTextField::getCaption, AbstractTextField::getValue));
    }

    private List<TextArea> textAreas(List<String> requiredProperties) {
        return requiredProperties.stream().map(TextArea::new).peek(t -> {
            t.setWidth(10, Unit.CM);
            t.setHeight(2, Unit.CM);
        }).collect(Collectors.toList());
    }
}
