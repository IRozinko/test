package fintech.viventor.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ViventorBorrowerConsumer {

    private String gender;

    private String street;

    private String building;

    private String flat;

    private String town;

    private String region;

    private String country;

    private String zipcode;

    private Integer loanCount;

    private BigDecimal liabilities;

    private BigDecimal income;

    private Integer age;
}
