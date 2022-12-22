package community.mingle.app.src.member;

import community.mingle.app.src.member.model.NotificationDTO;

import java.util.Comparator;

public class NotificationDTOComparator implements Comparator<NotificationDTO> {

    @Override
    public int compare(NotificationDTO notificationDTO1, NotificationDTO notificationDTO2) {
        return (notificationDTO1.getCreatedAt())
                .compareTo(notificationDTO2.getCreatedAt());
    }


}