package fintech.spain.scoring

import fintech.JsonUtils
import fintech.spain.scoring.impl.SpainScoringProviderBean
import fintech.spain.scoring.model.ScoringModelType
import org.springframework.core.io.ClassPathResource

class PrestoManualRun {

    static void main(String[] args) {
        SpainScoringProviderBean provider = new SpainScoringProviderBean("model_user", "B6JxBFu9dXoORx6u", "https://model.cubiform.net/LR_model_v2", "https://model.cubiform.net/Dedicated_model", "https://model.cubiform.net/Limit_call_model")
        def json = new ClassPathResource("request-example.json").inputStream.text
        def map = JsonUtils.readValue(json, Map.class)
        def result = provider.request(ScoringModelType.DEDICATED_MODEL, map)
        println result
        result = provider.request(ScoringModelType.LINEAR_REGRESSION_MODEL, map)
        println result
        result = provider.request(ScoringModelType.CREDIT_LIMIT_MODEL, map)
        println result
    }
}
