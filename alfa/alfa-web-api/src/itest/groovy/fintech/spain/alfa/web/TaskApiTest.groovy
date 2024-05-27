package fintech.spain.alfa.web


import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows
import fintech.task.model.TaskStatus
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class TaskApiTest extends AbstractAlfaApiTest {

    def "TaskApiTest: CREATE -> CHECK -> CANCEL"() {
        given:
        def client = TestFactory.newClient()
        client.signUp()
            .toLoanWorkflow()
            .runBeforeActivity(UnderwritingWorkflows.Activities.DOCUMENT_FORM)
        def token = apiHelper.login(client)

        when:
        def response = restTemplate.exchange("/api/web/task", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.controllers.web.TaskApi.TaskResponse.class)

        then:
        assert response.hasBody()

        def tasks = response.getBody().getTasks()
        tasks.size() == 0

        when:
        def createTaskRequest = new fintech.spain.alfa.web.controllers.web.TaskApi.CreateTaskRequest()
            .setType(UnderwritingTasks.InstantorHelpCall.TYPE)
            .setActivity(UnderwritingWorkflows.Activities.DOCUMENT_FORM)
        response = restTemplate.exchange("/api/web/task", HttpMethod.POST, ApiHelper.authorized(token, createTaskRequest), Object.class)

        then:
        assert response.getStatusCode() == HttpStatus.OK

        when:
        response = restTemplate.exchange("/api/web/task", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.controllers.web.TaskApi.TaskResponse.class)

        then:
        assert response.hasBody()
        assert response.getBody().getTasks().size() == 1
        def openTask = response.getBody().getTasks().get(0)
        assert openTask.getType() == UnderwritingTasks.InstantorHelpCall.TYPE
        assert openTask.getStatus() == TaskStatus.OPEN

        when:
        def deleteTaskRequest = new fintech.spain.alfa.web.controllers.web.TaskApi.DeleteTaskRequest().setType(UnderwritingTasks.InstantorHelpCall.TYPE)
        response = restTemplate.exchange("/api/web/cancel-task", HttpMethod.POST, ApiHelper.authorized(token, deleteTaskRequest), Object.class)

        then:
        response.getStatusCode() == HttpStatus.OK

        when:
        response = restTemplate.exchange("/api/web/task", HttpMethod.GET, ApiHelper.authorized(token, ""), fintech.spain.alfa.web.controllers.web.TaskApi.TaskResponse.class)

        then:
        assert response.hasBody()
        assert response.getBody().getTasks().size() == 1
        def cancelledTask = response.getBody().getTasks().get(0)
        assert cancelledTask.getType() == UnderwritingTasks.InstantorHelpCall.TYPE
        assert cancelledTask.getStatus() == TaskStatus.CANCELLED
    }
}
