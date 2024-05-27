package fintech.spain.alfa.web.controllers.web;

import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class LocalizationApi {

    @Autowired
    private CmsRegistry cmsRegistry;


    @GetMapping(value = "/api/public/web/localizations/{item}/{locale}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLocalizedStrings(@PathVariable(name = "item") String item,
                                                      @PathVariable(name = "locale") String locale) {
        Optional<CmsItem> maybeCmsItem = cmsRegistry.findItem(item, locale);
        if (maybeCmsItem.isPresent()) {
            CmsItem cmsItem = maybeCmsItem.get();
            String template = cmsItem.getContentTemplate();
            return ResponseEntity.ok(template);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
