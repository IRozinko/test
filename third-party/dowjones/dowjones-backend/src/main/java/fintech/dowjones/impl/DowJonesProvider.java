package fintech.dowjones.impl;

import fintech.dowjones.DowJonesRequestData;
import fintech.dowjones.DowJonesResponseData;

public interface DowJonesProvider {

    DowJonesResponseData search(DowJonesRequestData request);
}
