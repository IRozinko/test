package fintech.spain.asnef.models;

import fintech.spain.asnef.AsnefConstants;
import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.beanio.builder.Align;

import java.time.LocalDate;

@Data
@Record
public class RpInputDevoControlRecord {

    @Field(length = 4)
    private String entityCode;

    @Field(length = 2, rid = true, literal = AsnefConstants.Rp.DEVO_CONTROL_RECORD_TYPE)
    private String typeOfRecord;

    @Field(length = 8)
    private LocalDate processDate;

    @Field(length = 9, padding = '0', align = Align.RIGHT)
    private int numberOfLetterRecords;

    @Field(length = 9, padding = '0', align = Align.RIGHT)
    private int totalNumberOfRecords;
}
