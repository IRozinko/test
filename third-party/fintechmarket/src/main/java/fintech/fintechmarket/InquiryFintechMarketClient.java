package fintech.fintechmarket;

import fintech.fintechmarket.dto.NewInquiryResponse;
import fintech.fintechmarket.dto.StartInquiryRequest;
import fintech.fintechmarket.dto.StartInquiryResponse;

public interface InquiryFintechMarketClient {

    NewInquiryResponse newInquiry(String scenarioKey, String brand);

    StartInquiryResponse startInquiry(String scenarioKey, StartInquiryRequest request, String brand);

}
