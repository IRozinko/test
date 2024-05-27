package fintech.ekomi.impl

import fintech.ekomi.api.json.Snapshot
import fintech.ekomi.api.json.SnapshotInfo
import fintech.ekomi.exception.EKomiException
import okhttp3.Response

class EKomiServiceTest extends BaseApiSetupTest {

    def "Test snapshot request send with expected parameters"() {
        given:
        SnapshotInfo snapshotInfo = new SnapshotInfo(count: 111, average: 4.4)
        Response response = response(new Snapshot(info: snapshotInfo), ekomiApi.url + "/getSnapshot")
        testInterceptor.withExpectedResponse(response)

        eKomiService.getCompanyRating()

        expect:
        testInterceptor.getChain().request().url().queryParameter("type") == ekomiApi.type
        testInterceptor.getChain().request().url().queryParameter("version") == ekomiApi.version
        testInterceptor.getChain().request().url().queryParameter("auth") == ekomiApi.id + "|" + ekomiApi.key
    }

    def "Test getting expected rating result"() {
        given:
        SnapshotInfo snapshotInfo = new SnapshotInfo(count: 111, average: 4.4)
        Response response = response(new Snapshot(info: snapshotInfo), "http://ekomiApi.ekomi.de/v3/getSnapshot")
        testInterceptor.withExpectedResponse(response)

        clearCache()
        def rating = eKomiService.getCompanyRating()

        expect:
        assert rating.isPresent()
        rating.get().count == snapshotInfo.count
        rating.get().average == snapshotInfo.average * 2
    }

    def "Test executing second time result is cached"() {
        given:
        SnapshotInfo snapshotInfo = new SnapshotInfo(count: 111, average: 4.4)
        Response response = response(new Snapshot(info: snapshotInfo), "http://ekomiApi.ekomi.de/v3/getSnapshot")

        testInterceptor.withExpectedResponse(response)
        testInterceptor.resetInterceptedCount()

        clearCache()

        eKomiService.getCompanyRating()
        eKomiService.getCompanyRating()
        def rating = eKomiService.getCompanyRating()

        expect:
        assert rating.isPresent()
        testInterceptor.getInterceptedCount() == 1
    }

    def "Test returning exception in case of eKomi communication exception"() {
        given:
        Response response = response("{\"badjson\":\"\"}", "http://ekomiApi.ekomi.de/v3/getSnapshot")
        testInterceptor.withExpectedResponse(response)

        clearCache()
        when:
        eKomiService.getCompanyRating()

        then:
        thrown(EKomiException)
    }
}
