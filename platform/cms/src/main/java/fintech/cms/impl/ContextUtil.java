package fintech.cms.impl;

import fintech.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ContextUtil {

    static void validateContext(String templateKey, String scopes, Map<String, Object> context) {
        for (String scope : StringUtils.split(scopes, ",")) {
            scope = StringUtils.trim(scope);
            Validate.isTrue(context.containsKey(scope), "Missing required context value [%s], template [%s]", scope, templateKey);
        }
    }
}
