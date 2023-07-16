package community.mingle.app.src.member;

import community.mingle.app.src.domain.ItemNotification;
import community.mingle.app.src.domain.ItemStatus;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.item.ItemRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemNotificationRepository extends JpaRepository<ItemNotification, Long> {

    @Query("select tn from ItemNotification tn where tn.member.id = :memberId and tn.item.status <> :inactiveStatus and tn.item.status <> :reportedStatus order by tn.createdAt desc")
    List<ItemNotification> findFirst20ByMemberIdOrderByCreatedAtDesc(Long memberId, ItemStatus inactiveStatus, ItemStatus reportedStatus, Pageable pageable);
}
