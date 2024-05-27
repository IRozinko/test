package fintech.spain;

import fintech.crm.security.InvalidEmailException;
import fintech.crm.security.InvalidTokenException;
import fintech.marketing.MarketingService;
import fintech.spain.web.common.ValidationExceptions;
import fintech.web.api.models.OkResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

@RequiredArgsConstructor
@RestController
public class MarketingApi {

    private static final byte[] onePxGifImg = parseBase64Binary("R0lGODlhAQABAIAAAP///wAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==");

    private final MarketingService marketingService;
    private final ValidationExceptions validationExceptions;

    @PostMapping("/api/public/unsubscribe")
    public OkResponse unsubscribe(@Valid @RequestBody UnsubscribeRequest unsubscribeRequest) {
        try {
            marketingService.unsubscribe(unsubscribeRequest.token, unsubscribeRequest.email);
            return OkResponse.OK;
        } catch (InvalidTokenException ite) {
            throw validationExceptions.invalidValue("token");
        } catch (InvalidEmailException iee) {
            throw validationExceptions.invalidValue("email");
        }
    }

    @GetMapping("/api/mailing/pixel")
    public void trackView(@RequestParam("hid") String uuid, HttpServletResponse response) throws IOException {
        marketingService.trackViews(uuid);
        try (InputStream in = new ByteArrayInputStream(onePxGifImg)) {
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
            IOUtils.copy(in, response.getOutputStream());
        }
    }

    @PostMapping("/api/mailing/track")
    public void trackClick(@RequestParam("hid") String uuid) {
        marketingService.trackClicks(uuid);
    }

    @Data
    public static class UnsubscribeRequest {
        @NotEmpty
        private String email;
        @NotEmpty
        private String token;
    }
}
