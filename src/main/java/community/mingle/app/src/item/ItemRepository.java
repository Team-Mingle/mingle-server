package community.mingle.app.src.item;

import community.mingle.app.config.BaseException;
import community.mingle.app.src.domain.*;
import community.mingle.app.src.domain.Total.TotalPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static community.mingle.app.config.BaseResponseStatus.POST_NOT_EXIST;

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
        return em.createQuery("select i from ItemLike i where i.item.id = :itemId and i.member.id = :memberIdByJwt", ItemLike.class)
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

    public Item findItemById(Long itemId) throws BaseException {
        Item item = em.find(Item.class, itemId);
        if (item == null) {
            throw new BaseException(POST_NOT_EXIST);
        }
        return item;
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

    public void deleteItem(Long id) {
        em.remove(id);
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
        try {
            itemCommentWithMaxAnonymousId = itemComments.stream()
                    .max(Comparator.comparingLong(ItemComment::getAnonymousId))//nullPointerException
                    .get();
            newAnonymousId = itemCommentWithMaxAnonymousId.getAnonymousId() + 1;
            return newAnonymousId;

        } catch (NoSuchElementException e) {
            newAnonymousId = 1L;
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


    public void deleteItemComment(Long itemCommentId) {
        ItemComment itemComment = em.createQuery("select ic from ItemComment ic where ic.id = :itemCommentId", ItemComment.class)
                .setParameter("itemCommentId", itemCommentId)
                .getSingleResult();
        em.remove(itemComment);
    }


    public List<Item> searchItemWithKeyword(String keyword, Long memberIdByJwt) {

        List<Item> items = em.createQuery("SELECT i FROM Item i WHERE (i.title LIKE CONCAT('%',:keyword,'%') OR i.content LIKE CONCAT('%',:keyword,'%')) AND i.status IN (:statuses) and i.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) order by i.createdAt desc", Item.class)
                .setParameter("keyword", keyword)
                .setParameter("statuses", Arrays.asList(ItemStatus.SELLING, ItemStatus.RESERVED, ItemStatus.SOLDOUT, ItemStatus.NOTIFIED))
                .setParameter("memberIdByJwt", memberIdByJwt)
                .getResultList();
        return items;

    }
}
