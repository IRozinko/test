package fintech.fintechmarket.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FintechMarketSettings {

    public static final String FINTECT_MARKET_SETTINGS = "FintechMarketSettings";

    private String brand;

}
