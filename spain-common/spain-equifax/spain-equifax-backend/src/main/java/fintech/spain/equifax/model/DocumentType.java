package fintech.spain.equifax.model;

import fintech.Validate;

import static java.util.Arrays.stream;

public enum DocumentType {
    NIF("01", "[0-9]{8}[trwagmyfpdxbnjzsqvhlckeTRWAGMYFPDXBNJZSQVHLCKE]"),
    NIE("02", "[xyzXYZ][0-9]{7}[trwagmyfpdxbnjzsqvhlckeTRWAGMYFPDXBNJZSQVHLCKE]"),
    CIF("03", "[abcdefghjnpqrsuvwABCDEFGHJNPQRSUVW][0-9]{8}");

    private final String code;

    private final String regex;

    DocumentType(String code, String regex) {
        this.code = code;
        this.regex = regex;
    }

    public String getCode() {
        return code;
    }

    public static DocumentType getTypeOfDocumentNumber(final String documentNumber) {
        Validate.notBlank(documentNumber, "Document number required");

        DocumentType documentType = stream(values())
            .filter(type -> documentNumber.matches(type.regex))
            .findFirst()
            .orElse(null);
        Validate.isTrue(documentType != null, "Can not resolve document type of document number [%s]", documentNumber);
        return documentType;
    }
}
