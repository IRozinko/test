package fintech.activity

import fintech.activity.spi.BulkActionContext
import fintech.activity.spi.BulkActionHandler
import org.apache.commons.lang3.Validate
import org.springframework.stereotype.Component;

@Component
class NoopActionHandler implements BulkActionHandler {

    static int executed = 0

    @Override
    void handle(BulkActionContext context) {
        Validate.notNull(context.getRequiredParam("test", String.class))
        Validate.notNull(context.getParam("test", String.class).get())
        Validate.isTrue(!context.getParam("unknown", String.class).isPresent())
        Validate.notNull(context.getActivity())
        executed++
    }
}
