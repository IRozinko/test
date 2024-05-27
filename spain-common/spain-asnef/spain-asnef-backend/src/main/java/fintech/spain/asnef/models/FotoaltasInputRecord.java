package fintech.spain.asnef.models;

import lombok.Data;
import org.beanio.annotation.Field;
import org.beanio.annotation.Record;
import org.beanio.annotation.Segment;

@Data
@Record
public class FotoaltasInputRecord {

    @Field(length = 3)
    private String errorFileIdentifier;

    @Segment
    private FotoaltasOutputRecord originalRecord;

    @Field(length = 2)
    private String errorInRecordType;

    @Field(length = 2)
    private String errorInOperationIdentifier;

    @Field(length = 2)
    private String errorInStartDateOfOperation;

    @Field(length = 2)
    private String errorInEndDateOfOperation;

    @Field(length = 2)
    private String errorInFinancialProductCode;

    @Field(length = 2)
    private String errorInNominalAmountOfOperation;

    @Field(length = 2)
    private String errorInNominalAmountOfOperationCurrencyType;

    @Field(length = 2)
    private String errorInNumberOfPayments;

    @Field(length = 2)
    private String errorInCodeOfPaymentsFrequency;

    @Field(length = 2)
    private String errorInAmountOfPayments;

    @Field(length = 2)
    private String errorInAmountOfPaymentsCurrencyType;

    @Field(length = 2)
    private String errorInAmountNotYetDue;

    @Field(length = 2)
    private String errorInAmountNotYetDueCurrencyType;

    @Field(length = 2)
    private String errorInSituationOfOperationCode;

    @Field(length = 2)
    private String errorInNumberOfPaymentsCurrentlyOverdue;

    @Field(length = 2)
    private String errorInDueDateOfFirstCurrentlyUnpaidPayment;

    @Field(length = 2)
    private String errorInDueDateOfLastCurrentlyUnpaidPayment;

    @Field(length = 2)
    private String errorInTotalAmountCurrentlyUnpaid;

    @Field(length = 2)
    private String errorInTotalAmountCurrentlyUnpaidCurrencyType;

    @Field(length = 2)
    private String errorInSupplementaryInformation;

    @Field(length = 2)
    private String errorInNatureOfPersonCode;

    @Field(length = 2)
    private String errorInPersonIdentifier;

    @Field(length = 2)
    private String errorInNameFormat;

    @Field(length = 2)
    private String errorInSurnamesForename;

    @Field(length = 2)
    private String errorInNotifyIndicator;

    @Field(length = 2)
    private String errorInCountryCodeOfNationality;

    @Field(length = 2)
    private String errorInNationalOccupationCode;

    @Field(length = 2)
    private String errorInNationalBusinessActivityCode;

    @Field(length = 2)
    private String errorInAddressFormat;

    @Field(length = 2)
    private String errorInAddress;

    @Field(length = 2)
    private String errorInTownCode;

    @Field(length = 2)
    private String errorInNameOfTown;

    @Field(length = 2)
    private String errorInProvinceCode;

    @Field(length = 2)
    private String errorInCountryCode;

    @Field(length = 2)
    private String errorInPostalCode;

    @Field(length = 2)
    private String errorInFormatOfTelephoneNumber;

    @Field(length = 2)
    private String errorInTelephoneNumber;
}
