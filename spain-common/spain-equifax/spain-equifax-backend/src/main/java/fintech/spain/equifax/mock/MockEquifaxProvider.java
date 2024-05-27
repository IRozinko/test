package fintech.spain.equifax.mock;

import fintech.JsonUtils;
import fintech.spain.equifax.impl.EquifaxProvider;
import fintech.spain.equifax.model.EquifaxRequest;
import fintech.spain.equifax.model.EquifaxResponse;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Setter
@Component(MockEquifaxProvider.NAME)
public class MockEquifaxProvider implements EquifaxProvider {

    public static final String NAME = "mock-spain-equifax-provider";

    private Supplier<EquifaxResponse> responseSupplier = MockedEquifaxResponse.NOT_FOUND;
    private boolean throwError;

    @Override
    public EquifaxResponse request(EquifaxRequest request) {
        if (throwError) {
            throw new RuntimeException("Simulating Equifax failure");
        }
        EquifaxResponse equifaxResponse = responseSupplier.get();
        equifaxResponse.setRequestBody(JsonUtils.writeValueAsString(request));
        equifaxResponse.setResponseBody("Mocked value: " + JsonUtils.writeValueAsString(equifaxResponse));
        return equifaxResponse;
    }

}
