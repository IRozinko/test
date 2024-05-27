package fintech.task.db;


import fintech.db.BaseRepository;

import java.util.List;

public interface TaskRepository extends BaseRepository<TaskEntity, Long> {

    List<TaskEntity> findByParentTaskId(Long parentTaskId);

}
