package community.mingle.app.src.member;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Report;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivComment;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostScrap;
import community.mingle.app.src.member.model.ReportRequest;
import community.mingle.app.src.member.model.UnivPostScrapDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static community.mingle.app.src.domain.PostStatus.INACTIVE;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;


    public Member findMember(Long userIdByJwt) {
        return em.find(Member.class, userIdByJwt);
    }

    public List<UnivPostScrapDTO> findScraps(Long id) {
        List resultList = em.createQuery("select m.univPostScraps, m.totalPostScraps from Member m where m.id = :id")
                .setParameter("id", id)
                .getResultList();
        return resultList;
    }

    public List<UnivPostScrapDTO> findScraps1(Long id) {
        List resultList = em.createQuery("select UnivPostScrap ,TotalPostScrap from UnivPostScrap us , TotalPostScrap ts where us.member.id = :id and ts.member.id = :id order by us.createdAt, ts.createdAt desc")
                .setParameter("id", id)
                .getResultList();
        return resultList;
    }

    public List<UnivPostScrap> findUnivScraps1(Long memberId) {
        List<UnivPostScrap> resultList = em.createQuery("select us from UnivPostScrap us join us.member m where m.id = :id order by us.createdAt desc", UnivPostScrap.class)
                .setParameter("id", memberId)
                .getResultList();
        return resultList;
    }

    public List<UnivPost> findUnivScraps(Long memberId) { // join fetch 안했을경우 : likeCount: size()
        List<UnivPost> resultList = em.createQuery("select p from UnivPostScrap us join us.member m join us.univPost p where m.id = :id order by p.createdAt desc", UnivPost.class)
                .setParameter("id", memberId)
                .getResultList();
        return resultList;
    }

    public List<TotalPost> findTotalScraps(Long memberId) { // join fetch 안했을경우 : likeCount: size()
        List<TotalPost> resultList = em.createQuery("select p from TotalPostScrap ts join ts.member m join ts.totalPost p where m.id = :id order by p.createdAt desc", TotalPost.class)
                .setParameter("id", memberId)
                .getResultList();
        return resultList;
    }

    public List<TotalPost> findTotalPosts(Long memberId) {
        List<TotalPost> resultList = em.createQuery("select p from TotalPost p join p.member m where m.id = :id order by p.createdAt desc", TotalPost.class)
                .setParameter("id", memberId)
                .getResultList();
        return resultList;
    }

    public List<UnivPost> findUnivPosts(Long memberId) {
        List<UnivPost> resultList = em.createQuery("select p from UnivPost p join p.member m where m.id = :id order by p.createdAt desc", UnivPost.class)
                .setParameter("id", memberId)
                .getResultList();
        return resultList;
    }

    public List<TotalPost> findTotalComments(Long memberId) {
        List<TotalPost> resultList = em.createQuery("select distinct p from TotalComment c join c.member m join c.totalPost p where m.id = :id order by c.createdAt desc ", TotalPost.class)
                .setParameter("id", memberId)
                .getResultList();
        return resultList;
    }

    public List<UnivPost> findUnivComments(Long memberId) {
        List<UnivPost> resultList = em.createQuery("select distinct p from UnivComment c join c.member m join c.univPost p where m.id = :id order by c.createdAt desc ", UnivPost.class)
                .setParameter("id", memberId)
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
