package fintech.bo.spain.alfa.strategy;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Component;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.spain.alfa.bo.model.PreviewCalculationStrategyCmsItemRequest;
import fintech.strategy.bo.StrategyViewTab;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@org.springframework.stereotype.Component
public class CalculationStrategyCmsRenderTab implements StrategyViewTab {

    @Override
    public String getCaption() {
        return "CMS Item Preview";
    }

    @Override
    public Component component(CalculationStrategyRecord strategy) {
        try {
            StreamResource resource = new StreamResource(() -> {
                try {
                    Call<ResponseBody> call = ApiAccessor.gI().get(AlfaApiClient.class)
                        .renderStrategyCmsItem(new PreviewCalculationStrategyCmsItemRequest().setCalculationStrategyId(strategy.getId()));
                    Response<ResponseBody> fileResponse = call.execute();
                    if (fileResponse.isSuccessful()) {
                        return new ByteArrayInputStream(fileResponse.body().bytes());
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, "strategy_preview.pdf");

            BrowserFrame browserFrame = new BrowserFrame("", resource);
            browserFrame.setSizeFull();
            return browserFrame;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
