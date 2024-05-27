## Workflow module

Each Loan application request starts process called **Loan application workflow**. <br/>
Basically, workflow is set of activities (registration steps).<br/>
Each activity starts when set of configured conditions is met. As usual, activity X starts after activity Y completes.<br/>

Activity types:
* **System** As usual request to third-party service for client checking.
* **Client** Usually has UI state setup for frontend (like show some registration web form). Completes on submitting by client.
* **Agent** Agent action is required. As usual, agent task is started (for example, to check uploaded by client documents). Activity completes after task is resolved.



     
