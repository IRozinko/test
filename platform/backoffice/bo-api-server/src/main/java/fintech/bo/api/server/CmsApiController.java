package fintech.bo.api.server;


import fintech.Validate;
import fintech.bo.api.model.StringRequest;
import fintech.bo.api.model.cms.*;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.server.services.CmsApiService;
import fintech.cms.CmsContextBuilder;
import fintech.cms.CmsNotification;
import fintech.cms.NotificationRenderer;
import fintech.cms.Pdf;
import fintech.cms.PdfRenderer;
import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsRegistry;
import fintech.crm.CrmConstants;
import fintech.crm.client.ClientService;
import fintech.dc.DcService;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

@RestController
public class CmsApiController {

    @Autowired
    private NotificationRenderer notificationRenderer;

    @Autowired
    private PdfRenderer pdfRenderer;

    @Autowired
    private CmsApiService cmsApiService;

    @Autowired
    private CmsRegistry cmsRegistry;

    @Autowired
    private CmsContextBuilder contextBuilder;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DcService dcService;

    @PostMapping("/api/bo/cms/preview-notification")
    public RenderNotificationResponse previewNotification(@RequestBody CmsItem request) {
        RenderNotificationResponse response = new RenderNotificationResponse();
        Map<String, Object> context = cmsRegistry.getTestingContext();
        notificationRenderer.render(request, context, request.getLocale()).ifPresent(notification -> {
            if (notification.getEmail().isPresent()) {
                response.setEmailBody(notification.getEmail().get().getBody());
                response.setEmailSubject(notification.getEmail().get().getSubject());
            }
            if (notification.getSms().isPresent()) {
                response.setSmsText(notification.getSms().get().getText());
            }
        });
        return response;
    }

    @PostMapping("/api/bo/cms/render-notification")
    public RenderNotificationResponse renderNotification(@RequestBody StringRequest request) {
        RenderNotificationResponse response = new RenderNotificationResponse();
        Map<String, Object> context = cmsRegistry.getTestingContext();
        notificationRenderer.render(request.getString(), context, CrmConstants.DEFAULT_LOCALE)
            .ifPresent(notification -> {
                if (notification.getEmail().isPresent()) {
                    response.setEmailBody(notification.getEmail().get().getBody());
                    response.setEmailSubject(notification.getEmail().get().getSubject());
                }
                if (notification.getSms().isPresent()) {
                    response.setSmsText(notification.getSms().get().getText());
                }
            });
        return response;
    }

    @PostMapping("/api/bo/cms/get-notification")
    public GetNotificationResponse getNotification(@RequestBody GetNotificationRequest request) {
        Optional<CmsNotification> maybe;
        String locale = resolveLocale(request.getClientId(), request.getDebtId());
        if (request.isRender()) {
            Map<String, Object> context = contextBuilder.basicContext(request.getClientId(), request.getDebtId());
            maybe = notificationRenderer.render(request.getKey(), context, locale);
        } else {
            maybe = notificationRenderer.buildTemplate(request.getKey(), locale);
        }

        Validate.isTrue(maybe.isPresent(), "Notification template not found: [%s]", request.getKey());
        CmsNotification notification = maybe.get();
        GetNotificationResponse response = new GetNotificationResponse();

        notification.getEmail().ifPresent(email -> {
            response.setEmailBody(email.getBody());
            response.setEmailSubject(email.getSubject());
        });
        notification.getSms().ifPresent(sms -> response.setSmsText(sms.getText()));

        return response;
    }

    @SneakyThrows
    @PostMapping(path = "/api/bo/cms/render-pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void renderPdf(@RequestBody CmsItem request, HttpServletResponse response) {
        Optional<Pdf> pdfMaybe = pdfRenderer.render(request, cmsRegistry.getTestingContext());
        Validate.isTrue(pdfMaybe.isPresent(), "PDF not generated");
        Pdf pdf = pdfMaybe.get();
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "inline; filename=\"" + pdf.getName() + "\"");
        response.setContentLength(pdf.getContent().length);
        try (ServletOutputStream os = response.getOutputStream()) {
            IOUtils.write(pdf.getContent(), os);
            os.flush();
        }
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CMS_EDIT})
    @PostMapping("/api/bo/cms/save-item")
    public void saveItem(@RequestBody UpdateCmsItemRequest request) {
        cmsApiService.updateCmsItems(request);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CMS_EDIT})
    @PostMapping("/api/bo/cms/add-item")
    public void addItem(@RequestBody AddCmsItemRequest request) {
        cmsApiService.addItem(request);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CMS_EDIT})
    @PostMapping("/api/bo/cms/delete-item")
    public void deleteItem(@RequestBody DeleteCmsItemRequest request) {
        cmsApiService.deleteItem(request);
    }

    @GetMapping("/api/bo/cms/documentation")
    CmsDocumentationResponse getDocumentation() {
        CmsDocumentationResponse response = new CmsDocumentationResponse();
        response.setTestingContextDocumentation(cmsRegistry.getTestingContextDocumentation());
        return response;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CMS_EDIT})
    @PostMapping("/api/bo/cms/add-locale")
    public void addLocale(@RequestBody AddNewLocaleRequest request) {
        cmsRegistry.addLocale(request.getLocale());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CMS_EDIT})
    @PostMapping("/api/bo/cms/delete-locale")
    public void deleteLocale(@RequestBody DeleteLocaleRequest request) {
        cmsRegistry.deleteLocale(request.getLocale());
    }

    private String resolveLocale(Long clientId, Long debtId) {
        String locale = null;
        if (clientId != null) {
            locale = clientService.get(clientId).getLocale();
        } else if (debtId != null) {
            Long debtor = dcService.get(debtId).getClientId();
            locale = clientService.get(debtor).getLocale();
        }
        return locale != null ? locale : CrmConstants.DEFAULT_LOCALE;
    }

}
