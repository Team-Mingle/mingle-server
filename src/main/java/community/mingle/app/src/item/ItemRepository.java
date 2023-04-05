package community.mingle.app.src.item;

import community.mingle.app.src.domain.Item;
import community.mingle.app.src.domain.ItemStatus;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public List<Item> findItems( Long itemId, Long memberIdByJwt) {
        return em.createQuery("select i from Item i join fetch i.member as m where i.status <> :status1 and i.status <> :status2 and i.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) and i.id < :itemId order by i.createdAt desc", Item.class)
                .setParameter("status1", ItemStatus.INACTIVE)
                .setParameter("status2",ItemStatus.REPORTED)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("itemId", itemId)
                .setMaxResults(50)
                .getResultList();
    }
}
