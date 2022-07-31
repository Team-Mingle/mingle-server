package community.mingle.app.src.post;


import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Total.TotalPostScrap;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostScrap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;
    /**
     * 2.1 광고 배너 API
     */
    public List<Banner> findBanner(){
        return em.createQuery("select b from Banner b", Banner.class)
                .getResultList();

    }

    /**
     * 2.2 전체 베스트 게시판 api
     */
    public List<TotalPost> findTotalPostWithMemberLikeComment() {
        List<TotalPost> recentTotalPosts = em.createQuery("select p from TotalPost p join fetch p.member m where p.createdAt > :localDateTime order by p.totalPostLikes.size desc, p.createdAt desc", TotalPost.class)
                .setParameter("localDateTime", LocalDateTime.now().minusDays(3))
                .setFirstResult(0)
                .setMaxResults(40)
                .getResultList();

        return recentTotalPosts;
    }

    /**
     * 2.3 학교 베스트 게시판 api
     */
    public List<UnivPost> findAllWithMemberLikeCommentCount(Member member) {
//        LocalDateTime minusDate = LocalDateTime.now().minusDays(5);
        return em.createQuery(
//              "select p from UnivPost p join fetch p.member join fetch p.univName u where u.id = :univId AND p.createdAt > :localDateTime order by p.univPostLikes.size desc, p.createdAt desc", UnivPost.class)
//              "select p from UnivPost p join fetch p.member m.univName.id = :univId p.createdAt BETWEEN :timestampStart AND current_timestamp ", UnivPost.class)
                "select p from UnivPost p join fetch p.member m where p.univName.id = :univId AND p.createdAt > :localDateTime order by p.univPostLikes.size desc, p.createdAt desc ", UnivPost.class)
                .setParameter("localDateTime", (LocalDateTime.now().minusDays(3))) //최근 3일중 likeCount 로 정렬. 좋아요 수가 같으면 최신순으로 정렬.
                .setParameter("univId", member.getUniv().getId())
                .setFirstResult(0)
                .setMaxResults(40)
                .getResultList();
    }

    /**
     * 2.4 광장 게시판 api
     */
    public List<TotalPost> findTotalPost(int category) {
        return em.createQuery("select p from TotalPost p where p.category.id = :category order by p.createdAt desc", TotalPost.class)
                .setParameter("category", category)
                .getResultList();
    }

    /**
     * 2.5 학교 게시판 api
     */
    public List<UnivPost> findUnivPost(int category) {
        return em.createQuery("select u from UnivPost u where u.category.id = :category order by u.createdAt desc", UnivPost.class)
                .setParameter("category", category)
                .getResultList();
    }



    /**
     * memberId 로 Member 반환
     * @param id
     * @return Member
     */
    public Member findMemberbyId(Long id) {
        List<Member> m = em.createQuery("select m from Member m where m.id = :id", Member.class)
                .setParameter("id", id)
                .getResultList();
        if (m.size() != 0) { //있으면 list 첫번째 element 반환. 중복은 없을테니
            return m.get(0);
        } else { //없으면 null 반환
            return null;
        }
    }

    public Long save(UnivPost univPost) {
        em.persist(univPost);
        return univPost.getId();
    }

    public Category findCategoryById(int id) { //쿼리문에서 나는 에러는 if else 로 잡아서 null 로 보낼 수 없다.
        Category category = em.createQuery("select c from Category c where c.id = :id", Category.class)
                .setParameter("id", id)
                .getSingleResult();
        return category;
    }

    public Long save(TotalPostScrap totalPostScrap) {
        em.persist(totalPostScrap);
        return totalPostScrap.getId();
    }

    public Long save(UnivPostScrap univPostScrap) {
        em.persist(univPostScrap);
        return univPostScrap.getId();
    }

    public TotalPost findTotalPostbyId(Long postId) {
        List<TotalPost> totalPosts = em.createQuery("select tp from TotalPost tp where tp.id = :postId", TotalPost.class)
                .setParameter("postId", postId)
                .getResultList();
        if (totalPosts.size() != 0) {
            return totalPosts.get(0);
        } else {
            return null;
        }
    }

    public UnivPost findUnivPostbyId(Long postId) {
        List<UnivPost> univPosts = em.createQuery("select up from UnivPost up where up.id = :postId", UnivPost.class)
                .setParameter("postId", postId)
                .getResultList();
        if (univPosts.size() != 0) {
            return univPosts.get(0);
        } else {
            return null;
        }
    }




}
