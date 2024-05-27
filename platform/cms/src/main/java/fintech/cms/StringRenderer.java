package fintech.cms;

import java.util.Map;

public interface StringRenderer {
    String render(String template, Map<String, Object> context);
}
