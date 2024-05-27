package fintech.spain.alfa.web.controllers.internal;

import fintech.spain.alfa.product.crosscheck.CrosscheckFacade;
import fintech.spain.alfa.product.crosscheck.CrosscheckRequest;
import fintech.spain.alfa.product.crosscheck.CrosscheckResult;
import fintech.spain.alfa.product.web.WebAuthorities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
class CrosscheckApi {

    @Autowired
    private CrosscheckFacade crosscheckFacade;

    @Secured(WebAuthorities.INTERNAL)
    @PostMapping("/api/internal/crosscheck/client")
    public CrosscheckResult crosscheck(@RequestBody @Valid CrosscheckRequest request) {
        return crosscheckFacade.crosscheck(request);
    }
}
