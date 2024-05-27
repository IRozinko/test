package fintech.spain.alfa.bo.api;

import fintech.bo.api.model.address.AddAddressCatalogEntryRequest;
import fintech.bo.api.model.address.AddressCatalogEntry;
import fintech.bo.api.model.address.DeleteAddressCatalogEntryRequest;
import fintech.bo.api.model.address.EditAddressCatalogEntryRequest;
import fintech.bo.api.model.address.ImportAddressCatalogRequest;
import fintech.filestorage.CloudFile;
import fintech.spain.alfa.product.crm.AddressCatalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddressApiController {

    @Autowired
    private AddressCatalog addressCatalog;

    @PostMapping("/api/bo/address/add")
    public void addAddress(@RequestBody AddAddressCatalogEntryRequest request) {
        AddressCatalogEntry entry = request.getEntry();

        addressCatalog.saveAddress(entry.getPostalCode(), entry.getCity(), entry.getProvince(), entry.getState());
    }

    @PostMapping("/api/bo/address/edit")
    public void editAddress(@RequestBody EditAddressCatalogEntryRequest request) {
        AddressCatalogEntry entry = request.getEntry();

        addressCatalog.editAddress(request.getId(), entry.getPostalCode(), entry.getCity(), entry.getProvince(), entry.getState());
    }

    @PostMapping("/api/bo/address/delete")
    public void deleteAddress(@RequestBody DeleteAddressCatalogEntryRequest request) {
        addressCatalog.deleteAddress(request.getId());
    }

    @PostMapping("/api/bo/address/export")
    public CloudFile exportAddressCatalog() {
        return addressCatalog.exportAddressCatalog();
    }

    @PostMapping("/api/bo/address/import")
    public void importAddressCatalog(@RequestBody ImportAddressCatalogRequest request) {
        addressCatalog.importAddressCatalog(request.getFileId());
    }
}
