package fintech.bo.components.utils;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.Link;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.stream.Collectors;

public class UrlUtils {

    public static final String ID = "id";

    public static final String TAB = "tab";

    public static final String NAVIGATE_TO = "navigate_to";

    public static final String TEL = "tel";

    public static String appendToFragment(String fragment, String key, String value) {
        Map<String, String> params = toParams(fragment);
        params.put(key, toParamValue(value));

        return String.format("%s?%s", params.remove(ID), params.entrySet().stream().map(e -> String.format("%s=%s", e.getKey(), e.getValue())).collect(Collectors.joining("&")));
    }

    public static String toParamValue(String name) {
        return StringUtils.lowerCase(StringUtils.replace(name, StringUtils.SPACE, "_"));
    }

    public static String getParam(String fragment, String name) {
        return StringUtils.trimToNull(toParams(fragment).get(name));
    }

    private static Map<String, String> toParams(String url) {
        Map<String, String> parameters = UriComponentsBuilder.fromUriString(url).build().getQueryParams().toSingleValueMap();
        parameters.put(ID, StringUtils.substringBefore(url, "?"));
        return parameters;
    }

    public static Link generateViewLink(FileApiClient fileApiClient, CloudFile cloudFile) {
        Link link = new Link();
        link.setCaption(cloudFile.getName());
        CloudFileResource resource = new CloudFileResource(cloudFile, fileApiClient, f -> {
        });
        BrowserWindowOpener opener = new BrowserWindowOpener(resource);
        opener.extend(link);
        return link;
    }
}
