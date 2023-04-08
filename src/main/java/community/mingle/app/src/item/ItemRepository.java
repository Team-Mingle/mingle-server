package community.mingle.app.src.item;

import community.mingle.app.src.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

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

    public List<ItemLike> findItemLike(Long itemId, Long memberIdByJwt) {
        return em.createQuery("select i from ItemLike i where i.id = :itemId and i.member.id = :memberIdByJwt", ItemLike.class)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("itemId", itemId)
                .getResultList();
    }

    public Long save(Item item) {
        em.persist(item);
        return item.getId();
    }

    public Long save(ItemLike itemLike) {
        em.persist(itemLike);
        return itemLike.getId();
    }

    public void saveImg(ItemImg itemImg) {
        em.persist(itemImg);
    }

    public Item findItemByIdAndMemberId(Long itemId, Long memberIdByJwt) {
        return em.createQuery("select i from Item i join fetch i.member as m where i.status <> :status1 and i.status <> :status2 and i.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) and i.id = :itemId", Item.class)
                .setParameter("status1", ItemStatus.INACTIVE)
                .setParameter("status2",ItemStatus.REPORTED)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("itemId", itemId)
                .getSingleResult();
    }

    public void deleteItemLike(Long itemId, Long memberIdByJwt) {
        ItemLike itemLike = em.createQuery("select i from ItemLike i where i.id = :itemId and i.member.id = :memberIdByJwt", ItemLike.class)
                .setParameter("itemId", itemId)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .getSingleResult();
        em.remove(itemLike);
    }

    public Item findItemById(Long itemId) {
        return em.createQuery("select i from Item i where i.id = :itemId", Item.class)
                .setParameter("itemId", itemId)
                .getSingleResult();
    }

    public Long findItemAnonymousId(Item item, Long memberIdByJwt) {
        Long newAnonymousId;


        /**
         * case 1: 해당 게시글 커멘츠가 멤버가 있는지 없는지 확인하고 있으면 그 전 id 부여
         */
        List<ItemComment> itemCommentsByMember = em.createQuery("select ic from ItemComment ic join fetch ic.item as it join fetch ic.member as m where it.id = :itemId and m.id = :memberId and ic.isAnonymous = true", ItemComment.class)
                .setParameter("itemId", item.getId())
                .setParameter("memberId", memberIdByJwt)
                .getResultList();
        if (itemCommentsByMember.size() != 0) { //있으면 list 첫번째 element 반환. 중복은 없을테니
            return itemCommentsByMember.get(0).getAnonymousId();
        }

        /**
         * case 2: 댓글 단 이력이 없고 익명 댓글을 달고싶을때: anonymousId 부여받음
         */
        List<ItemComment> itemComments = item.getItemCommentList();
        ItemComment itemCommentWithMaxAnonymousId;

        try {  //게시물에서 제일 큰 id를 찾은 후 +1 한 id 를 내 댓글에 새로운 anonymousId 로 부여
            // 닉네임으로 단 사람만 있을 경우에도 그냥 0+1 을 해줘서 1을 부여해줌
            itemCommentWithMaxAnonymousId = itemComments.stream()
                    .max(Comparator.comparingLong(ItemComment::getAnonymousId))//nullPointerException
                    .get();
            newAnonymousId = itemCommentWithMaxAnonymousId.getAnonymousId() + 1;
            return newAnonymousId;

        } catch (NoSuchElementException e) {  //게시물에 기존 익명 id 가 아예 없을때: id 로 1 부여 --> 댓글이 아예 없을때
            newAnonymousId = Long.valueOf(1);
        }
        return newAnonymousId;
    }

    public void saveItemComment(ItemComment comment) {
        em.persist(comment);
    }

    public void saveItemNotification(ItemNotification itemNotification) {
        em.persist(itemNotification);
    }

    public void deleteItemNotification(Long itemNotificationId) {
        ItemNotification itemNotification = em.find(ItemNotification.class, itemNotificationId);
        em.remove(itemNotification);
    }

    public ItemComment findItemCommentById(Long id) {
        return em.find(ItemComment.class, id);
    }

    public List<ItemComment> findItemComments(Long itemId, Long memberIdByJwt) {
        List<ItemComment> itemCommentList = em.createQuery("select ic from ItemComment ic join ic.item as i" +
                        " where i.id = :itemId and ic.parentCommentId is null and ic.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt)" +
                        " order by ic.createdAt asc ", ItemComment.class)
                .setParameter("itemId", itemId)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .getResultList();
        return itemCommentList;
    }

    public List<ItemComment> findItemCoComments(Long itemId, Long memberIdByJwt) {
        List<ItemComment> univCoCommentList = em.createQuery("select ic from ItemComment ic join ic.item as i " +
                        " where i.id = :itemId and ic.parentCommentId is not null and ic.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) " +
                        " order by ic.createdAt asc", ItemComment.class)
                .setParameter("itemId", itemId)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .getResultList();
        return univCoCommentList;
    }




}
