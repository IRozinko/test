package fintech.bo.components.dc;


import com.vaadin.ui.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueueInfoPanel extends CustomComponent {

    private Map<String, QueueInfoComponent> queues;
    private HorizontalLayout layout;
    private Label emptyQueues;

    public QueueInfoPanel(List<DcSettingsJson.Portfolio> portfolios) {
        layout = new HorizontalLayout();
        emptyQueues = new Label("No debts to process");

        queues = portfolios.stream()
            .map(DcSettingsJson.Portfolio::getName)
            .map(p -> new QueueInfoComponent(p, 0))
            .peek(QueueInfoComponent::hide)
            .peek(comp -> layout.addComponent(comp))
            .collect(Collectors.toMap(QueueInfoComponent::getPortfolio, comp -> comp));

        layout.addComponents(emptyQueues);

        setCompositionRoot(layout);
    }

    public void setCount(String portfolio, int count) {
        QueueInfoComponent portfolioQueue = queues.get(portfolio);
        portfolioQueue.setCount(count);

        if (count == 0)
            portfolioQueue.hide();
        else
            portfolioQueue.show();
    }

    public void refresh() {
        if (queues.values().stream().anyMatch(AbstractComponent::isVisible))
            emptyQueues.setVisible(false);
        else
            emptyQueues.setVisible(true);
    }

    public void addQueueBtnClickListener(Function<String, Button.ClickListener> clickListenerFunction) {
        queues.values().forEach(comp -> comp.addClickListener(clickListenerFunction.apply(comp.getPortfolio())));
    }
}
