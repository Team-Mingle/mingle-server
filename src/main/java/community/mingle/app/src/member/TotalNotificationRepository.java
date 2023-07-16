package community.mingle.app.src.member;

import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TotalNotificationRepository extends JpaRepository<TotalNotification,Long> {

    @Query("select tn from TotalNotification tn where tn.member.id = :memberId and tn.totalPost.status <> :inactiveStatus and tn.totalPost.status <> :reportedStatus order by tn.createdAt desc")
    List<TotalNotification> findFirst20ByMemberIdOrderByCreatedAtDesc(Long memberId, PostStatus inactiveStatus, PostStatus reportedStatus, Pageable pageable);
}
