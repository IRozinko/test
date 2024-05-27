package fintech.spain.asnef.models;

import fintech.spain.asnef.AsnefConstants;
import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;

import java.time.LocalDate;

@Data
@Record
public class FotoaltasOutputHeaderRecord {

    @Field(length = 6, rid = true, literal = AsnefConstants.Fotoaltas.HEADER_RECORD_TYPE)
    private String recordType = AsnefConstants.Fotoaltas.HEADER_RECORD_TYPE;

    @Field(length = 4)
    private String reportingEntity;

    @Field(length = 8)
    private String fileIdentifier;

    @Field(length = 8)
    private LocalDate dateOfProcessing;

    @Field(length = 8)
    private LocalDate closingDateOfAccounts;
}
