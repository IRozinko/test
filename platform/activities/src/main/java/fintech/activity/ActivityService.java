package fintech.activity;

import fintech.activity.commands.AddActivityCommand;
import fintech.activity.model.Activity;
import fintech.activity.model.FindActivitiesQuery;

import java.util.List;

public interface ActivityService {

    Long addActivity(AddActivityCommand command);

    List<Activity> findActivities(FindActivitiesQuery query);
}

