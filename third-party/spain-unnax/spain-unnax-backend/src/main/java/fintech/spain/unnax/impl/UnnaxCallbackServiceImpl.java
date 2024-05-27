package fintech.spain.unnax.impl;

import com.google.common.hash.Hashing;
import fintech.spain.unnax.UnnaxCallbackService;
import fintech.spain.unnax.callback.model.CallbackRequest;
import fintech.spain.unnax.db.CallbackEntity;
import fintech.spain.unnax.db.UnnaxCallbackRepository;
import fintech.spain.unnax.event.CallbackEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.Objects;

@Service
@Transactional
public class UnnaxCallbackServiceImpl implements UnnaxCallbackService {

    private final UnnaxCallbackRepository unnaxCallbackRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final String apiId;
    private final String apiCode;

    public UnnaxCallbackServiceImpl(UnnaxCallbackRepository unnaxCallbackRepository,
                                    ApplicationEventPublisher eventPublisher,
                                    @Value("${unnax.apiId:id}") String apiId,
                                    @Value("${unnax.apiCode:code}") String apiCode) {
        this.unnaxCallbackRepository = unnaxCallbackRepository;
        this.eventPublisher = eventPublisher;
        this.apiId = apiId;
        this.apiCode = apiCode;
    }

    @Override
    public CallbackEntity save(CallbackRequest request) {
        return unnaxCallbackRepository.save(new CallbackEntity(request));
    }

    @Async
    @Override
    public void publishEvent(CallbackEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public boolean isSignatureValid(CallbackRequest request) {
        String responseId = request.getResponseId();
        String signature = apiId + responseId + apiCode;
        String hashedSignature = Hashing.sha1()
            .hashString(signature, Charset.defaultCharset())
            .toString();

        return Objects.equals(hashedSignature, request.getSignature());
    }
}
