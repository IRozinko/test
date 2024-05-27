package fintech.spain.asnef.models;

import fintech.spain.asnef.AsnefConstants;
import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.beanio.builder.Align;

import java.time.LocalDate;

@Data
@Record
public class FotoaltasOutputControlRecord {

    @Field(length = 6, rid = true, literal = AsnefConstants.Fotoaltas.CONTROL_RECORD_TYPE)
    private String recordType = AsnefConstants.Fotoaltas.CONTROL_RECORD_TYPE;

    @Field(length = 4)
    private String reportingEntity;

    @Field(length = 8)
    private String fileIdentifier;

    @Field(length = 8)
    private LocalDate dateOfProcessing;

    @Field(length = 9, padding = '0', align = Align.RIGHT)
    private int numberOfOperations;

    @Field(length = 9, padding = '0', align = Align.RIGHT)
    private int totalNumberOfRecords;
}
