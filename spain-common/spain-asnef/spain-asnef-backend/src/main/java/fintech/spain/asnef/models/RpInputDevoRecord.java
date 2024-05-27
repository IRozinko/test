package fintech.spain.asnef.models;

import fintech.spain.asnef.AsnefConstants;
import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.beanio.builder.Align;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Record
public class RpInputDevoRecord {

    @Field(length = 4)
    private String entityCode;

    @Field(length = 2, rid = true, literal = AsnefConstants.Rp.DEVO_RECORD_TYPE)
    private String typeOfRecord;

    @Field(length = 8)
    private LocalDate returnDate;

    @Field(length = 2)
    private String returnReason;

    @Field(length = 8)
    private LocalDate batchDate;

    @Field(length = 10)
    private String personIdentifier;

    @Field(length = 110)
    private String surnameAndName;

    @Field(length = 25)
    private String identifierOfOperation;

    @Field(length = 2)
    private String financialProductCode;

    @Field(length = 17, padding = '0', align = Align.RIGHT)
    private BigDecimal amountClaimed;

    @Field(length = 8)
    private LocalDate firstUnpaidPaymentDueDate;

    @Field(length = 2)
    private String natureOfPersonCode;

    @Field(length = 110)
    private String address;

    @Field(length = 50)
    private String cityTown;

    @Field(length = 2)
    private String provinceCode;

    @Field(length = 5)
    private String postalCode;

    @Field(length = 8)
    private String typeOfLetter;

    @Field(length = 4, minOccurs = 0)
    private String textCode;

    @Field(length = 500, minOccurs = 0)
    private String text;

    @Field(length = 500, minOccurs = 0)
    private String field1;
}
