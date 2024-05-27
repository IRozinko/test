package fintech.spain.alfa.product.affiliate;

public enum AffiliateApplicationStatus {
    PENDING, ACCEPTED, DECLINED, COMPLETED, FAILED, NOT_FOUND;

    public boolean isOk() {
        return this == PENDING || this == ACCEPTED || this == COMPLETED;
    }
}
