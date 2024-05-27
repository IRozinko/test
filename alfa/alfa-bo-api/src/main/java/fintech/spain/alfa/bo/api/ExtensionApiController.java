package fintech.spain.alfa.bo.api;


import com.google.common.annotations.VisibleForTesting;
import fintech.Validate;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.IdResponse;
import fintech.spain.alfa.bo.model.CreateExtensionDiscountRequest;
import fintech.bo.api.model.loan.ExtensionPrice;
import fintech.bo.api.model.loan.GetExtensionPricesRequest;
import fintech.bo.api.model.loan.GetExtensionPricesResponse;
import fintech.bo.api.model.payments.AddExtensionTransactionRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.spain.alfa.product.extension.ApplyAndRepayExtensionFeeCommand;
import fintech.spain.alfa.product.extension.ExtensionService;
import fintech.spain.alfa.product.extension.discounts.CreateExtensionDiscountCommand;
import fintech.spain.alfa.product.extension.discounts.ExtensionDiscountService;
import fintech.strategy.model.ExtensionOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ExtensionApiController {

    @Autowired
    private ExtensionService extensionService;

    @Autowired
    private ExtensionDiscountService extensionDiscountService;

    @PostMapping("/api/bo/loan/extension-prices")
    public GetExtensionPricesResponse getExtensionPrices(@Valid @RequestBody GetExtensionPricesRequest request) {
        List<ExtensionOffer> extensions = extensionService.listOffersForLoan(request.getLoanId(), request.getDate());

        List<ExtensionPrice> prices = extensions.stream().map(offer -> {
            ExtensionPrice price = new ExtensionPrice();
            price.setPeriodCount(offer.getPeriodCount());
            price.setPeriodUnit(offer.getPeriodUnit().name());
            price.setPrice(offer.getPrice());
            price.setPriceWithDiscount(offer.getPriceWithDiscount());
            price.setDiscountAmount(offer.getDiscountAmount());
            price.setDiscountPercent(offer.getDiscountPercent());
            return price;
        }).collect(Collectors.toList());

        GetExtensionPricesResponse response = new GetExtensionPricesResponse();
        response.setExtensions(prices);
        return response;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.PAYMENT_ADD_TRANSACTION})
    @PostMapping("/api/bo/payments/add-extension-transaction")
    public IdResponse addExtensionTransaction(@Valid @RequestBody AddExtensionTransactionRequest request) {
        BigDecimal total = request.getOverpaymentAmount().add(request.getPaymentAmount());
        Optional<ExtensionOffer> offer = extensionService.findOfferForLoan(request.getLoanId(), total, true, request.getValueDate());
        Validate.isTrue(offer.isPresent(), "Extension offer not found for amount [%s]", total);

        Long txId = extensionService.applyAndRepayExtensionFee(toExtensionCommand(request, offer.get()));

        return new IdResponse(txId);
    }

    @PostMapping("/api/bo/extension-discount")
    public IdResponse create(@Valid @RequestBody CreateExtensionDiscountRequest request) {
        CreateExtensionDiscountCommand command = new CreateExtensionDiscountCommand()
            .setLoanId(request.getLoanId())
            .setEffectiveFrom(request.getEffectiveFrom())
            .setEffectiveTo(request.getEffectiveTo())
            .setRateInPercent(request.getRateInPercent());

        Long extensionDiscountId = extensionDiscountService.createExtensionDiscount(command);
        return new IdResponse(extensionDiscountId);
    }

    @PostMapping("/api/bo/extension-discount/delete")
    public void delete(@Valid @RequestBody IdRequest request) {
        extensionDiscountService.deleteExtensionDiscount(request.getId());
    }

    @PostMapping("/api/bo/extension-discount/activate")
    public void activate(@Valid @RequestBody IdRequest request) {
        extensionDiscountService.activateExtensionDiscount(request.getId());
    }

    @PostMapping("/api/bo/extension-discount/deactivate")
    public void deactivate(@Valid @RequestBody IdRequest request) {
        extensionDiscountService.deactivateExtensionDiscount(request.getId());
    }

    @VisibleForTesting
    protected ApplyAndRepayExtensionFeeCommand toExtensionCommand(AddExtensionTransactionRequest request, ExtensionOffer offer) {
        return new ApplyAndRepayExtensionFeeCommand()
            .setLoanId(request.getLoanId())
            .setExtensionOffer(offer)
            .setComments(request.getComments())
            .setPaymentAmount(request.getPaymentAmount())
            .setOverpaymentAmount(request.getOverpaymentAmount())
            .setPaymentId(request.getPaymentId())
            .setValueDate(request.getValueDate());
    }
}
