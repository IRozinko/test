package fintech.bo.api.server.services;

import com.google.common.base.MoreObjects;
import fintech.Validate;
import fintech.bo.api.model.cms.AddCmsItemRequest;
import fintech.bo.api.model.cms.DeleteCmsItemRequest;
import fintech.bo.api.model.cms.UpdateCmsItemRequest;
import fintech.cms.NotificationRenderer;
import fintech.cms.PdfRenderer;
import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsItemType;
import fintech.cms.spi.CmsRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CmsApiService {

    @Autowired
    private NotificationRenderer notificationRenderer;

    @Autowired
    private PdfRenderer pdfRenderer;

    @Autowired
    private CmsRegistry registry;

    @Transactional
    public void updateCmsItems(UpdateCmsItemRequest request) {
        Validate.notEmpty(request.getItems(), "There is no CMS items to save");
        request.getItems().forEach((locale, item) -> {

            CmsItem cmsItem = registry.findItem(item.getKey(), item.getLocale())
                .orElseGet(() -> new CmsItem()
                    .setKey(item.getKey())
                    .setLocale(item.getLocale())
                    .setDescription(item.getDescription())
                    .setScope(item.getScope())
                    .setItemType(CmsItemType.valueOf(item.getItemType()))
                );
            cmsItem.setEmailSubjectTemplate(item.getEmailSubjectTemplate());
            cmsItem.setEmailBodyTemplate(item.getEmailBodyTemplate());
            cmsItem.setSmsTextTemplate(item.getSmsTextTemplate());
            cmsItem.setContentTemplate(item.getContentTemplate());
            cmsItem.setTitleTemplate(item.getTitleTemplate());
            cmsItem.setHeaderTemplate(item.getHeaderTemplate());
            cmsItem.setFooterTemplate(item.getFooterTemplate());
            registry.saveItem(cmsItem, true);

            // make sure that template are valid
            if (cmsItem.getItemType() == CmsItemType.NOTIFICATION) {
                notificationRenderer.render(item.getKey(), registry.getTestingContext(), cmsItem.getLocale());
            } else if (cmsItem.getItemType() == CmsItemType.PDF_HTML) {
                pdfRenderer.render(item.getKey(), registry.getTestingContext(), cmsItem.getLocale());
            }

        });
    }

    public void addItem(AddCmsItemRequest request) {
        Validate.isTrue(!registry.findItem(request.getKey(), request.getLocale()).isPresent(), "CMS item already exists by key and locale");
        CmsItem item = new CmsItem();
        item.setKey(request.getKey());
        item.setLocale(request.getLocale());
        item.setDescription(MoreObjects.firstNonNull(request.getDescription(), ""));
        item.setScope(MoreObjects.firstNonNull(request.getScope(), ""));
        item.setItemType(CmsItemType.valueOf(request.getType()));
        registry.saveItem(item, false);
    }

    public void deleteItem(DeleteCmsItemRequest request) {
        registry.deleteItem(request.getKey());
    }
}
