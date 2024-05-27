package fintech.spain.asnef.models;

import fintech.spain.asnef.AsnefConstants;
import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;

import java.time.LocalDate;

@Data
@Record
public class RpInputDevoHeaderRecord implements RpInputHeader {

    @Field(length = 4)
    private String reportingEntity;

    @Field(length = 2, rid = true, literal = AsnefConstants.Rp.DEVO_HEADER_RECORD_TYPE)
    private String typeOfRecord;

    @Field(length = 8)
    private LocalDate processDate;

    @Override
    public RpType getType() {
        return RpType.DEVO;
    }
}
