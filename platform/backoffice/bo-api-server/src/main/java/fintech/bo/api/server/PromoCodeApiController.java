package fintech.bo.api.server;

import com.google.common.collect.Lists;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.loan.CreatePromoCodeRequest;
import fintech.bo.api.model.loan.EditPromoCodeRequest;
import fintech.bo.api.model.loan.UpdatePromoCodeClientsRequest;
import fintech.bo.api.model.loan.UpdatePromoCodeSourcesRequest;
import fintech.filestorage.FileStorageService;
import fintech.lending.core.promocode.CreatePromoCodeCommand;
import fintech.lending.core.promocode.PromoCodeService;
import fintech.lending.core.promocode.UpdatePromoCodeCommand;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import javax.validation.Valid;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RestController
public class PromoCodeApiController {

    @Autowired
    private PromoCodeService promoCodeService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/api/bo/promo-codes")
    public IdResponse create(@Valid @RequestBody CreatePromoCodeRequest request) {
        CreatePromoCodeCommand command = new CreatePromoCodeCommand()
            .setCode(request.getCode())
            .setDescription(request.getDescription())
            .setEffectiveFrom(request.getEffectiveFrom())
            .setEffectiveTo(request.getEffectiveTo())
            .setRateInPercent(request.getRateInPercent())
            .setSources(request.getSources())
            .setMaxTimesToApply(request.getMaxTimesToApply());

        if (request.getClientFileId() != null) {
            List<String> clientNumbers = fileStorageService.readContents(request.getClientFileId(), this::read);
            command.setClientNumbers(clientNumbers);
        }

        Long promoCodeId = promoCodeService.create(command);
        return new IdResponse(promoCodeId);
    }

    @PostMapping("/api/bo/promo-codes/edit")
    public void edit(@Valid @RequestBody EditPromoCodeRequest request) {
        promoCodeService.update(new UpdatePromoCodeCommand()
            .setPromoCodeId(request.getPromoCodeId())
            .setDescription(request.getDescription())
            .setEffectiveFrom(request.getEffectiveFrom())
            .setEffectiveTo(request.getEffectiveTo())
            .setRateInPercent(request.getRateInPercent())
            .setMaxTimesToApply(request.getMaxTimesToApply())
            .setSources(request.getSources())
        );
    }

    @PostMapping("/api/bo/promo-codes/delete")
    public void delete(@Valid @RequestBody IdRequest request) {
        promoCodeService.delete(request.getId());
    }

    @PostMapping("/api/bo/promo-codes/activate")
    public void activate(@Valid @RequestBody IdRequest request) {
        promoCodeService.activate(request.getId());
    }

    @PostMapping("/api/bo/promo-codes/deactivate")
    public void deactivate(@Valid @RequestBody IdRequest request) {
        promoCodeService.deactivate(request.getId());
    }

    @PostMapping("/api/bo/promo-codes/update-clients")
    public void updateClients(@Valid @RequestBody UpdatePromoCodeClientsRequest request) {
        List<String> clientNumbers = fileStorageService.readContents(request.getClientFileId(), this::read);
        promoCodeService.updateClients(request.getPromoCodeId(), clientNumbers);
    }

    @SneakyThrows
    private List<String> read(InputStream input) {
        CsvBeanReader reader = new CsvBeanReader(new InputStreamReader(input), CsvPreference.STANDARD_PREFERENCE);

        String[] header = reader.getHeader(true);

        PromoCodeClient promoCodeClient;
        List<String> clientNumbers = Lists.newArrayList();

        while ((promoCodeClient = reader.read(PromoCodeClient.class, header)) != null) {
            clientNumbers.add(promoCodeClient.getClientNumber());
        }

        reader.close();
        return clientNumbers;
    }

    @Data
    public static class PromoCodeClient {
        private String clientNumber;
    }

}
