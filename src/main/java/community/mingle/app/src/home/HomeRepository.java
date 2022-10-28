package community.mingle.app.src.home;

import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.PostStatus;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
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
    public List<Banner> findBanner(){
        return em.createQuery("select b from Banner b", Banner.class)
                .getResultList();
    }


    /**
     * 5.2 홈 전체 베스트 게시판 api
     */
    public List<TotalPost> findTotalPostWithMemberLikeComment() {
        List<TotalPost> recentTotalPosts = em.createQuery("select p from TotalPost p join fetch p.member m where p.status = :status and p.totalPostLikes.size > 10 order by p.createdAt desc", TotalPost.class)
                .setParameter("status", PostStatus.ACTIVE)
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();

        return recentTotalPosts;
    }


    /**
     * 5.2 홈 학교 베스트 게시판 api
     * @param id
     * @return
     */
    public Member findMemberbyId(Long id) {
        return em.find(Member.class, id);
    }

    public List<UnivPost> findAllWithMemberLikeCommentCount(Member member) {
        return em.createQuery(
                        "select p from UnivPost p join fetch p.member m where p.status = :status and p.univName.id = :univId  and p.univPostLikes.size > 5 order by p.createdAt desc ", UnivPost.class)
                .setParameter("status", PostStatus.ACTIVE)
                .setParameter("univId", member.getUniv().getId()) //어디 학교인지
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();
    }
}
