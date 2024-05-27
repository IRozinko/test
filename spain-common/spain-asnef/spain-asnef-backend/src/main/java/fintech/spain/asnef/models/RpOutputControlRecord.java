package fintech.spain.asnef.models;

import fintech.spain.asnef.AsnefConstants;
import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.beanio.builder.Align;

import java.time.LocalDate;

@Data
@Record
public class RpOutputControlRecord {

    @Field(length = 2, rid = true, literal = AsnefConstants.Rp.CONTROL_RECORD_TYPE)
    private String typeOfRecord = AsnefConstants.Rp.CONTROL_RECORD_TYPE;

    @Field(length = 4)
    private String reportingEntity;

    @Field(length = 8)
    private LocalDate batchDate;

    @Field(length = 9, padding = '0', align = Align.RIGHT)
    private int letterRecordsNumber;

    @Field(length = 9, padding = '0', align = Align.RIGHT)
    private int totalNumberOfRecords;
}
