package fintech.nordigen

import fintech.ClasspathUtils
import fintech.nordigen.impl.NordigenProviderBean

class ManualRun {

    static void main(String[] args) {
        def bean = new NordigenProviderBean("https://demo.nordigen.com/api/process/factors/flags", "1337000138", "6f430c085ddc9d7c5945adac80a02ce1")
        def result = bean.request(ClasspathUtils.resourceToString("nordigen-test-statement.json"))
        println result
        result = bean.request(ClasspathUtils.resourceToString("nordigen-test-statement.json"))
        println result
    }
}
