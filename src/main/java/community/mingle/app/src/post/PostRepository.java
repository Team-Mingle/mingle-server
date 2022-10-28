package community.mingle.app.src.post;


import community.mingle.app.src.domain.*;
import community.mingle.app.src.domain.Total.*;
import community.mingle.app.src.domain.Univ.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;


    /**
     * 2.2 전체 베스트 게시판 api
     */
    public List<TotalPost> findTotalPostWithMemberLikeComment(Long postId) {
        List<TotalPost> recentTotalPosts = em.createQuery("select p from TotalPost p join fetch p.member m where p.status = :status and p.id < :postId and p.totalPostLikes.size > 10 order by p.createdAt desc", TotalPost.class)
                .setParameter("status", PostStatus.ACTIVE)
//                .setParameter("localDateTime", LocalDateTime.now().minusDays(3))
                .setParameter("postId", postId)
                .setMaxResults(50) //수정 필요
                .getResultList();

        return recentTotalPosts;
    }


    /**
     * 2.3 학교 베스트 게시판 api
     */
    public List<UnivPost> findAllWithMemberLikeCommentCount(Member member, Long postId) {
//        LocalDateTime minusDate = LocalDateTime.now().minusDays(5);
        return em.createQuery(
//              "select p from UnivPost p join fetch p.member join fetch p.univName u where u.id = :univId AND p.createdAt > :localDateTime order by p.univPostLikes.size desc, p.createdAt desc", UnivPost.class)
//              "select p from UnivPost p join fetch p.member m.univName.id = :univId p.createdAt BETWEEN :timestampStart AND current_timestamp ", UnivPost.class)
                "select p from UnivPost p join fetch p.member m where p.status = :status and p.univName.id = :univId  and p.univPostLikes.size > 5 and p.id < :postId  order by p.createdAt desc ", UnivPost.class)
                .setParameter("status", PostStatus.ACTIVE)
//                .setParameter("localDateTime", (LocalDateTime.now().minusDays(3))) //최근 3일중 likeCount 로 정렬. 좋아요 수가 같으면 최신순으로 정렬.
                .setParameter("univId", member.getUniv().getId()) //어디 학교인지
                .setParameter("postId", postId)
                .setMaxResults(50)
                .getResultList();
    }


    /**
     * 2.4 광장 게시판 api
     */
    public List<TotalPost> findTotalPost(int category, Long postId) {
        return em.createQuery("select p from TotalPost p join p.category as c join fetch p.member as m where p.status = :status and c.id = :categoryId and p.id < :postId order by p.createdAt desc ", TotalPost.class)
                .setParameter("status", PostStatus.ACTIVE)
                .setParameter("categoryId", category)
                .setParameter("postId", postId)
                .setMaxResults(50)
                .getResultList();
    }


    /**
     * 2.5 학교 게시판 api
     */
    public List<UnivPost> findUnivPost(int category, Long postId, int univId) {
        return em.createQuery("select p from UnivPost p join p.category as c join fetch p.member as m where p.status = :status and p.univName.id = :univId and c.id = :categoryId and p.id < :postId order by p.createdAt desc", UnivPost.class)
                .setParameter("status", PostStatus.ACTIVE)
                .setParameter("univId", univId)
                .setParameter("categoryId", category)
                .setParameter("postId", postId)
                .setMaxResults(50)
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
//        Category category = em.createQuery("select c from Category c where c.id = :id", Category.class)
//                .setParameter("id", id)
//                .getSingleResult();
//        return category;
        return em.find(Category.class, id);
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

    public List<TotalPostImage> findAllTotalImage(Long postId) {
        List<TotalPostImage> allTotalImages = em.createQuery("select pi from TotalPostImage pi where pi.totalPost.id = :id", TotalPostImage.class)
                .setParameter("id", postId)
                .getResultList();
        return allTotalImages;
    }


    public List<UnivComment> findAllUnivComment(Long postId) {
        List<UnivComment> allUnivComments = em.createQuery("select c from UnivComment c where c.univPost.id = :id", UnivComment.class)
                .setParameter("id", postId)
                .getResultList();
        return allUnivComments;
    }

    public List<UnivPostImage> findAllUnivImage(Long postId) {
        List<UnivPostImage> allUnivImages = em.createQuery("select pi from UnivPostImage pi where pi.univPost.id = :id", UnivPostImage.class)
                .setParameter("id", postId)
                .getResultList();
        return allUnivImages;
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

    /*
    1. 포스트 찾음
    2. 그 포스트의 댓글 찾음
    3. 댓글 하나당 대댓글 찾음
    4. 그 포스트의 댓글 (2) 리스트에서 parentCommentId 가 null 인 댓글들 리스트에서 하나씩 비교해가며 찾음. <Old 로직>
    3 & 4 포스트의 댓글, 대댓글 따로 다 찾음. 그다음에 댓글에 대댓글 리스트를 하나씩 매핑 <new 로직>
     */

    /**
     * 3.9 통합 게시물 상세 api
     */

//    public TotalPost getTotalPostbyId(Long id) {
//        TotalPost totalPost = em.createQuery("select tp from TotalPost tp where tp.id = :id order by tp.createdAt desc", TotalPost.class)
//                .setParameter("id", id)
//                .getSingleResult();
//        return  totalPost;
//    }

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


    //좋아요 중복 방지...ㅋ
//    public boolean checkIsLiked(Long postId, Long memberId) {
//        List<TotalPostLike> totalPostLikeList = em.createQuery("select m from Member m join m.totalPosts p join p.totalPostLikes tpl where p.id = :postId and m.id = :memberId", TotalPostLike.class)
//                .setParameter("postId", postId)
//                .setParameter("memberId", memberId)
//                .getResultList();
//        if (totalPostLikeList.size() != 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }

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
     * 3.9 대댓글 이슈 수정용
     * @param mentionId
     * @return
     */
    public TotalComment findTotalComment(Long mentionId) {
        return em.find(TotalComment.class, mentionId);
    }


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


    public boolean checkUnivPostIsScraped(Long postId, Long memberId){
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
     * 댓글에 해당하는 게시판 신고 또는 삭제 여부 확인
     */
    public TotalPost checkTotalPostDisabled(Long postId) {
        return em.find(TotalPost.class, postId);
    }

    public UnivPost checkUnivPostDisabled(Long postId) {
        return em.find(UnivPost.class, postId);
    }

    /**
     * 댓글 좋아요
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



    /**
     * 3.10 mentionId로 댓글 찾기
     */
    public UnivComment findUnivComment(Long mentionId) {
        return em.find(UnivComment.class, mentionId);
    }



    /**
     * 전체 게시판 검색 기능
     * @param keyword
     * @return totalPosts
     */
    public List<TotalPost> searchTotalPostWithKeyword(String keyword) {

        List<TotalPost> totalPosts = em.createQuery("SELECT tp FROM TotalPost tp WHERE (tp.title LIKE CONCAT('%',:keyword,'%') OR tp.content LIKE CONCAT('%',:keyword,'%')) AND tp.status = :status order by tp.createdAt desc" , TotalPost.class)
                .setParameter("keyword",keyword)
                .setParameter("status", PostStatus.ACTIVE)
                .getResultList();
        return totalPosts;

    }


    /**
     * 학교 게시판 검색 기능
     * @param keyword
     * @return totalPosts
     */
    public List<UnivPost> searchUnivPostWithKeyword(String keyword) {
        List<UnivPost> univPosts = em.createQuery("SELECT up FROM UnivPost up WHERE (up.title LIKE CONCAT('%',:keyword,'%') OR up.content LIKE CONCAT('%',:keyword,'%')) AND up.status = :status order by up.createdAt desc", UnivPost.class)
                .setParameter("keyword", keyword)
                .setParameter("status", PostStatus.ACTIVE)
                .getResultList();
        return univPosts;
    }


    public List<Category> getPostCategory() {
        List<Category> categoryList = em.createQuery("select c from Category c", Category.class)
                .getResultList();
        return categoryList;
    }


    /**
     * 게시물 작성 에러 handle
     */
    public void deleteTotalPost(Long postId) {
        TotalPost totalPost = em.find(TotalPost.class, postId);
        em.remove(postId);

    }

    public void deleteUnivPost(Long postId) {
        UnivPost univPost = em.find(UnivPost.class, postId);
        em.remove(postId);

    }


}
