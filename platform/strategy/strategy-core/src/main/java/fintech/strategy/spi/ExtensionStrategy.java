package fintech.strategy.spi;


import fintech.strategy.model.ExtensionOffer;

import java.time.LocalDate;
import java.util.List;

public interface ExtensionStrategy {

    List<ExtensionOffer> getOffers(LocalDate onDate);

}
