//package fintech.spain.alfa.bo.api;
//
//import fintech.spain.alfa.bo.model.UploadLocClientsRequest;
//import fintech.spain.alfa.product.loc.LocBatchService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//public class LocBatchApi {
//
//    @Autowired
//    private LocBatchService locBatchService;
//
//    @PostMapping("/api/bo/loc-batch/{batchNumber}/trigger")
//    public void trigger(@PathVariable long batchNumber) {
//        locBatchService.trigger(batchNumber);
//    }
//
//    @PostMapping("/api/bo/loc-batch/upload")
//    public void upload(@RequestBody UploadLocClientsRequest request) {
//        locBatchService.uploadClients(request.getClients());
//    }
//
//    @PostMapping("/api/bo/loc-batch/trigger-start-workflows")
//    public void triggerStartWorkflows() {
//        locBatchService.startWorkflows();
//    }
//
//}
