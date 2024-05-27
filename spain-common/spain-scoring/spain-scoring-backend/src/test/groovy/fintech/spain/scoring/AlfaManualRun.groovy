package fintech.spain.scoring

import fintech.JsonUtils
import fintech.spain.scoring.impl.SpainScoringProviderBean
import fintech.spain.scoring.model.ScoringModelType
import org.springframework.core.io.ClassPathResource

class AlfaManualRun {

    static void main(String[] args) {
        dedicatedScoring()
    }

    private static void dedicatedScoring() {
        SpainScoringProviderBean provider = new SpainScoringProviderBean("model_user", "B6JxBFu9dXoORx6u", "", "https://model.cubiform.net/Dedicated_model_v2", "")
        def json = new ClassPathResource("alfa-dedicated-scoring-request2.json").inputStream.text
        def map = JsonUtils.readValue(json, Map.class)
        def result = provider.request(ScoringModelType.DEDICATED_MODEL, map)
        println result
    }
}
