package community.mingle.app.src.member;

import community.mingle.app.src.domain.Total.TotalNotification;
import community.mingle.app.src.domain.Univ.UnivNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnivNotificationRepository extends JpaRepository<UnivNotification, Long> {

    List<UnivNotification> findFirst20ByMemberIdOrderByCreatedAtDesc(Long memberId);
}
