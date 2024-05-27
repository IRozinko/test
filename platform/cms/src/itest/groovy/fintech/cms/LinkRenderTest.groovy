package fintech.cms

import org.springframework.beans.factory.annotation.Autowired

class LinkRenderTest extends BaseSpecification {

    @Autowired
    StringRenderer linkRenderer

    def "Render link with parameters"() {
        given:
        def context = [application: [number: "123456"]]

        when:
        def link = linkRenderer.render("http://www.google.com?id={{application.number}}", context)

        then:
        link == "http://www.google.com?id=123456"
    }

    def "Ignore unknown parameter"() {
        given:
        def context = [application: [number: "123456"]]

        when:
        def link = linkRenderer.render("http://www.google.com?id={{loan.number}}", context)

        then:
        link == "http://www.google.com?id="
    }

}
