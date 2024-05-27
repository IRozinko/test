package fintech.bo.api.server;

import com.google.common.collect.Lists;
import fintech.BigDecimalUtils;
import fintech.bo.api.model.IdRequest;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.filestorage.FileStorageService;
import fintech.lending.core.discount.ApplyDiscountCommand;
import fintech.lending.core.discount.DiscountService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class DiscountApiController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DiscountService discountService;

    @PostMapping("/api/bo/discounts/apply-batch")
    public void apply(@RequestBody IdRequest request) {
        List<Discount> discounts = fileStorageService.readContents(request.getId(), this::read);

        discounts.forEach(discount -> {
            Optional<Client> maybeClient = clientService.findByClientNumber(discount.getClientNumber());

            if (!maybeClient.isPresent()) {
                log.info("Unable to find client by client number [{}]", discount.getClientNumber());

                return;
            }

            ApplyDiscountCommand command = new ApplyDiscountCommand();
            command.setClientId(maybeClient.get().getId());
            command.setRateInPercent(BigDecimalUtils.amount(discount.getRateInPercent()));
            command.setEffectiveFrom(LocalDate.parse(discount.getEffectiveFrom()));
            command.setEffectiveTo(LocalDate.parse(discount.getEffectiveTo()));
            command.setMaxTimesToApply(Integer.valueOf(discount.getMaxTimesToApply()));

            discountService.applyDiscount(command);
        });
    }

    @SneakyThrows
    private List<Discount> read(InputStream input) {
        CsvBeanReader reader = new CsvBeanReader(new InputStreamReader(input), CsvPreference.STANDARD_PREFERENCE);

        String[] header = reader.getHeader(true);

        Discount discount;
        List<Discount> discounts = Lists.newArrayList();

        while ((discount = reader.read(Discount.class, header)) != null) {
            discounts.add(discount);
        }

        reader.close();

        return discounts;
    }
}
