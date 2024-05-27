package fintech.spain.asnef.models;

import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.beanio.annotation.Segment;
import org.beanio.builder.Align;

@Data
@Record
public class RpInputControlRecord {

    @Field(length = 3)
    private String errorFileIdentifier;

    @Segment
    private RpOutputHeaderRecord inputFileHeaderRecord;

    @Field(length = 9, padding = '0', align = Align.RIGHT)
    private int numberOfErrorRecords;

    @Field(length = 9, padding = '0', align = Align.RIGHT)
    private int totalNumberOfRecords;
}
