package community.mingle.app.src.member;

import community.mingle.app.src.domain.ReportNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportNotificationRepository extends JpaRepository<ReportNotification,Long> {

    public List<ReportNotification> findFirst20ByMemberIdOrderByCreatedAtDesc(Long memberId);
}
