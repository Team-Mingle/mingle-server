package community.mingle.app.src.post;


import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.*;
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
     * 2.4 페이징 테스트
     */
    public List<TotalPost> findTotalPostByPaging(int category, Long postId) {
        return em.createQuery("select p from TotalPost p join p.category as c join fetch p.member as m where c.id = :categoryId and p.id < :postId order by p.createdAt desc ", TotalPost.class)
                .setParameter("categoryId", category)
                .setParameter("postId", postId)
                .setMaxResults(3)
                .getResultList();
    }



    /**
     * memberId 로 Member 반환
     */
    public Member findMemberbyId(Long id) {
        return em.find(Member.class, id);
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


    /*
    1. 포스트 찾음
    2. 그 포스트의 댓글 찾음
    3. 댓글 하나당 대댓글 찾음
    4. 그 포스트의 댓글 (2) 리스트에서 parentCommentId 가 null 인 댓글들 리스트에서 하나씩 비교해가며 찾음. <Old 로직>
    3 & 4 포스트의 댓글, 대댓글 따로 다 찾음. 그다음에 댓글에 대댓글 리스트를 하나씩 매핑 <new 로직>
     */


    /**
     * 3.10 getUnivPostDetail >> NEW <<
     */
    public UnivPost getUnivPostById(Long id) {
        UnivPost univPost = em.createQuery("select up from UnivPost up where up.id = :id", UnivPost.class)
                .setParameter("id", id)
                .getSingleResult();
        return univPost;
    }

    public boolean checkUnivPostIsLiked(Long postId, Long memberId) {
        List<UnivPostLike> univPostLikeList = em.createQuery("select upl from UnivPostLike upl join upl.univPost up join upl.member m where up.id = :postId and m.id = :memberId", UnivPostLike.class)
                .setParameter("postId", postId)
                .setParameter("memberId", memberId)
                .getResultList();
        if (univPostLikeList.size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkUnivPostIsScraped(Long postId, Long memberId) {
        List<UnivPostScrap> univPostScrapList = em.createQuery("select ups from UnivPostScrap ups join ups.univPost up join ups.member m where up.id = :postId and m.id = :memberId", UnivPostScrap.class)
                .setParameter("postId", postId)
                .setParameter("memberId", memberId)
                .getResultList();
        if (univPostScrapList.size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    //parentCommentId 가 null 인 댓글만 가져오기 (commentLike 는? )
    public List<UnivComment> getUnivComments(Long postId) {
        List<UnivComment> univCommentList = em.createQuery("select uc from UnivComment uc join uc.univPost as p" +
                " where p.id = :postId and uc.parentCommentId is null" +
                " order by uc.createdAt asc ", UnivComment.class)
                .setParameter("postId", postId)
                .getResultList();
        return univCommentList;
    }


    //대댓글만 가져오기 --> 페이징?
    public List<UnivComment> getUnivCoComments(Long postId) {
        List<UnivComment> univCoCommentList = em.createQuery("select uc from UnivComment uc join uc.univPost as p " +
                " where p.id = :postId and uc.parentCommentId is not null " +
                " order by uc.createdAt asc", UnivComment.class)
                .setParameter("postId", postId)
                .getResultList();
        return univCoCommentList;
    }


}
