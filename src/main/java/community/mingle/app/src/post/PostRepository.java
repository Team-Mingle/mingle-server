package community.mingle.app.src.post;


import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
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
        return em.createQuery("select p from TotalPost p where p.category.id = :category ", TotalPost.class)
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

    public Category findCategoryById(int id) {
        Category category = em.createQuery("select c from Category c where c.id = :id", Category.class)
                .setParameter("id", id)
                .getSingleResult();
        return category;
    }
}