package fintech.cms.generator;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TestModel {

    private String title;
    private List<TestModelChild> children;
    private Map<String, TestModelChild> map;
    private TestModelChild child;

    @Data
    static class TestModelChild {
        private String field;
    }
}
