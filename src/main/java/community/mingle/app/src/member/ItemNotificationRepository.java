package community.mingle.app.src.member;

import community.mingle.app.src.domain.ItemNotification;
import community.mingle.app.src.item.ItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemNotificationRepository extends JpaRepository<ItemNotification, Long> {

    List<ItemNotification> findFirst20ByMemberIdOrderByCreatedAtDesc(Long memberId);
}
