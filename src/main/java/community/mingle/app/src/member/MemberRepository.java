package community.mingle.app.src.member;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Report;
import community.mingle.app.src.domain.Total.*;
import community.mingle.app.src.domain.Univ.*;
import community.mingle.app.src.member.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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
     * 2.3
     */
    public List<TotalPost> findTotalPosts(Long memberId, Long postId) {
        List<TotalPost> resultList = em.createQuery("select p from TotalPost p join p.member m where m.id = :id AND p.id < :postId and p.status = :status order by p.createdAt desc", TotalPost.class)
                .setParameter("id", memberId)
                .setParameter("postId", postId)
                .setParameter("status", PostStatus.ACTIVE)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }

    /**
     * 2.4
     */
    public List<UnivPost> findUnivPosts(Long memberId, Long postId) {
        List<UnivPost> resultList = em.createQuery("select p from UnivPost p join p.member m where m.id = :id and p.id < :postId and p.status = :status order by p.createdAt desc", UnivPost.class)
                .setParameter("id", memberId)
                .setParameter("postId", postId)
                .setParameter("status", PostStatus.ACTIVE)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }

    /**
     * 2.5
     */
    public List<TotalPost> findTotalComments(Long memberId, Long postId) {
        List<TotalPost> resultList = em.createQuery("select distinct p from TotalComment c join c.member m join c.totalPost p where m.id = :id and p.id < :postId AND p.status = :status order by c.createdAt desc ", TotalPost.class)
                .setParameter("id", memberId)
                .setParameter("postId", postId)
                .setParameter("status", PostStatus.ACTIVE)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }

    /**
     * 2.6
     */
    public List<UnivPost> findUnivComments(Long memberId, Long postId) {
        List<UnivPost> resultList = em.createQuery("select distinct p from UnivComment c join c.member m join c.univPost p where m.id = :id AND p.id < :postId AND p.status = :status order by c.createdAt desc ", UnivPost.class)
                .setParameter("id", memberId)
                .setParameter("postId", postId)
                .setParameter("status", PostStatus.ACTIVE)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }


    /**
     * 2.7 내가 스크랩한 글 - 대학
     */
    public List<UnivPost> findUnivScraps(Long memberId, Long postId) { // join fetch 안했을경우 : likeCount: size()
        List<UnivPost> resultList = em.createQuery("select p from UnivPostScrap us join us.member m join us.univPost p " +
                        "where m.id = :id and p.id < :postId and p.status = :status order by p.createdAt desc", UnivPost.class)
                .setParameter("id", memberId)
                .setParameter("postId", postId)
                .setParameter("status", PostStatus.ACTIVE)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }


    /**
     * 2.8 내가 스크랩 한 글 - 전체
     */
    public List<TotalPost> findTotalScraps(Long memberId, Long postId) { // join fetch 안했을경우 : likeCount: size()
        List<TotalPost> resultList = em.createQuery("select p from TotalPostScrap ts join ts.member m join ts.totalPost p" +
                        " where m.id = :id and p.id < :postId order by p.createdAt desc", TotalPost.class)
                .setParameter("id", memberId)
                .setParameter("postId", postId)
                .setMaxResults(50)
                .getResultList();
        return resultList;
    }


    /**
     * report API
     */
    public boolean isMultipleReport(ReportRequest reportRequest, Long memberId) {
        Long countMultipleReport = em.createQuery("select count(r) from Report r where r.tableId = :tableId and r.contentId = :contentId and r.reporterMemberId = :memberId", Long.class)
                .setParameter("tableId", reportRequest.getTableId())
                .setParameter("contentId", reportRequest.getContentId())
                .setParameter("memberId", memberId)
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

    public Long reportSave(Report report) {
        em.persist(report);
        return report.getReportId();
    }

    public Long countMemberReport(Long memberId) {
        Long countMember = em.createQuery("select count(r.reportedMemberId) from Report r where r.reportedMemberId = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
        return countMember;
    }

    public Long countContentReport(ReportRequest reportRequest) {
        Long countContent = em.createQuery("select count(r) from Report r where r.tableId =:tableId and r.contentId =: contentId", Long.class)
                .setParameter("tableId", reportRequest.getTableId())
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



//    public List<UnivPost> findUnivScrapsV2(Long memberId) { // join fetch 했을경우: 다 가져옴 리스트까지. / fetch join 은 별칭이 안됨.? Hibernate 는 됨? 에러. ㅠㅠ
//        List<UnivPost> resultList = em.createQuery("select p from UnivPostScrap us join us.member m join fetch us.univPost p where m.id = :id order by us.createdAt desc", UnivPost.class)
//                .setParameter("id", memberId)
//                .getResultList();
//        return resultList;
//    }


}
