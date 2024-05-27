package fintech.workflow.db;

import fintech.db.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityListenerRepository extends BaseRepository<ActivityListenerEntity, Long> {

    @Query("from ActivityListenerEntity e where e.workflowName = ?1 and e.workflowVersion = ?2 and e.activityName = ?3 and e.activityStatus = 'STARTED'")
    List<ActivityListenerEntity> findExistedOnActivityStarted(String wfName, int version, String activity);

    @Query("from ActivityListenerEntity e where e.workflowName = ?1 and e.workflowVersion = ?2 and e.activityName = ?3 and e.activityStatus = 'COMPLETED' and e.resolution = ?4")
    List<ActivityListenerEntity> findExistedOnActivityCompleted(String wfName, int version, String activity, String resolution);
}
