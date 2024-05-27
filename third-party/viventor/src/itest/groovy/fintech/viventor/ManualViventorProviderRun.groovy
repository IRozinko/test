package fintech.viventor

import fintech.JsonUtils
import fintech.viventor.impl.ViventorProviderBean
import fintech.viventor.model.PostLoanPaidRequest
import fintech.viventor.model.PostLoanPaymentRequest
import fintech.viventor.model.PostLoanRequest
import fintech.viventor.model.ViventorLoan

import static fintech.DateUtils.date

class ManualViventorProviderRun {

    static void main(String[] args) {
        def provider = new ViventorProviderBean(
            "https://api.stage.viventor.com/lo/v2",
            "f292a1d2-e972-4a60-bdc2-1d57715fef15"
        )

        def response = provider.postLoan(JsonUtils.readValue(POST_LOAN_REQUEST, PostLoanRequest))
        println "Post New Loan Response:"
        println "---------------------------------------------------------------------------------"
        println response
        println response.responseBody

        response = provider.postLoanPaid("2428-1", new PostLoanPaidRequest(date("2017-12-01")))
        println "Post Loan Paid Response:"
        println "---------------------------------------------------------------------------------"
        println response
        println response.responseBody

        response = provider.getLoan("2428-1")
        println "Get Loan Response:"
        println "---------------------------------------------------------------------------------"
        println response
        println response.responseBody
        println JsonUtils.readValue(response.responseBody, ViventorLoan)

        response = provider.postLoanPayment("2428-1", new PostLoanPaymentRequest(1, date("2017-10-01")))
        println "Post Loan Payment Response:"
        println "---------------------------------------------------------------------------------"
        println response
        println response.responseBody

    }


    static String POST_LOAN_REQUEST = """
        {
          "loan" : {
            "id" : "2428-1",
            "type" : "LINE_OF_CREDIT",
            "currency" : "EUR",
            "amount" : 300.0000,
            "purpose" : null,
            "buyback" : true,
            "interest_rate" : 12.00,
            "start_date" : "2017-08-18",
            "maturity_date" : "2018-08-10",
            "country_code" : "ES",
            "payment_guarantee": true
          },
          "borrower" : {
            "consumer" : {
              "gender" : "FEMALE",
              "liabilities" : null,
              "income" : null,
              "dependants" : 1,
              "city" : "BARCELONA",
              "region" : "Test",
              "country" : "ES",
              "date_of_birth" : "1985-12-31",
              "loan_count" : null,
              "postal_code" : "08014"
            }
          },
          "schedule" : {
            "custom" : {
              "items" : [ {
                "number" : 1,
                "date" : "2017-09-05",
                "total" : 40.14,
                "principal" : 39.15,
                "interest" : 0.99,
                "remaining_principal" : 260.8500
              }, {
                "number" : 2,
                "date" : "2017-10-05",
                "total" : 24.15,
                "principal" : 21.49,
                "interest" : 2.66,
                "remaining_principal" : 239.3600
              }, {
                "number" : 3,
                "date" : "2017-11-05",
                "total" : 27.76,
                "principal" : 25.40,
                "interest" : 2.36,
                "remaining_principal" : 213.9600
              }, {
                "number" : 4,
                "date" : "2017-12-05",
                "total" : 29.13,
                "principal" : 26.95,
                "interest" : 2.18,
                "remaining_principal" : 187.0100
              }, {
                "number" : 5,
                "date" : "2018-01-05",
                "total" : 32.15,
                "principal" : 30.31,
                "interest" : 1.84,
                "remaining_principal" : 156.7000
              }, {
                "number" : 6,
                "date" : "2018-02-05",
                "total" : 34.14,
                "principal" : 32.54,
                "interest" : 1.60,
                "remaining_principal" : 124.1600
              }, {
                "number" : 7,
                "date" : "2018-03-05",
                "total" : 37.01,
                "principal" : 35.74,
                "interest" : 1.27,
                "remaining_principal" : 88.4200
              }, {
                "number" : 8,
                "date" : "2018-04-05",
                "total" : 41.26,
                "principal" : 40.45,
                "interest" : 0.81,
                "remaining_principal" : 47.9700
              }, {
                "number" : 9,
                "date" : "2018-05-05",
                "total" : 43.61,
                "principal" : 43.12,
                "interest" : 0.49,
                "remaining_principal" : 4.8500
              }, {
                "number" : 10,
                "date" : "2018-06-05",
                "total" : 4.9000,
                "principal" : 4.8500,
                "interest" : 0.05,
                "remaining_principal" : 0.0000
              } ],
              "prepaid_items" : 0,
              "grace_period" : 0
            }
          }
        }
    """

}
