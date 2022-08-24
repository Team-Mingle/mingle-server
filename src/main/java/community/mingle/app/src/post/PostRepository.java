package community.mingle.app.src.post;


import community.mingle.app.src.domain.Banner;
import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;

import community.mingle.app.src.domain.Univ.UnivComment;

import community.mingle.app.src.domain.Total.TotalPostLike;
import community.mingle.app.src.domain.Total.TotalPostScrap;

import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostLike;
import community.mingle.app.src.domain.Univ.UnivPostScrap;

import community.mingle.app.src.domain.Total.*;

import community.mingle.app.src.domain.Univ.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


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

    public Long save(TotalPost totalPost) {
        em.persist(totalPost);
        return totalPost.getId();
    }
    public Long save(UnivPost univPost) {
        em.persist(univPost);
        return univPost.getId();
    }

    public void save(TotalPostImage totalPostImage) {
        em.persist(totalPostImage);
    }

    public void save(UnivPostImage univPostImage) {
        em.persist(univPostImage);
    }



    public Category findCategoryById(int id) { //쿼리문에서 나는 에러는 if else 로 잡아서 null 로 보낼 수 없다.
        Category category = em.createQuery("select c from Category c where c.id = :id", Category.class)
                .setParameter("id", id)
                .getSingleResult();
        return category;
    }

    public TotalPost findTotalPostById(Long id) {
        return em.find(TotalPost.class,id);
    }
    public UnivPost findUnivPostById(Long id) {
        return em.find(UnivPost.class, id);
    }

    public List<TotalComment> findAllTotalComment(Long postId) {
        List<TotalComment> allTotalComments = em.createQuery("select c from TotalComment c where c.totalPost.id = :id", TotalComment.class)
                .setParameter("id", postId)
                .getResultList();
        return allTotalComments;
    }


    public List<UnivComment> findAllUnivComment(Long postId) {
        List<UnivComment> allUnivComments = em.createQuery("select c from UnivComment c where c.univPost.id = :id", UnivComment.class)
                .setParameter("id", postId)
                .getResultList();
        return allUnivComments;
    }



    public Long save(TotalPostScrap totalPostScrap) {
        em.persist(totalPostScrap);
        return totalPostScrap.getId();
    }

    public Long save(UnivPostScrap univPostScrap) {
        em.persist(univPostScrap);
        return univPostScrap.getId();
    }

    public Long save(TotalPostLike totalPostLike) {
        em.persist(totalPostLike);
        return totalPostLike.getId();
    }

    public Long save(UnivPostLike univPostLike) {
        em.persist(univPostLike);
        return univPostLike.getId();
    }



    public TotalPost findTotalPostbyId(Long likeId) {
        List<TotalPost> totalPosts = em.createQuery("select tp from TotalPostLike tp where tp.id = :postId", TotalPost.class)
                .setParameter("postId", likeId)
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



    public void deleteTotalLike(Long likeIdx) {
        TotalPostLike findLike = em.find(TotalPostLike.class, likeIdx);
        em.remove(findLike);

    }

    public void deleteUnivLike(Long likeIdx) {
        UnivPostLike findLike = em.find(UnivPostLike.class, likeIdx);
        em.remove(findLike);

    }


    public void deleteTotalScrap(Long scrapIdx) {
        TotalPostScrap findScrap = em.find(TotalPostScrap.class, scrapIdx);
        em.remove(findScrap);

    }


    public void deleteUnivScrap(Long scrapIdx) {
        UnivPostScrap findScrap = em.find(UnivPostScrap.class, scrapIdx);
        em.remove(findScrap);

    }

    /**
     * 3.9 통합 게시물 상세 api
     */

    public TotalPost getTotalPostbyId(Long id) {
        TotalPost totalPost = em.createQuery("select tp from TotalPost tp where tp.id = :id order by tp.createdAt desc", TotalPost.class)
                .setParameter("id", id)
                .getSingleResult();
        return  totalPost;
    }

    public List<TotalComment> getTotalComments(Long id) {
        List<TotalComment> totalCommentList = em.createQuery("select tc from TotalComment tc join tc.totalPost as tp where tp.id = :id and tc.parentCommentId is null order by tc.createdAt asc", TotalComment.class)
                .setParameter("id", id)
                .getResultList();
        return totalCommentList;
    }

    public List<TotalComment> getTotalCocomments(Long id) {
        List<TotalComment> totalCocommentList = em.createQuery("select tc from TotalComment tc join tc.totalPost as tp where tp.id = :id and tc.parentCommentId is not null order by tc.createdAt asc", TotalComment.class)
                .setParameter("id", id)
                .getResultList();
        return totalCocommentList;
    }

    public boolean checkTotalIsLiked(Long postId, Long memberId) {
        List<TotalPostLike> totalPostLikeList = em.createQuery("select tpl from TotalPostLike tpl join tpl.totalPost tp join tpl.member m where tp.id = :postId and m.id = :memberId", TotalPostLike.class)
                .setParameter("postId", postId)
                .setParameter("memberId", memberId)
                .getResultList();
        if (totalPostLikeList.size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkTotalIsScraped(Long postId, Long memberId){
        List<TotalPostScrap> totalPostScrapList = em.createQuery("select tps from TotalPostScrap tps join tps.totalPost tp join tps.member m where tp.id = :postId and m.id = :memberId", TotalPostScrap.class)
                .setParameter("postId", postId)
                .setParameter("memberId", memberId)
                .getResultList();
        if (totalPostScrapList.size() != 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * getUnivPostDetail >> NEW <<
     */
    public UnivPost getUnivPostById(Long id) {
        UnivPost univPost = em.createQuery("select up from UnivPost up where up.id = :id", UnivPost.class)
                .setParameter("id", id)
                .getSingleResult();
        return univPost;
    }

    public boolean checkUnivIsLiked(Long postId, Long memberId) {
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



    public boolean checkUnivIsScraped(Long postId, Long memberId){
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
    public boolean checkUnivCommentIsLiked(Long commentId, Long memberId) {
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

}
