package fintech.marketing;

public enum MarketingCampaignStatus {

    ACTIVE, PAUSED;

    public MarketingCampaignStatus toggle() {
        return this == ACTIVE ? PAUSED : ACTIVE;
    }
}
