package fintech.spain.asnef.models;

import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.beanio.annotation.Segment;

@Data
@Record
public class FotoaltasInputHeaderRecord {

    @Field(length = 3)
    private String errorFileIdentifier;

    @Segment
    private FotoaltasOutputHeaderRecord updateFileHeaderRecord;
}
