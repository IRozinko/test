package fintech.ga.impl;

import fintech.ga.GADataSender;
import fintech.ga.GAService;
import fintech.ga.db.GAClientDataEntity;
import fintech.ga.db.GAClientDataRepository;
import fintech.ga.db.GARequestLogEntity;
import fintech.ga.db.GARequestLogRepository;
import fintech.ga.events.GAEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class GAServiceImpl implements GAService {

    private final GAClientDataRepository repository;
    private final GARequestLogRepository requestLogRepository;

    @Resource(name = "${ga.data-sender:" + GAMockDataSender.NAME + "}")
    private GADataSender gaDataSender;

    @Autowired
    private GAProperties properties;

    private static final Pattern GA_COOKIE_PATTERN = Pattern.compile("^GA\\d+\\.\\d+\\.(.+)$");

    @Transactional
    @Override
    public void saveOrUpdateCookie(long clientId, String cookie, String userAgent) {
        GAClientDataEntity clientDataEntity = repository.findByClientId(clientId).orElse(new GAClientDataEntity());
        clientDataEntity.setClientId(clientId);
        clientDataEntity.setCookieUserId(parseClientId(cookie));
        clientDataEntity.setUserAgent(userAgent);
        repository.saveAndFlush(clientDataEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public <E extends GAEvent> void sendEvent(E event) {
        Optional<GAClientDataEntity> clientDataEntity = repository.findByClientId(event.getClientId());
        Map<String, String> p = event.getParams();
        String userAgent = null;
        p.put(properties.getTrackingIdParamName(), properties.getTrackingId());
        if (clientDataEntity.isPresent()) {
            userAgent = clientDataEntity.get().getUserAgent();
            if (clientDataEntity.get().getCookieUserId() != null) {
                p.put(properties.getClientIdParamName(), clientDataEntity.get().getCookieUserId());
            } else {
                p.putAll(event.getUnknownCidParams());
            }
        }

        try {
            GAResponse response = gaDataSender.sendData(new GARequest(properties.getServiceUrl(), userAgent, p));
            String request = properties.getServiceUrl() + "?" + p.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
            boolean isOkResponse = response.getResponseCode() / 100 == 2;
            if (!isOkResponse) {
                log.error("Bad GA response. Code: {}, Request: {}, Message: {}", response.getResponseCode(), request, response.getResponse());
            }
            GARequestLogEntity entity = new GARequestLogEntity();
            entity.setClientId(event.getClientId());

            entity.setRequest(request);
            entity.setResponse(isOkResponse ? "OK" : response.getResponse());
            entity.setResponseCode(response.getResponseCode());
            requestLogRepository.save(entity);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Nullable
    private static String parseClientId(String gaCookie) {
        if (gaCookie == null) {
            return null;
        }
        Matcher m = GA_COOKIE_PATTERN.matcher(gaCookie);
        if (m.matches()) {
            return m.group(1);
        }
        return null;
    }
}
