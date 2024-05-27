package fintech.spain.alfa.web.controllers.web;

import fintech.spain.alfa.product.web.model.PopupInfo;
import fintech.spain.alfa.product.web.spi.PopupService;
import fintech.web.api.models.OkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PopupApi {

    private final PopupService popupService;

    @Autowired
    public PopupApi(PopupService popupService) {
        this.popupService = popupService;
    }

    @PostMapping("/api/public/web/popup/{id}")
    public OkResponse update(@PathVariable int id, @RequestBody PopupInfo popupInfo) {
        popupService.resolve(id, popupInfo.getResolution());
        return OkResponse.OK;
    }
}
