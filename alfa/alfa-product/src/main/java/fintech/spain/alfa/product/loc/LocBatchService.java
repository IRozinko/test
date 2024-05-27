//package fintech.spain.alfa.product.loc;
//
//import fintech.spain.alfa.product.loc.db.LocBatchEntity;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//
//public interface LocBatchService {
//
//    Long uploadClients(Collection<Long> clientIds);
//
//    void uploadClients(String input);
//
//    void trigger(Long batchNumber);
//
//    Optional<LocBatchEntity> findOptional(LocBatchQuery query);
//
//    List<LocBatchEntity> find(LocBatchQuery query);
//
//    void markAsCompleted(Long clientId);
//
//    void startWorkflows();
//
//}
