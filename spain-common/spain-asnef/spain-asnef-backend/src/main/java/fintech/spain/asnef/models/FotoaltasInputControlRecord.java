package fintech.spain.asnef.models;

import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.beanio.annotation.Segment;
import org.beanio.builder.Align;

@Data
@Record
public class FotoaltasInputControlRecord {

    @Field(length = 3)
    private String errorFileIdentifier;

    @Segment
    private FotoaltasOutputHeaderRecord uploadFileHeaderRecord;

    @Field(length = 9, padding = '0', align = Align.RIGHT)
    private int numberOfErrorRecords;

    @Field(length = 9, padding = '0', align = Align.RIGHT)
    private int totalNumberOfRecords;
}
