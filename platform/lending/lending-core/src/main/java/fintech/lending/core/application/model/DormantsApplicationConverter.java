package fintech.lending.core.application.model;

import fintech.lending.core.application.LoanApplication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DormantsApplicationConverter implements Converter<LoanApplication, DormantsApplicationData> {
    @Override
    public DormantsApplicationData convert(LoanApplication source) {
        DormantsApplicationData dormantsApplicationData = new DormantsApplicationData();
        dormantsApplicationData.setCreditLimit(source.getCreditLimit());
        return dormantsApplicationData;
    }
}
