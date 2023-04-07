package community.mingle.app.src.item;

import community.mingle.app.src.domain.*;
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

    public Long save(Item item) {
        em.persist(item);
        return item.getId();
    }

    public void saveImg(ItemImg itemImg) {
        em.persist(itemImg);
    }

    public Item findItemById(Long itemId) {
        return em.find(Item.class, itemId);
    }

    public boolean checkItemIsBlinded(Long itemId, Long memberIdByJwt) {
        List<ItemBlind> resultList = em.createQuery("select b from ItemBlind b where b.item.id =:itemId and b.member.id =:memberId", ItemBlind.class)
                .setParameter("itemId", itemId)
                .setParameter("memberId", memberIdByJwt)
                .getResultList();
        return resultList.size() != 0;
    }

    public boolean checkItemIsLiked(Long itemId, Long memberIdByJwt) {
        List<ItemLike> itemLikeList = em.createQuery("select l from ItemLike l join l.item item join l.member m where item.id = :itemId and m.id = :memberId", ItemLike.class)
                .setParameter("itemId", itemId)
                .setParameter("memberId", memberIdByJwt)
                .getResultList();
        return itemLikeList.size() != 0;
    }
}
