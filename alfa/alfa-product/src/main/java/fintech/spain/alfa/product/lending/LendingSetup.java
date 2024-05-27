package fintech.spain.alfa.product.lending;

import fintech.lending.core.loan.spi.LoanRegistry;
import fintech.lending.core.product.ProductType;
import fintech.lending.core.product.db.ProductEntity;
import fintech.lending.core.product.db.ProductRepository;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.lending.spi.AlfaDisbursementStrategy;
import fintech.spain.alfa.product.lending.spi.AlfaLoanDerivedValuesResolver;
import fintech.spain.alfa.product.lending.spi.AlfaLoanIssueStrategy;
import fintech.spain.alfa.product.lending.spi.AlfaRepaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
public class LendingSetup {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LoanRegistry loanRegistry;

    @Autowired
    private AlfaDisbursementStrategy disbursementStrategy;

    @Autowired
    private AlfaRepaymentStrategy repaymentStrategy;

    @Autowired
    private AlfaLoanIssueStrategy loanIssueStrategy;

    @Autowired
    private AlfaLoanDerivedValuesResolver derivedValuesResolver;

    public void setUp() {
        initProduct();
        loanRegistry.addDisbursementStrategy(AlfaConstants.PRODUCT_ID, disbursementStrategy);
        loanRegistry.addRepaymentStrategy(AlfaConstants.PRODUCT_ID, repaymentStrategy);
        loanRegistry.addLoanIssueStrategy(AlfaConstants.PRODUCT_ID, loanIssueStrategy);
        loanRegistry.addLoanDerivedValueResolver(AlfaConstants.PRODUCT_ID, derivedValuesResolver);
    }

    private void initProduct() {
        ProductEntity entity = productRepository.findOne(AlfaConstants.PRODUCT_ID);
        if (entity == null) {
            log.info("Adding new product");
            entity = new ProductEntity();
            entity.setId(AlfaConstants.PRODUCT_ID);
            entity.setProductType(ProductType.PAYDAY);
            entity.setDefaultSettingsJson("{}");
            productRepository.saveAndFlush(entity);
        } else {
            log.info("Product already exists, skipping");
        }
    }
}
