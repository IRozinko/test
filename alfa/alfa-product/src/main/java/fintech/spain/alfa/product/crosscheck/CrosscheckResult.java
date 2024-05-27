package fintech.spain.alfa.product.crosscheck;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CrosscheckResult {

    private boolean found;
    private int openLoans;
    private int maxDpd;
    private boolean blacklisted;
    private boolean repeatedClient;
    private String clientNumber;
    private boolean activeRequest;
    private String activeRequestStatus;

    public static CrosscheckResult notFound() {
        return new CrosscheckResult().setFound(false);
    }
}
