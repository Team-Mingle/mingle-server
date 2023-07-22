package community.mingle.app.src.member;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalNotification;
import community.mingle.app.src.domain.Univ.UnivNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnivNotificationRepository extends JpaRepository<UnivNotification, Long> {

    @Query("select un from UnivNotification un where un.member.id = :memberId and un.univPost.status <> :inactiveStatus and un.univPost.status <> :reportedStatus order by un.createdAt desc")
    List<UnivNotification> findFirst20ByMemberIdOrderByCreatedAtDesc(Long memberId, PostStatus inactiveStatus, PostStatus reportedStatus, Pageable pageable);

}
