package fintech.spain.alfa.product.testing;

import fintech.crm.documents.IdentityDocumentNumberUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.iban4j.IbanUtil;

public class RandomData {

    public static String randomEmail() {
        return RandomStringUtils.randomAlphabetic(8) + "@mailinator.com";
    }

    public static String randomPhoneNumber() {
        return RandomStringUtils.randomNumeric(8);
    }

    public static Iban randomIban() {
        return Iban.random(CountryCode.ES);
    }

    public static Iban randomIbanWithBankCode(String bankCode) {
        String number = bankCode + RandomStringUtils.randomNumeric(16);
        String code = IbanUtil.calculateCheckDigit("ES00" + number);
        String iban = "ES" + code + number;
        IbanUtil.validate(iban);
        return Iban.valueOf(iban);
    }

    public static String randomDni() {
        int number = RandomUtils.nextInt(10000000, 49999999);
        int modulus = number % 23;
        String dni = "" + number + IdentityDocumentNumberUtils.DNI_LETTER_ASSOCIATION.charAt(modulus);
        assert IdentityDocumentNumberUtils.isValidDni(dni);
        return dni;
    }

    public static void main(String[] args) {
        System.out.println(randomDni());
        System.out.println(randomIban());
    }
}
