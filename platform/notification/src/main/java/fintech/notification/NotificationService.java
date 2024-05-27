package fintech.notification;

import java.util.List;

public interface NotificationService {
    Long send(Notification notification);

    List<Notification> find(NotificationQuery query);
}
