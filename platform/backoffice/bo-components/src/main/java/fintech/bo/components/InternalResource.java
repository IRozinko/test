package fintech.bo.components;

import com.vaadin.server.ExternalResource;

public class InternalResource extends ExternalResource {

    private static final String URL_PREFIX = "#!";

    public InternalResource(String sourceURL) {
        super(URL_PREFIX + sourceURL);
    }
}
