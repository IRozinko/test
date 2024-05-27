## Decision engine module

External service for evaluating decision regarding specified client.<br/> 
Decision is evaluated on passed in request key+value pair list (scoring values). <br/> 

Each scoring values list is associated to scenario, by which third-party service calculates result.<br/> 
Before each request service gets scoring values keys by specified scenario to avoid sending excess data.  

