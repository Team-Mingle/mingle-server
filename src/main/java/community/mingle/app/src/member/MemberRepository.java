package community.mingle.app.src.member;

import community.mingle.app.src.domain.*;
import community.mingle.app.src.domain.Total.*;
import community.mingle.app.src.domain.Univ.*;
import community.mingle.app.src.member.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;


    /**
     * 2.X 유저 조회
     */
    public Member findMember(Long userIdByJwt) {
        return em.find(Member.class, userIdByJwt);
    }


    /**
     * 2.2 +
     */
    public List<TotalPost> findTotalPosts(Long memberIdByJwt, Long postId) {
        List<TotalPost> resultList = em.createQuery("select p from TotalPost p join p.member m where p.status <> :status and m.id = :id and p.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) AND p.id < :postId order by p.createdAt desc", TotalPost.class)
                .setParameter("status", PostStatus.INACTIVE)
                .setParameter("id", memberIdByJwt)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("postId", postId)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }

    /**
     * 2.3 +
     */
    public List<UnivPost> findUnivPosts(Long memberIdByJwt, Long postId) {
        List<UnivPost> resultList = em.createQuery("select p from UnivPost p join p.member m where p.status <> :status and m.id = :id and p.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) and p.id < :postId order by p.createdAt desc", UnivPost.class)
                .setParameter("status", PostStatus.INACTIVE)
                .setParameter("id", memberIdByJwt)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("postId", postId)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }

    /**
     * 2.4
     */
    public List<TotalPost> findTotalComments(Long memberIdByJwt, Long postId) {
        List<TotalPost> resultList = em.createQuery("select distinct p from TotalComment c join c.member m join c.totalPost p where p.status <> :status1 and c.status = :status2 and m.id = :id and p.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) and p.id < :postId order by c.createdAt desc ", TotalPost.class)
                .setParameter("status1", PostStatus.INACTIVE)
                .setParameter("status2", PostStatus.ACTIVE)
                .setParameter("id", memberIdByJwt)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("postId", postId)
//                .setParameter("status", PostStatus.ACTIVE)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }

    /**
     * 2.5
     */
    public List<UnivPost> findUnivComments(Long memberIdByJwt, Long postId) {
        List<UnivPost> resultList = em.createQuery("select distinct p from UnivComment c join c.member m join c.univPost p where p.status <> :status1 and c.status = :status2 and m.id = :id AND p.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) and p.id < :postId order by c.createdAt desc ", UnivPost.class)
                .setParameter("status1", PostStatus.INACTIVE)
                .setParameter("status2", PostStatus.ACTIVE)
                .setParameter("id", memberIdByJwt)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("postId", postId)
//                .setParameter("status", PostStatus.ACTIVE)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }


    /**
     * 2.7 내가 스크랩한 글 - 대학
     */
    public List<UnivPost> findUnivScraps(Long memberIdByJwt, Long postId) { // join fetch 안했을경우 : likeCount: size()
        List<UnivPost> resultList = em.createQuery("select p from UnivPostScrap us join us.member m join us.univPost p " +
                        "where  p.status <> :status and m.id = :id and p.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) and p.id < :postId order by p.createdAt desc", UnivPost.class)
                .setParameter("status", PostStatus.INACTIVE)
                .setParameter("id", memberIdByJwt)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("postId", postId)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }


    /**
     * 2.8 내가 스크랩 한 글 - 전체
     */
    public List<TotalPost> findTotalScraps(Long memberIdByJwt, Long postId) { // join fetch 안했을경우 : likeCount: size()
        List<TotalPost> resultList = em.createQuery("select p from TotalPostScrap ts join ts.member m join ts.totalPost p" +
                        " where p.status <> :status and m.id = :id and p.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) and p.id < :postId order by p.createdAt desc", TotalPost.class)
                .setParameter("status", PostStatus.INACTIVE)
                .setParameter("id", memberIdByJwt)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("postId", postId)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }



    /**
     * 2.9 내가 좋아요 한 광장 글
     */
    public List<TotalPost> findTotalLikes(Long memberIdByJwt, Long postId) {
        List<TotalPost> resultList = em.createQuery("select p from TotalPostLike us join us.member m join us.totalPost p " +
                        "where p.status <> :status and m.id = :id and p.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) and p.id < :postId order by p.createdAt desc", TotalPost.class)
                .setParameter("status", PostStatus.INACTIVE)
                .setParameter("id", memberIdByJwt)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("postId", postId)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }


    /**
     * 2.10 내가 좋아요 한 잔디밭 글
     */
    public List<UnivPost> findUnivLikes(Long memberIdByJwt, Long postId) {
        List<UnivPost> resultList = em.createQuery("select p from UnivPostLike us join us.member m join us.univPost p " +
                        "where p.status <> :status and m.id = :id and p.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) and p.id < :postId order by p.createdAt desc", UnivPost.class)
                .setParameter("status", PostStatus.INACTIVE)
                .setParameter("id", memberIdByJwt)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setParameter("postId", postId)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }



    /**
     * report API
     */
    public boolean isMultipleReport(ReportRequest reportRequest, Long memberIdByJwt) {
        Long countMultipleReport = em.createQuery("select count(r) from Report r where r.tableId = :tableId and r.contentId = :contentId and r.reporterMemberId = :memberIdByJwt", Long.class)
                .setParameter("tableId", reportRequest.getTableType())
                .setParameter("contentId", reportRequest.getContentId())
                .setParameter("memberIdByJwt", memberIdByJwt)
                .getSingleResult();
        if (countMultipleReport != 0) {
            return true;
        } else {
            return false;
        }
    }

    public Member findReportedTotalPostMember(Long contentId) {
        Member reportedMember = em.createQuery("select m from TotalPost tp join tp.member m where tp.id = :contentId", Member.class)
                .setParameter("contentId", contentId)
                .getSingleResult();
        return reportedMember;
    }

    public Member findReportedTotalCommentMember(Long contentId) {
        Member reportedMember = em.createQuery("select m from TotalComment tc join tc.member m where tc.id = :contentId", Member.class)
                .setParameter("contentId", contentId)
                .getSingleResult();
        return reportedMember;
    }

    public Member findReportedUnivPostMember(Long contentId) {
        Member reportedMember = em.createQuery("select m from UnivPost up join up.member m where up.id = :contentId", Member.class)
                .setParameter("contentId", contentId)
                .getSingleResult();
        return reportedMember;
    }

    public Member findReportedUnivCommentMember(Long contentId) {
        Member reportedMember = em.createQuery("select m from UnivComment uc join uc.member m where uc.id = :contentId", Member.class)
                .setParameter("contentId", contentId)
                .getSingleResult();
        return reportedMember;
    }

    public Member findReportedItemMember(Long contentId) {
        Member reportedMember = em.createQuery("select m from Item i join i.member m where i.id = :contentId", Member.class)
                .setParameter("contentId", contentId)
                .getSingleResult();
        return reportedMember;
    }

    public Member findReportedItemCommentMember(Long contentId) {
        Member reportedMember = em.createQuery("select m from ItemComment i join i.member m where i.id = :contentId", Member.class)
                .setParameter("contentId", contentId)
                .getSingleResult();
        return reportedMember;
    }

    public Long reportSave(Report report) {
        em.persist(report);
        return report.getReportId();
    }

    public Long countMemberReport(Long memberIdByJwt) {
        Long countMember = em.createQuery("select count(r.reportedMemberId) from Report r where r.reportedMemberId = :memberIdByJwt", Long.class)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .getSingleResult();
        return countMember;
    }

    public Long countContentReport(ReportRequest reportRequest) {
        Long countContent = em.createQuery("select count(r) from Report r where r.tableId =:tableId and r.contentId =: contentId", Long.class)
                .setParameter("tableId", reportRequest.getTableType())
                .setParameter("contentId", reportRequest.getContentId())
                .getSingleResult();
        return countContent;
    }

    public TotalPost findReportedTotalPost(Long totalPostId) {
        TotalPost reportedTotalPost = em.createQuery("select tp from TotalPost tp where tp.id = :totalPostId", TotalPost.class)
                .setParameter("totalPostId", totalPostId)
                .getSingleResult();
        return reportedTotalPost;
    }
    //select tc from TotalComment tc join tc.totalPost tp where tp.id = :totalPostId
    public int findReportedTotalCommentsByPostId(Long totalPostId) {
        int reportedTotalComments = em.createQuery("update TotalComment tc set tc.status = 'INACTIVE' where tc.totalPost.id = :totalPostId")
                .setParameter("totalPostId", totalPostId)
                .executeUpdate();
        return reportedTotalComments;
    }

    public TotalComment findReportedTotalCommentByCommentId(Long totalCommentId) {
        TotalComment reportedTotalComment = em.createQuery("select tc from TotalComment tc where tc.id = :totalCommentId", TotalComment.class)
                .setParameter("totalCommentId", totalCommentId)
                .getSingleResult();
        return reportedTotalComment;
    }

    public UnivPost findReportedUnivPost(Long univPostId) {
        UnivPost reportedUnivPost = em.createQuery("select up from UnivPost up where up.id = :univPostId", UnivPost.class)
                .setParameter("univPostId", univPostId)
                .getSingleResult();
        return reportedUnivPost;
    }

    public int findReportedUnivCommentsByPostId(Long univPostId) {
        int reportedUnivComments = em.createQuery("update UnivComment uc set uc.status = 'INACTIVE' where uc.univPost.id = :univPostId")
                .setParameter("univPostId", univPostId)
                .executeUpdate();
        return reportedUnivComments;
    }

    public UnivComment findReportedUnivCommentByCommentId(Long univCommentId) {
        UnivComment reportedUnivComment = em.createQuery("select uc from UnivComment uc where uc.id = :univCommentId", UnivComment.class)
                .setParameter("univCommentId", univCommentId)
                .getSingleResult();
        return reportedUnivComment;
    }

    /**
     * 거래게시판
     */
    public Item findReportedItemByItemId(Long itemId) {
        Item reportedItem = em.createQuery("select i from Item i where i.id = :itemId", Item.class)
                .setParameter("itemId", itemId)
                .getSingleResult();
        return reportedItem;
    }

    public int findReportedItemCommentsByItemId(Long itemId) {
        int reportedItemComments = em.createQuery("update ItemComment ic set ic.status = 'INACTIVE' where ic.item.id = :itemId")
                .setParameter("itemId", itemId)
                .executeUpdate();
        return reportedItemComments;
    }

    public ItemComment findReportedItemCommentById(Long id) {
        ItemComment reportedItemComment = em.createQuery("select ic from ItemComment ic where ic.id = :id", ItemComment.class)
                .setParameter("id", id)
                .getSingleResult();
        return reportedItemComment;
    }





    /**
     * 알림 리스트
     **/

    public List<UnivNotification> getUnivNotification(Long userIdByJwt) {
        List<UnivNotification> getUnivNotification = em.createQuery("select n from UnivNotification n where n.member.id = :userIdByJwt order by n.createdAt desc", UnivNotification.class)
                .setParameter("userIdByJwt",userIdByJwt)
                .setMaxResults(20)
                .getResultList();
        return getUnivNotification;
    }


    public List<TotalNotification> getTotalNotification(Long userIdByJwt) {
        List<TotalNotification> resultList = em.createQuery("select t from TotalNotification t where t.member.id = :userIdByJwt order by t.createdAt desc", TotalNotification.class)
                .setParameter("userIdByJwt",userIdByJwt)
                .setMaxResults(20)
                .getResultList();
        return resultList;
    }

    public List<ReportNotification> getReportNotification(Long userIdByJwt) {
        List<ReportNotification> resultList = em.createQuery("select r from ReportNotification r where r.memberId = :userIdByJwt order by r.createdAt desc", ReportNotification.class)
                .setParameter("userIdByJwt",userIdByJwt)
                .setMaxResults(20)
                .getResultList();
        return resultList;
    }


    /**알림 읽음 여부**/

    public UnivNotification findUnivNotification(Long notificationId) {
        UnivNotification readUnivNotification = em.createQuery("select n from UnivNotification n where n.id = :notificationId", UnivNotification.class)
                .setParameter("notificationId", notificationId)
                .getSingleResult();
        return readUnivNotification;
    }


    public TotalNotification findTotalNotification(Long notificationId) {
        TotalNotification readTotalNotification = em.createQuery("select t from TotalNotification t where t.id = :notificationId", TotalNotification.class)
                .setParameter("notificationId", notificationId)
                .getSingleResult();
        return readTotalNotification;
    }

    public ItemNotification findItemNotification(Long notificationId) {
        ItemNotification readItemNotification = em.createQuery("select i from ItemNotification i where i.id = :notificationId", ItemNotification.class)
                .setParameter("notificationId", notificationId)
                .getSingleResult();
        return readItemNotification;
    }


    public void saveUnivNotification(UnivNotification univNotification){
        em.persist(univNotification);
    }
    public void saveTotalNotification(TotalNotification totalNotification){
        em.persist(totalNotification);
    }

    public List<String> findAllFcmToken() {
        List<String> resultList = em.createQuery("select m.fcmToken from Member m where m.fcmToken is not null and m.status = :status", String.class)
                .setParameter("status", UserStatus.ACTIVE)
                .getResultList();
        return resultList;
    }

    /**
     * 2.16 내가 찜한 거래 게시물
     */
    public List<Item> findLikedItems(Long itemId, Long memberId) {
        List<Item> resultList = em.createQuery("select i from ItemLike  il join il.member m join il.item i where i.status <> :status and m.id = :id and " +
                "i.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) and i.id < :itemId order by i.createdAt desc", Item.class)
                .setParameter("status", ItemStatus.INACTIVE)
                .setParameter("id", memberId)
                .setParameter("memberIdByJwt",memberId)
                .setParameter("itemId", itemId)
                .setMaxResults(20)
                .getResultList();
        return resultList;
    }


    /**
     * 2.17
     */
    public List<Item> findMyItemsByItemStatus(Long itemId, Long memberId, String itemStatus) {
        List<Item> resultList = em.createQuery("select i from Item i join i.member m where i.status = :status and i.status <> :status1 and i.status <> :status2 and m.id = :memberId and " +
                        "i.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberId) and i.id < :itemId order by i.createdAt desc", Item.class)
                .setParameter("status", ItemStatus.valueOf(itemStatus))
                .setParameter("status1", ItemStatus.INACTIVE)
                .setParameter("status2",ItemStatus.REPORTED)
                .setParameter("memberId", memberId)
                .setParameter("itemId", itemId)
                .setMaxResults(20)
                .getResultList();
        return resultList;
    }


//    public List<UnivPost> findUnivScrapsV2(Long memberIdByJwt) { // join fetch 했을경우: 다 가져옴 리스트까지. / fetch join 은 별칭이 안됨.? Hibernate 는 됨? 에러. ㅠㅠ
//        List<UnivPost> resultList = em.createQuery("select p from UnivPostScrap us join us.member m join fetch us.univPost p where m.id = :id order by us.createdAt desc", UnivPost.class)
//                .setParameter("id", memberIdByJwt)
//                .getResultList();
//        return resultList;
//    }

    public List<Member> findMemberByUnivId(int univId) {
        return em.createQuery("select m from Member m where m.univ.id = :univId", Member.class)
                .setParameter("univId", univId)
                .getResultList();
    }


}
