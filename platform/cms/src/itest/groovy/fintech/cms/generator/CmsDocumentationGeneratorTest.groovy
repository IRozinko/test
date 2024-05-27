package fintech.cms.generator

import fintech.cms.CmsDocumentationGenerator
import spock.lang.Specification

class CmsDocumentationGeneratorTest extends Specification {

    def "GenerateContextDocumentation"() {
        given:
        CmsDocumentationGenerator generator = new CmsDocumentationGenerator()

        when:
        def doc = generator.generateContextDocumentation(['test': new TestModel(title: 'Title',
            children: [new TestModel.TestModelChild(field: 'value')],
            map: ['child1Day' : new TestModel.TestModelChild(field: '1'),
                  'child2Days': new TestModel.TestModelChild(field: '2')],
            child : new TestModel.TestModelChild(field: 'child')),
        ])

        then:
        doc == new File(getClass().getResource('/generator/generator-output.txt').toURI()).text

    }

}
