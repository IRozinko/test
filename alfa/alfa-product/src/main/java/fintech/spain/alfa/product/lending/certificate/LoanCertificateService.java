package fintech.spain.alfa.product.lending.certificate;

import fintech.filestorage.CloudFile;

public interface LoanCertificateService {

    CloudFile generateCertificate(Long loanId, LoanCertificateType type);
}
