## Iovation module

Backend part of iovation integration.

Common process:
* Iovation frontend JS collects client's browser/system information into **blackbox** string
* Frontend sends blackbox to backend
* backend checks blackbox in iovation service in LoC workflow.
* If smth is wrong with blackbox LoC request rejects.    
