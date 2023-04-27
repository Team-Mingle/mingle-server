package community.mingle.app.src.member;

import community.mingle.app.src.domain.Total.TotalNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TotalNotificationRepository extends JpaRepository<TotalNotification,Long> {

    List<TotalNotification> findFirst20ByMemberIdOrderByCreatedAtDesc(Long memberId);
}
