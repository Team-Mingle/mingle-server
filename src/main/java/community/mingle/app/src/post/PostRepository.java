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


    /**
     * 게시물 상세
     */
    //13개 -> 15ㄴㅋ -> 59개  (댓글29개)  //fetch join 201개
    public UnivPost findUnivPost(Long univPostId) {
        UnivPost univPost = em.createQuery("select distinct p from UnivPost p left join fetch p.member m " +
                        " join fetch p.univComments c join fetch p.univName u where p.id = :id", UnivPost.class)
                .setParameter("id", univPostId)
                .getSingleResult();
        return univPost;
    }

    //34개
    public UnivPost findUnivPostById(Long univPostId) {
        UnivPost univPost = em.createQuery("select p from UnivPost p where p.id = :id", UnivPost.class)
                .setParameter("id", univPostId)
                .getSingleResult();
        return univPost;
    }

    public List<UnivComment> findUnivComment(Long postId) { //without null? left join?
        Long id = null;
        List<UnivComment> univComments = em.createQuery("select c from UnivComment c where c.univPost.id = :id and c.parentCommentId IS NULL ", UnivComment.class)
                .setParameter("id", postId)
//                .setParameter("null", id)
                .setFirstResult(0)
                .setMaxResults(40)
                .getResultList();
        return univComments;
    }


    public List<UnivComment> findUnivCoComment(UnivPost p, UnivComment c) { // commentId = 1;
        List<UnivComment> univCoComments = em.createQuery("select distinct cc from UnivComment cc " +
                        "left join fetch cc.member m " +
                        " where cc.univPost.id = :id and cc.parentCommentId = :parentCommentId", UnivComment.class)
                .setParameter("parentCommentId", c.getId())
                .setParameter("id", p.getId())
                .setFirstResult(0)
                .setMaxResults(40)
                .getResultList();
        return univCoComments;
    }


//    public List<UnivComment> findAllComments() {
//        List<UnivComment> allComments = em.createQuery("select c, count(c.univCommentLikes) from UnivComment c join fetch c.univCommentLikes  ")
//    }



    //1. 포스트 찾음
    //2. 그 포스트의 댓글 찾음
    //3. 댓글 하나당 대댓글 찾음
    //4. 그 포스트의 댓글 (2) 리스트에서 parentCommentId 가 null 인 댓글들 리스트에서 하나씩 비교해가며 찾음. <Old 로직>
    // 3 & 4 포스트의 댓글, 대댓글 따로 다 찾음. 그다음에 댓글에 대댓글 리스트를 하나씩 매핑 <new 로직>

    /**
     * getUnivPostDetail >> NEW <<
     */
    public UnivPost getUnivPostById(Long id) {
        UnivPost univPost = em.createQuery("select up from UnivPost up where up.id = :id", UnivPost.class)
                .setParameter("id", id)
                .getSingleResult();
        return univPost;
    }

    public boolean checkIsLiked(Long postId, Long memberId) {
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

    public boolean checkIsScraped(Long postId, Long memberId) {
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


    /**
     * 댓글 좋아요
     * @param commentId
     * @param memberId
     * @return
     */
    public boolean checkCommentIsLiked(Long commentId, Long memberId) {
        List<UnivCommentLike> univCommentLikes = em.createQuery("select ucl from UnivCommentLike ucl join ucl.univComment uc join ucl.member m where uc.id = :commentId and m.id = :memberId", UnivCommentLike.class)
                .setParameter("commentId", commentId)
                .setParameter("memberId", memberId)
                .getResultList();
        if (univCommentLikes.size() != 0) {
            return true;
        } else {
            return false;
        }
    }
//    public boolean checkCoCommentLiked(List<UnivComment> coCommentList) {
//
//    }
}
