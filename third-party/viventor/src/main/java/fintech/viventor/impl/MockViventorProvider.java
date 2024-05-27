package fintech.viventor.impl;

import com.google.common.collect.ImmutableMap;
import fintech.viventor.model.PostLoanExtensionRequest;
import fintech.viventor.model.PostLoanPaidRequest;
import fintech.viventor.model.PostLoanPaymentRequest;
import fintech.viventor.model.PostLoanRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component(MockViventorProvider.NAME)
@Lazy
public class MockViventorProvider implements ViventorProvider {

    public static final String NAME = "mock-viventor-provider";

    private final String url = "mock";

    private final String token = "mock";

    public MockViventorProvider() {

    }

    @Override
    public ViventorResponse postLoan(PostLoanRequest postLoanRequest) {
        log.info("Using MockViventorProvider! Posting loan [{}]", postLoanRequest);
        return response;
    }

    @Override
    public ViventorResponse postLoanPaid(String viventorLoanId, PostLoanPaidRequest postLoanPaidRequest) {
        log.info("Using MockViventorProvider! Posting loan paid [{}]", postLoanPaidRequest);
        return response;
    }

    @Override
    public ViventorResponse postLoanPayment(String viventorLoanId, PostLoanPaymentRequest postLoanPaymentRequest) {
        log.info("Using MockViventorProvider! Posting loan payment [{}]", postLoanPaymentRequest);
        return response;
    }

    @Override
    public ViventorResponse postLoanExtension(String viventorLoanId, PostLoanExtensionRequest postLoanExtensionRequest) {
        log.info("Using MockViventorProvider! Posting loan extension [{}]", postLoanExtensionRequest);
        return response;
    }

    @Override
    public ViventorResponse getLoan(String viventorLoanId) {
        if (getResponse.containsKey(viventorLoanId)) {
            return getResponse.get(viventorLoanId);
        }
        if (getResponse.containsKey("DEFAULT")) {
            return getResponse.get("DEFAULT");
        }
        throw new IllegalArgumentException("Viventor Mock, unable to find response for viventorLoanId " + viventorLoanId);
    }

    public final static String GET_LOC_LOAN = "{\n" +
        "  \"type\" : \"LINE_OF_CREDIT\",\n" +
        "  \"amount\" : 300.00,\n" +
        "  \"interest\" : 12.00,\n" +
        "  \"currency\" : \"EUR\",\n" +
        "  \"startDate\" : \"2017-08-18\",\n" +
        "  \"dueDate\" : \"2018-08-10\",\n" +
        "  \"placementDate\" : \"2017-11-02T17:51:56.795\",\n" +
        "  \"listingDate\" : \"2017-11-02T18:00:35.523\",\n" +
        "  \"closeDate\" : null,\n" +
        "  \"paymentSchedule\" : [ {\n" +
        "    \"number\" : 1,\n" +
        "    \"date\" : \"2017-09-05\",\n" +
        "    \"total\" : 40.14,\n" +
        "    \"principal\" : 39.15,\n" +
        "    \"interest\" : 0.99,\n" +
        "    \"remainingPrincipal\" : 260.85,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 2,\n" +
        "    \"date\" : \"2017-10-05\",\n" +
        "    \"total\" : 24.15,\n" +
        "    \"principal\" : 21.49,\n" +
        "    \"interest\" : 2.66,\n" +
        "    \"remainingPrincipal\" : 239.36,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 3,\n" +
        "    \"date\" : \"2017-11-05\",\n" +
        "    \"total\" : 27.76,\n" +
        "    \"principal\" : 25.40,\n" +
        "    \"interest\" : 2.36,\n" +
        "    \"remainingPrincipal\" : 213.96,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 4,\n" +
        "    \"date\" : \"2017-12-05\",\n" +
        "    \"total\" : 29.13,\n" +
        "    \"principal\" : 26.95,\n" +
        "    \"interest\" : 2.18,\n" +
        "    \"remainingPrincipal\" : 187.01,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 5,\n" +
        "    \"date\" : \"2018-01-05\",\n" +
        "    \"total\" : 32.15,\n" +
        "    \"principal\" : 30.31,\n" +
        "    \"interest\" : 1.84,\n" +
        "    \"remainingPrincipal\" : 156.70,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 6,\n" +
        "    \"date\" : \"2018-02-05\",\n" +
        "    \"total\" : 34.14,\n" +
        "    \"principal\" : 32.54,\n" +
        "    \"interest\" : 1.60,\n" +
        "    \"remainingPrincipal\" : 124.16,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 7,\n" +
        "    \"date\" : \"2018-03-05\",\n" +
        "    \"total\" : 37.01,\n" +
        "    \"principal\" : 35.74,\n" +
        "    \"interest\" : 1.27,\n" +
        "    \"remainingPrincipal\" : 88.42,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 8,\n" +
        "    \"date\" : \"2018-04-05\",\n" +
        "    \"total\" : 41.26,\n" +
        "    \"principal\" : 40.45,\n" +
        "    \"interest\" : 0.81,\n" +
        "    \"remainingPrincipal\" : 47.97,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 9,\n" +
        "    \"date\" : \"2018-05-05\",\n" +
        "    \"total\" : 43.61,\n" +
        "    \"principal\" : 43.12,\n" +
        "    \"interest\" : 0.49,\n" +
        "    \"remainingPrincipal\" : 4.85,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 10,\n" +
        "    \"date\" : \"2018-06-05\",\n" +
        "    \"total\" : 4.90,\n" +
        "    \"principal\" : 4.85,\n" +
        "    \"interest\" : 0.05,\n" +
        "    \"remainingPrincipal\" : 0.00,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 11,\n" +
        "    \"date\" : \"2017-11-02\",\n" +
        "    \"total\" : 265.82,\n" +
        "    \"principal\" : 260.85,\n" +
        "    \"interest\" : 4.97,\n" +
        "    \"remainingPrincipal\" : 0.00,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  } ],\n" +
        "  \"consumer\" : {\n" +
        "    \"gender\" : \"FEMALE\",\n" +
        "    \"street\" : null,\n" +
        "    \"building\" : null,\n" +
        "    \"flat\" : null,\n" +
        "    \"town\" : \"BARCELONA\",\n" +
        "    \"region\" : \"Test\",\n" +
        "    \"country\" : \"ES\",\n" +
        "    \"zipcode\" : \"08014\",\n" +
        "    \"loanCount\" : null,\n" +
        "    \"liabilities\" : null,\n" +
        "    \"income\" : null,\n" +
        "    \"age\" : 31\n" +
        "  },\n" +
        "  \"buyback\" : true,\n" +
        "  \"purpose\" : null,\n" +
        "  \"remainingPrincipal\" : 0.00,\n" +
        "  \"investmentAmount\" : 0,\n" +
        "  \"investmentCount\" : 0,\n" +
        "  \"currentExtensionNumber\" : 0\n" +
        "}";

    public final static String GET_LOC_LOAN_FIRST_INSTALLMENT_PAID = "{\n" +
        "  \"type\" : \"LINE_OF_CREDIT\",\n" +
        "  \"amount\" : 300.00,\n" +
        "  \"interest\" : 12.00,\n" +
        "  \"currency\" : \"EUR\",\n" +
        "  \"startDate\" : \"2017-08-18\",\n" +
        "  \"dueDate\" : \"2018-08-10\",\n" +
        "  \"placementDate\" : \"2017-11-02T17:51:56.795\",\n" +
        "  \"listingDate\" : \"2017-11-02T18:00:35.523\",\n" +
        "  \"closeDate\" : null,\n" +
        "  \"paymentSchedule\" : [ {\n" +
        "    \"number\" : 1,\n" +
        "    \"date\" : \"2017-09-05\",\n" +
        "    \"total\" : 40.14,\n" +
        "    \"principal\" : 39.15,\n" +
        "    \"interest\" : 0.99,\n" +
        "    \"remainingPrincipal\" : 260.85,\n" +
        "    \"status\" : \"PAID\"\n" +
        "  }, {\n" +
        "    \"number\" : 2,\n" +
        "    \"date\" : \"2017-10-05\",\n" +
        "    \"total\" : 24.15,\n" +
        "    \"principal\" : 21.49,\n" +
        "    \"interest\" : 2.66,\n" +
        "    \"remainingPrincipal\" : 239.36,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 3,\n" +
        "    \"date\" : \"2017-11-05\",\n" +
        "    \"total\" : 27.76,\n" +
        "    \"principal\" : 25.40,\n" +
        "    \"interest\" : 2.36,\n" +
        "    \"remainingPrincipal\" : 213.96,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 4,\n" +
        "    \"date\" : \"2017-12-05\",\n" +
        "    \"total\" : 29.13,\n" +
        "    \"principal\" : 26.95,\n" +
        "    \"interest\" : 2.18,\n" +
        "    \"remainingPrincipal\" : 187.01,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 5,\n" +
        "    \"date\" : \"2018-01-05\",\n" +
        "    \"total\" : 32.15,\n" +
        "    \"principal\" : 30.31,\n" +
        "    \"interest\" : 1.84,\n" +
        "    \"remainingPrincipal\" : 156.70,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 6,\n" +
        "    \"date\" : \"2018-02-05\",\n" +
        "    \"total\" : 34.14,\n" +
        "    \"principal\" : 32.54,\n" +
        "    \"interest\" : 1.60,\n" +
        "    \"remainingPrincipal\" : 124.16,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 7,\n" +
        "    \"date\" : \"2018-03-05\",\n" +
        "    \"total\" : 37.01,\n" +
        "    \"principal\" : 35.74,\n" +
        "    \"interest\" : 1.27,\n" +
        "    \"remainingPrincipal\" : 88.42,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 8,\n" +
        "    \"date\" : \"2018-04-05\",\n" +
        "    \"total\" : 41.26,\n" +
        "    \"principal\" : 40.45,\n" +
        "    \"interest\" : 0.81,\n" +
        "    \"remainingPrincipal\" : 47.97,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 9,\n" +
        "    \"date\" : \"2018-05-05\",\n" +
        "    \"total\" : 43.61,\n" +
        "    \"principal\" : 43.12,\n" +
        "    \"interest\" : 0.49,\n" +
        "    \"remainingPrincipal\" : 4.85,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 10,\n" +
        "    \"date\" : \"2018-06-05\",\n" +
        "    \"total\" : 4.90,\n" +
        "    \"principal\" : 4.85,\n" +
        "    \"interest\" : 0.05,\n" +
        "    \"remainingPrincipal\" : 0.00,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  }, {\n" +
        "    \"number\" : 11,\n" +
        "    \"date\" : \"2017-11-02\",\n" +
        "    \"total\" : 265.82,\n" +
        "    \"principal\" : 260.85,\n" +
        "    \"interest\" : 4.97,\n" +
        "    \"remainingPrincipal\" : 0.00,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  } ],\n" +
        "  \"consumer\" : {\n" +
        "    \"gender\" : \"FEMALE\",\n" +
        "    \"street\" : null,\n" +
        "    \"building\" : null,\n" +
        "    \"flat\" : null,\n" +
        "    \"town\" : \"BARCELONA\",\n" +
        "    \"region\" : \"Test\",\n" +
        "    \"country\" : \"ES\",\n" +
        "    \"zipcode\" : \"08014\",\n" +
        "    \"loanCount\" : null,\n" +
        "    \"liabilities\" : null,\n" +
        "    \"income\" : null,\n" +
        "    \"age\" : 31\n" +
        "  },\n" +
        "  \"buyback\" : true,\n" +
        "  \"purpose\" : null,\n" +
        "  \"remainingPrincipal\" : 0.00,\n" +
        "  \"investmentAmount\" : 0,\n" +
        "  \"investmentCount\" : 0,\n" +
        "  \"currentExtensionNumber\" : 0\n" +
        "}";

    public final static String GET_LOC_LOAN_BUYBACK = "{\n" +
        "  \"type\" : \"LINE_OF_CREDIT\",\n" +
        "  \"amount\" : 300.00,\n" +
        "  \"interest\" : 12.00,\n" +
        "  \"currency\" : \"EUR\",\n" +
        "  \"startDate\" : \"2017-08-18\",\n" +
        "  \"dueDate\" : \"2018-08-10\",\n" +
        "  \"placementDate\" : \"2017-11-02T17:51:56.795\",\n" +
        "  \"listingDate\" : \"2017-11-02T18:00:35.523\",\n" +
        "  \"closeDate\" : \"2017-11-02T19:00:00.391\",\n" +
        "  \"paymentSchedule\" : [ {\n" +
        "    \"number\" : 1,\n" +
        "    \"date\" : \"2017-09-05\",\n" +
        "    \"total\" : 40.14,\n" +
        "    \"principal\" : 39.15,\n" +
        "    \"interest\" : 0.99,\n" +
        "    \"remainingPrincipal\" : 260.85,\n" +
        "    \"status\" : \"PAID\"\n" +
        "  }, {\n" +
        "    \"number\" : 2,\n" +
        "    \"date\" : \"2017-10-05\",\n" +
        "    \"total\" : 24.15,\n" +
        "    \"principal\" : 21.49,\n" +
        "    \"interest\" : 2.66,\n" +
        "    \"remainingPrincipal\" : 239.36,\n" +
        "    \"status\" : \"CANCELLED\"\n" +
        "  }, {\n" +
        "    \"number\" : 3,\n" +
        "    \"date\" : \"2017-11-05\",\n" +
        "    \"total\" : 27.76,\n" +
        "    \"principal\" : 25.40,\n" +
        "    \"interest\" : 2.36,\n" +
        "    \"remainingPrincipal\" : 213.96,\n" +
        "    \"status\" : \"CANCELLED\"\n" +
        "  }, {\n" +
        "    \"number\" : 4,\n" +
        "    \"date\" : \"2017-12-05\",\n" +
        "    \"total\" : 29.13,\n" +
        "    \"principal\" : 26.95,\n" +
        "    \"interest\" : 2.18,\n" +
        "    \"remainingPrincipal\" : 187.01,\n" +
        "    \"status\" : \"CANCELLED\"\n" +
        "  }, {\n" +
        "    \"number\" : 5,\n" +
        "    \"date\" : \"2018-01-05\",\n" +
        "    \"total\" : 32.15,\n" +
        "    \"principal\" : 30.31,\n" +
        "    \"interest\" : 1.84,\n" +
        "    \"remainingPrincipal\" : 156.70,\n" +
        "    \"status\" : \"CANCELLED\"\n" +
        "  }, {\n" +
        "    \"number\" : 6,\n" +
        "    \"date\" : \"2018-02-05\",\n" +
        "    \"total\" : 34.14,\n" +
        "    \"principal\" : 32.54,\n" +
        "    \"interest\" : 1.60,\n" +
        "    \"remainingPrincipal\" : 124.16,\n" +
        "    \"status\" : \"CANCELLED\"\n" +
        "  }, {\n" +
        "    \"number\" : 7,\n" +
        "    \"date\" : \"2018-03-05\",\n" +
        "    \"total\" : 37.01,\n" +
        "    \"principal\" : 35.74,\n" +
        "    \"interest\" : 1.27,\n" +
        "    \"remainingPrincipal\" : 88.42,\n" +
        "    \"status\" : \"CANCELLED\"\n" +
        "  }, {\n" +
        "    \"number\" : 8,\n" +
        "    \"date\" : \"2018-04-05\",\n" +
        "    \"total\" : 41.26,\n" +
        "    \"principal\" : 40.45,\n" +
        "    \"interest\" : 0.81,\n" +
        "    \"remainingPrincipal\" : 47.97,\n" +
        "    \"status\" : \"CANCELLED\"\n" +
        "  }, {\n" +
        "    \"number\" : 9,\n" +
        "    \"date\" : \"2018-05-05\",\n" +
        "    \"total\" : 43.61,\n" +
        "    \"principal\" : 43.12,\n" +
        "    \"interest\" : 0.49,\n" +
        "    \"remainingPrincipal\" : 4.85,\n" +
        "    \"status\" : \"CANCELLED\"\n" +
        "  }, {\n" +
        "    \"number\" : 10,\n" +
        "    \"date\" : \"2018-06-05\",\n" +
        "    \"total\" : 4.90,\n" +
        "    \"principal\" : 4.85,\n" +
        "    \"interest\" : 0.05,\n" +
        "    \"remainingPrincipal\" : 0.00,\n" +
        "    \"status\" : \"CANCELLED\"\n" +
        "  }, {\n" +
        "    \"number\" : 11,\n" +
        "    \"date\" : \"2017-11-02\",\n" +
        "    \"total\" : 265.82,\n" +
        "    \"principal\" : 260.85,\n" +
        "    \"interest\" : 4.97,\n" +
        "    \"remainingPrincipal\" : 0.00,\n" +
        "    \"status\" : \"PAID\"\n" +
        "  } ],\n" +
        "  \"consumer\" : {\n" +
        "    \"gender\" : \"FEMALE\",\n" +
        "    \"street\" : null,\n" +
        "    \"building\" : null,\n" +
        "    \"flat\" : null,\n" +
        "    \"town\" : \"BARCELONA\",\n" +
        "    \"region\" : \"Test\",\n" +
        "    \"country\" : \"ES\",\n" +
        "    \"zipcode\" : \"08014\",\n" +
        "    \"loanCount\" : null,\n" +
        "    \"liabilities\" : null,\n" +
        "    \"income\" : null,\n" +
        "    \"age\" : 31\n" +
        "  },\n" +
        "  \"factoring\" : null,\n" +
        "  \"company\" : null,\n" +
        "  \"collateralRealty\" : null,\n" +
        "  \"collateralLicence\" : null,\n" +
        "  \"collateralInvoice\" : null,\n" +
        "  \"buyback\" : true,\n" +
        "  \"purpose\" : null,\n" +
        "  \"remainingPrincipal\" : 0.00,\n" +
        "  \"investmentAmount\" : 0,\n" +
        "  \"investmentCount\" : 0,\n" +
        "  \"currentExtensionNumber\" : 0\n" +
        "}";

    public final static String GET_CONSUMER_LOAN = "{\n" +
        "  \"type\" : \"CONSUMER\",\n" +
        "  \"amount\" : 200.00,\n" +
        "  \"interest\" : 10.50,\n" +
        "  \"currency\" : \"EUR\",\n" +
        "  \"startDate\" : \"2018-01-01\",\n" +
        "  \"dueDate\" : \"2018-01-31\",\n" +
        "  \"placementDate\" : \"2018-01-01T17:51:56.795\",\n" +
        "  \"listingDate\" : \"2018-01-01T18:00:35.523\",\n" +
        "  \"closeDate\" : null,\n" +
        "  \"paymentSchedule\" : [ {\n" +
        "    \"number\" : 1,\n" +
        "    \"date\" : \"2018-01-31\",\n" +
        "    \"total\" : 201.8,\n" +
        "    \"principal\" : 200,\n" +
        "    \"interest\" : 1.8,\n" +
        "    \"remainingPrincipal\" : 0,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  } ],\n" +
        "  \"consumer\" : {\n" +
        "    \"gender\" : \"FEMALE\",\n" +
        "    \"street\" : null,\n" +
        "    \"building\" : null,\n" +
        "    \"flat\" : null,\n" +
        "    \"town\" : \"BARCELONA\",\n" +
        "    \"region\" : \"Test\",\n" +
        "    \"country\" : \"ES\",\n" +
        "    \"zipcode\" : \"08014\",\n" +
        "    \"loanCount\" : null,\n" +
        "    \"liabilities\" : null,\n" +
        "    \"income\" : null,\n" +
        "    \"age\" : 31\n" +
        "  },\n" +
        "  \"buyback\" : true,\n" +
        "  \"purpose\" : null,\n" +
        "  \"remainingPrincipal\" : 200.00,\n" +
        "  \"investmentAmount\" : 0,\n" +
        "  \"investmentCount\" : 0,\n" +
        "  \"listingStatus\" : \"LISTED\",\n" +
        "  \"currentExtensionNumber\" : 0,\n" +
        "  \"previousExtensions\" : null\n" +
        "}";

    public final static String GET_CONSUMER_LOAN_EXT1 = "{\n" +
        "  \"type\" : \"CONSUMER\",\n" +
        "  \"amount\" : 200.00,\n" +
        "  \"interest\" : 10.50,\n" +
        "  \"currency\" : \"EUR\",\n" +
        "  \"startDate\" : \"2018-02-01\",\n" +
        "  \"dueDate\" : \"2018-03-02\",\n" +
        "  \"placementDate\" : \"2018-02-01T17:51:56.795\",\n" +
        "  \"listingDate\" : \"2018-02-01T18:00:35.523\",\n" +
        "  \"closeDate\" : null,\n" +
        "  \"paymentSchedule\" : [ {\n" +
        "    \"number\" : 1,\n" +
        "    \"date\" : \"2018-03-02\",\n" +
        "    \"total\" : 201.8,\n" +
        "    \"principal\" : 200,\n" +
        "    \"interest\" : 1.8,\n" +
        "    \"remainingPrincipal\" : 0,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  } ],\n" +
        "  \"consumer\" : {\n" +
        "    \"gender\" : \"FEMALE\",\n" +
        "    \"street\" : null,\n" +
        "    \"building\" : null,\n" +
        "    \"flat\" : null,\n" +
        "    \"town\" : \"BARCELONA\",\n" +
        "    \"region\" : \"Test\",\n" +
        "    \"country\" : \"ES\",\n" +
        "    \"zipcode\" : \"08014\",\n" +
        "    \"loanCount\" : null,\n" +
        "    \"liabilities\" : null,\n" +
        "    \"income\" : null,\n" +
        "    \"age\" : 31\n" +
        "  },\n" +
        "  \"buyback\" : true,\n" +
        "  \"purpose\" : null,\n" +
        "  \"remainingPrincipal\" : 200.00,\n" +
        "  \"investmentAmount\" : 0,\n" +
        "  \"investmentCount\" : 0,\n" +
        "  \"listingStatus\" : \"LISTED\",\n" +
        "  \"currentExtensionNumber\" : 0,\n" +
        "  \"previousExtensions\" : null\n" +
        "}";

    public final static String GET_CONSUMER_LOAN_EXT2 = "{\n" +
        "  \"type\" : \"CONSUMER\",\n" +
        "  \"amount\" : 200.00,\n" +
        "  \"interest\" : 10.50,\n" +
        "  \"currency\" : \"EUR\",\n" +
        "  \"startDate\" : \"2018-03-03\",\n" +
        "  \"dueDate\" : \"2018-04-01\",\n" +
        "  \"placementDate\" : \"2018-03-03T17:51:56.795\",\n" +
        "  \"listingDate\" : \"2018-03-03T18:00:35.523\",\n" +
        "  \"closeDate\" : null,\n" +
        "  \"paymentSchedule\" : [ {\n" +
        "    \"number\" : 1,\n" +
        "    \"date\" : \"2018-04-01\",\n" +
        "    \"total\" : 201.8,\n" +
        "    \"principal\" : 200,\n" +
        "    \"interest\" : 1.8,\n" +
        "    \"remainingPrincipal\" : 0,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  } ],\n" +
        "  \"consumer\" : {\n" +
        "    \"gender\" : \"FEMALE\",\n" +
        "    \"street\" : null,\n" +
        "    \"building\" : null,\n" +
        "    \"flat\" : null,\n" +
        "    \"town\" : \"BARCELONA\",\n" +
        "    \"region\" : \"Test\",\n" +
        "    \"country\" : \"ES\",\n" +
        "    \"zipcode\" : \"08014\",\n" +
        "    \"loanCount\" : null,\n" +
        "    \"liabilities\" : null,\n" +
        "    \"income\" : null,\n" +
        "    \"age\" : 31\n" +
        "  },\n" +
        "  \"buyback\" : true,\n" +
        "  \"purpose\" : null,\n" +
        "  \"remainingPrincipal\" : 200.00,\n" +
        "  \"investmentAmount\" : 0,\n" +
        "  \"investmentCount\" : 0,\n" +
        "  \"listingStatus\" : \"LISTED\",\n" +
        "  \"currentExtensionNumber\" : 0,\n" +
        "  \"previousExtensions\" : null\n" +
        "}";

    public final static String GET_CONSUMER_LOAN_BUYBACK = "{\n" +
        "  \"type\" : \"CONSUMER\",\n" +
        "  \"amount\" : 200.00,\n" +
        "  \"interest\" : 10.50,\n" +
        "  \"currency\" : \"EUR\",\n" +
        "  \"startDate\" : \"2018-01-01\",\n" +
        "  \"dueDate\" : \"2018-01-31\",\n" +
        "  \"placementDate\" : \"2018-01-01T17:51:56.795\",\n" +
        "  \"listingDate\" : \"2018-01-01T18:00:35.523\",\n" +
        "  \"closeDate\" : \"2018-02-28T00:40:35.523\",\n" +
        "  \"paymentSchedule\" : [ {\n" +
        "    \"number\" : 1,\n" +
        "    \"date\" : \"2018-01-31\",\n" +
        "    \"total\" : 201.8,\n" +
        "    \"principal\" : 200,\n" +
        "    \"interest\" : 1.8,\n" +
        "    \"remainingPrincipal\" : 0,\n" +
        "    \"status\" : \"PAID\"\n" +
        "  } ],\n" +
        "  \"consumer\" : {\n" +
        "    \"gender\" : \"FEMALE\",\n" +
        "    \"street\" : null,\n" +
        "    \"building\" : null,\n" +
        "    \"flat\" : null,\n" +
        "    \"town\" : \"BARCELONA\",\n" +
        "    \"region\" : \"Test\",\n" +
        "    \"country\" : \"ES\",\n" +
        "    \"zipcode\" : \"08014\",\n" +
        "    \"loanCount\" : null,\n" +
        "    \"liabilities\" : null,\n" +
        "    \"income\" : null,\n" +
        "    \"age\" : 31\n" +
        "  },\n" +
        "  \"buyback\" : true,\n" +
        "  \"purpose\" : null,\n" +
        "  \"remainingPrincipal\" : 0,\n" +
        "  \"investmentAmount\" : 0,\n" +
        "  \"investmentCount\" : 0,\n" +
        "  \"listingStatus\" : \"LISTED\",\n" +
        "  \"currentExtensionNumber\" : 0,\n" +
        "  \"previousExtensions\" : null\n" +
        "}";

    public final static String GET_CONSUMER_LOAN_EXTENDED = "{\n" +
        "  \"type\" : \"CONSUMER\",\n" +
        "  \"amount\" : 200.00,\n" +
        "  \"interest\" : 10.50,\n" +
        "  \"currency\" : \"EUR\",\n" +
        "  \"startDate\" : \"2018-01-01\",\n" +
        "  \"dueDate\" : \"2018-03-02\",\n" +
        "  \"placementDate\" : \"2018-01-31T17:51:56.795\",\n" +
        "  \"listingDate\" : \"2018-01-31T18:00:35.523\",\n" +
        "  \"closeDate\" : null,\n" +
        "  \"paymentSchedule\" : [ {\n" +
        "    \"number\" : 1,\n" +
        "    \"date\" : \"2018-03-02\",\n" +
        "    \"total\" : 203.6,\n" +
        "    \"principal\" : 200,\n" +
        "    \"interest\" : 3.6,\n" +
        "    \"remainingPrincipal\" : 0,\n" +
        "    \"status\" : \"PENDING\"\n" +
        "  } ],\n" +
        "  \"consumer\" : {\n" +
        "    \"gender\" : \"FEMALE\",\n" +
        "    \"street\" : null,\n" +
        "    \"building\" : null,\n" +
        "    \"flat\" : null,\n" +
        "    \"town\" : \"BARCELONA\",\n" +
        "    \"region\" : \"Test\",\n" +
        "    \"country\" : \"ES\",\n" +
        "    \"zipcode\" : \"08014\",\n" +
        "    \"loanCount\" : null,\n" +
        "    \"liabilities\" : null,\n" +
        "    \"income\" : null,\n" +
        "    \"age\" : 31\n" +
        "  },\n" +
        "  \"buyback\" : true,\n" +
        "  \"purpose\" : null,\n" +
        "  \"remainingPrincipal\" : 200.00,\n" +
        "  \"investmentAmount\" : 0,\n" +
        "  \"investmentCount\" : 0,\n" +
        "  \"listingStatus\" : \"LISTED\",\n" +
        "  \"currentExtensionNumber\" : 1,\n" +
        "  \"previousExtensions\" : [ {\n" +
        "    \"number\" : 0,\n" +
        "    \"dueDate\" : \"2018-01-31\"\n" +
        "  } ]\n" +
        "}";

    private ViventorResponse response = ViventorResponse.ok("mock", 200, "");

    private Map<String, ViventorResponse> getResponse = ImmutableMap.of("DEFAULT", ViventorResponse.ok("mock", 200, GET_LOC_LOAN));

    public void setResponse(ViventorResponse response) {
        this.response = response;
    }

    public void setGetResponse(Map<String, ViventorResponse> getResponse) {
        this.getResponse = getResponse;
    }

    public void setGetResponse(ViventorResponse getResponse) {
        this.getResponse = ImmutableMap.of("DEFAULT", getResponse);
    }
}
