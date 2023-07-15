package community.mingle.app.src.home;

import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostImage;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HomeRepository {
    private final EntityManager em;


    /**
     * 5.1 광고 배너 API
     */
    public List<Banner> findBanner() {
        return em.createQuery("select b from Banner b", Banner.class)
                .getResultList();
    }

    public Member findMemberbyId(Long id) {
        try {
            return em.createQuery("select m from Member m where m.id = :id and m.status = :status", Member.class)
                    .setParameter("id", id)
                    .setParameter("status", UserStatus.ACTIVE)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
//        return em.find(Member.class, id);
    }

    public int save(Banner banner) {
        em.persist(banner);
        return banner.getId();
    }

    public void save(TotalPostImage totalPostImage) {
        em.persist(totalPostImage);
    }

    /**
     * 5.2 홈 전체 베스트 게시판 api
     */
    public List<TotalPost> findAllTotalPostWithMemberLikeComment(Long memberIdByJwt) {
        List<TotalPost> recentTotalPosts = em.createQuery("select p from TotalPost p join fetch p.member m where p.status = :status and p.totalPostLikes.size > 9 and p.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) order by p.createdAt desc", TotalPost.class)
                .setParameter("status", PostStatus.ACTIVE)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();
        return recentTotalPosts;
    }

    /**
     * 5.3 홈 학교 베스트 게시판 api
     *
     * @param member
     */
    public List<UnivPost> findAllUnivPostsWithMemberLikeCommentCount(Member member) {
        return em.createQuery(
                        "select p from UnivPost p join fetch p.member m where p.status = :status and p.univName.id = :univId  and p.univPostLikes.size > 4 and p.member.id  not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) order by p.createdAt desc ", UnivPost.class)
                .setParameter("status", PostStatus.ACTIVE)
                .setParameter("univId", member.getUniv().getId()) //어디 학교인지
                .setParameter("memberIdByJwt", member.getId())
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();
    }

    /**
     * 5.4 홈 전체 최신 게시글 api
     */
    public List<TotalPost> findTotalRecentPosts(Long memberIdByJwt) {
        
        return em.createQuery("select p from TotalPost p join fetch p.member m where p.status <> :status1 and p.status <> :status2 and p.member.id  not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) order by p.createdAt desc", TotalPost.class)
                .setParameter("status1", PostStatus.INACTIVE)
                .setParameter("status2", PostStatus.REPORTED)
                .setParameter("memberIdByJwt", memberIdByJwt)
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();
    }


    /**
     * 5.5 홈 학교 최신 게시글 api
     *
     * @param member
     */
    public List<UnivPost> findUnivRecentPosts(Member member) {
        return em.createQuery("select p from UnivPost p join fetch p.member m where p.status = :status and p.univName.id = :univId and p.member.id not in (select bm.blockedMember.id from BlockMember bm where bm.blockerMember.id = :memberIdByJwt) order by p.createdAt desc ", UnivPost.class)
                .setParameter("status", PostStatus.ACTIVE)
                .setParameter("univId", member.getUniv().getId()) //어디 학교인지
                .setParameter("memberIdByJwt", member.getId())
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();
    }


}
