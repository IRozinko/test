package fintech.spain.alfa.bo.api;

import fintech.filestorage.CloudFile;
import fintech.spain.alfa.bo.model.LoanCertificateRequest;
import fintech.spain.alfa.product.lending.certificate.LoanCertificateType;
import fintech.spain.alfa.product.lending.certificate.LoanCertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoanCertificateApi {

    private final LoanCertificateService service;

    @PostMapping("/api/bo/loan/{loanId}/certificate")
    public CloudFile exportAddressCatalog(@PathVariable Long loanId,
                                          @RequestBody @Valid LoanCertificateRequest request) {
        return service.generateCertificate(loanId, LoanCertificateType.valueOf(request.getCertificateType()));
    }
}
