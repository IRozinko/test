package fintech.cms

import fintech.cms.impl.pebble.PebbleTemplateEngine
import org.springframework.beans.factory.annotation.Autowired

class PebbleTemplateEngineTest extends BaseSpecification {

    @Autowired
    PebbleTemplateEngine pebbleTemplateEngine

    def "Render"() {
        expect:
        pebbleTemplateEngine.render("Hello {{name}}!", [name: "world"], "en") == "Hello world!"
    }

    def "Render with parent template"() {
        expect:
        pebbleTemplateEngine.render("{% extends \"base.html\" %}{% block content %}Hello {{name}}!{% endblock %}", [name: "world"], "en") == "Hello world!"
    }
}
