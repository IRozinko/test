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
public class FotoaltasOutputRecord {

    @Field(length = 6, rid = true, literal = AsnefConstants.Fotoaltas.OUTPUT_RECORD_TYPE)
    private String recordType = AsnefConstants.Fotoaltas.OUTPUT_RECORD_TYPE;

    @Field(length = 25)
    private String operationIdentifier;

    @Field(length = 8)
    private LocalDate startDateOfOperation;

    @Field(length = 8)
    private LocalDate endDateOfOperation;

    @Field(length = 2, defaultValue = AsnefConstants.FINANCIAL_PRODUCT_CODE)
    private String financialProductCode;

    @Field(length = 15, padding = '0', align = Align.RIGHT)
    private BigDecimal nominalAmountOfOperation;

    @Field(length = 2, defaultValue = AsnefConstants.Fotoaltas.CURRENCY_TYPE_EURO)
    private String nominalAmountOfOperationCurrencyType;

    @Field(length = 4)
    private int numberOfPayments;

    @Field(length = 2)
    private String codeOfPaymentsFrequency;

    @Field(length = 15, padding = '0', align = Align.RIGHT)
    private BigDecimal amountOfPayments;

    @Field(length = 2, defaultValue = AsnefConstants.Fotoaltas.CURRENCY_TYPE_EURO)
    private String amountOfPaymentsCurrencyType;

    @Field(length = 15, padding = '0', align = Align.RIGHT)
    private BigDecimal amountNotYetDue;

    @Field(length = 2, defaultValue = AsnefConstants.Fotoaltas.CURRENCY_TYPE_EURO)
    private String amountNotYetDueCurrencyType;

    @Field(length = 2, defaultValue = AsnefConstants.Fotoaltas.OPERATION_SITUATION_CODE_OTHER)
    private String situationOfOperationCode;

    @Field(length = 4)
    private int numberOfPaymentsCurrentlyOverdue;

    @Field(length = 8)
    private LocalDate dueDateOfFirstCurrentlyUnpaidPayment;

    @Field(length = 8)
    private LocalDate dueDateOfLastCurrentlyUnpaidPayment;

    @Field(length = 15, padding = '0', align = Align.RIGHT, required = true)
    private BigDecimal totalAmountCurrentlyUnpaid;

    @Field(length = 2, defaultValue = AsnefConstants.Fotoaltas.CURRENCY_TYPE_EURO)
    private String totalAmountCurrentlyUnpaidCurrencyType;

    @Field(length = 40)
    private String supplementaryInformation;

    @Field(length = 2, defaultValue = AsnefConstants.NATURE_OF_PERSON_CODE)
    private String natureOfPersonCode;

    @Field(length = 10)
    private String personIdentifier;

    @Field(length = 1, defaultValue = AsnefConstants.Fotoaltas.NAME_FORMAT)
    private String nameFormat;

    @Field(length = 80)
    private String lastName;

    @Field(length = 30)
    private String firstName;

    @Field(length = 8, defaultValue = AsnefConstants.Fotoaltas.NOTIFY_INDICATOR)
    private String notifyIndicator;

    @Field(length = 3, defaultValue = AsnefConstants.Fotoaltas.COUNTRY_CODE_SPAIN)
    private String countryCodeOfNationality;

    @Field(length = 5)
    private String nationalOccupationCode;

    @Field(length = 5)
    private String nationalBusinessActivityCode;

    @Field(length = 1, defaultValue = AsnefConstants.Fotoaltas.ADDRESS_FORMAT)
    private String addressFormat;

    @Field(length = 5)
    private String typeOfRoad;

    @Field(length = 60)
    private String roadName;

    @Field(length = 5)
    private String roadNumber;

    @Field(length = 40)
    private String restOfRoad;

    @Field(length = 6)
    private String townCode;

    @Field(length = 50)
    private String nameOfTown;

    @Field(length = 2)
    private String provinceCode;

    @Field(length = 3, defaultValue = AsnefConstants.Fotoaltas.COUNTRY_CODE_SPAIN)
    private String countryCode;

    @Field(length = 5)
    private String postalCode;

    @Field(length = 1, defaultValue = AsnefConstants.Fotoaltas.PHONE_FORMAT)
    private String formatOfTelephoneNumber;

    @Field(length = 6, defaultValue = AsnefConstants.Fotoaltas.PHONE_COUNTRY_CODE_SPAIN)
    private String phoneCountryCode;

    @Field(length = 14)
    private String phoneNumber;
}
