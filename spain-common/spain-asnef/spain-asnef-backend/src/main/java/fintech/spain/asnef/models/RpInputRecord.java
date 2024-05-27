package fintech.spain.asnef.models;

import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.beanio.annotation.Segment;

@Data
@Record
public class RpInputRecord {

    @Field(length = 3)
    private String errorFileIdentifier;

    @Segment
    private RpOutputRecord originalRecord;

    @Field(length = 1)
    private String errorMark;

    @Field(length = 2)
    private String errorInTypeOfRecord;

    @Field(length = 2)
    private String errorInIdentifier;

    @Field(length = 2)
    private String errorInSurnameAndName;

    @Field(length = 2)
    private String errorInIdentifierOfOperation;

    @Field(length = 2)
    private String errorInFinancialProductCode;

    @Field(length = 2)
    private String errorInAmountClaimed;

    @Field(length = 2)
    private String errorInFirstUnpaidPaymentDueDate;

    @Field(length = 2)
    private String errorInNatureOfPersonCode;

    @Field(length = 2)
    private String errorInAddress;

    @Field(length = 2)
    private String errorInCityTown;

    @Field(length = 2)
    private String errorInProvinceCode;

    @Field(length = 2)
    private String errorInPostalCode;

    @Field(length = 2)
    private String errorInTypeOfLetter;

    @Field(length = 2)
    private String errorInTextCode;

    @Field(length = 2)
    private String errorInField1;
}
