package fintech.crm.country.impl;

public class CountryNotValidException extends RuntimeException {
    public CountryNotValidException(String message) {
        super(message);
    }
}
