package fintech.spain.asnef.models;

import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.beanio.annotation.Segment;

@Data
@Record
public class RpInputHeaderRecord implements RpInputHeader {

    @Field(length = 3)
    private String errorFileIdentifier;

    @Segment
    private RpOutputHeaderRecord inputFileHeaderRecord;

    @Override
    public RpType getType() {
        return RpType.ERROR;
    }
}
