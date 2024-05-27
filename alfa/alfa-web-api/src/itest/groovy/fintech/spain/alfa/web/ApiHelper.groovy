package fintech.spain.alfa.web


import fintech.spain.alfa.product.testing.TestClient
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows
import fintech.workflow.Workflow
import fintech.workflow.WorkflowQuery
import fintech.workflow.WorkflowStatus
import fintech.workflow.impl.WorkflowServiceBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@Component
class ApiHelper {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    WorkflowServiceBean workflowServiceBean

    static HttpEntity authorized(String token) {
        return authorized(token, null)
    }

    static HttpEntity authorized(String token, Object body) {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        headers.setAccept([MediaType.APPLICATION_JSON])
        headers.set("Authorization", token)
        return new HttpEntity(body, headers)
    }

    static HttpEntity multipart(String token, Map<String, Object> map) {
        HttpHeaders headers = new HttpHeaders()
        headers.set("Authorization", token)
        headers.setContentType(MediaType.MULTIPART_FORM_DATA)
        MultiValueMap<String, Object> multiMap = new LinkedMultiValueMap<String, Object>()
        map.forEach({ key, value -> multiMap.add(key, value) })
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(multiMap, headers)
        return request
    }

    static HttpEntity formPost(Map<String, String> map) {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
        MultiValueMap<String, String> multiMap = new LinkedMultiValueMap<String, String>()
        map.forEach({ key, value -> multiMap.add(key, value) })
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(multiMap, headers)
        return request
    }

    String login(TestClient client, int expectedStatus) {
        def result = restTemplate.postForEntity("/api/public/web/login", new fintech.spain.alfa.web.models.LoginRequest(email: client.signUpForm.email, password: client.signUpForm.password), fintech.spain.alfa.web.models.LoginResponse.class)
        assert result.statusCodeValue == expectedStatus
        return result.body.token
    }

    String login(TestClient client) {
        return login(client, 200)
    }

    Optional<Workflow> findWorkflow(TestClient client) {
        return workflowServiceBean.findWorkflows(WorkflowQuery.byClientId(client.clientId, UnderwritingWorkflows.FIRST_LOAN, WorkflowStatus.ACTIVE)).stream().findFirst()

    }
}
