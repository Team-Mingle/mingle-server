package community.mingle.app.src.comment;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final EntityManager em;


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


    /**
     * postId 로 Post 찾기
     * @param postId
     * @return
     */

    public TotalPost findTotalPostbyId(Long postId) {
        List<TotalPost> totalPosts = em.createQuery("select tp from TotalPost tp where tp.id = :postId", TotalPost.class)
                .setParameter("postId", postId)
                .getResultList();
        if (totalPosts.size() != 0) { //있으면 list 첫번째 element 반환. 중복은 없을테니
            return totalPosts.get(0);
        } else { //없으면 null 반환
            return null;
        }
    }

    /**
     * 익명 몇 인지 찾기 (anonymousId)
     * @return
     */
    public Long findAnonymousId(TotalPost post, Long memberIdByJwt ) {
        Long newAnonymousId;

        /**
         * case 1: 해당 게시글 커멘츠가 멤버가 있는지 없는지 확인하고 있으면 그 전 id 부여
         */
        List<TotalComment> totalCommentsByMember = em.createQuery("select tc from TotalComment tc where tc.totalPost.id = :postId and tc.member.id = :memberId and tc.isAnonymous = true", TotalComment.class)
                .setParameter("postId", post.getId())
                .setParameter("memberId", memberIdByJwt)
                .getResultList();
        if (totalCommentsByMember.size() != 0) { //있으면 list 첫번째 element 반환. 중복은 없을테니
             return totalCommentsByMember.get(0).getAnonymousId();
        }

        /**
         * case 2: 댓글 단 이력이 없고 익명 댓글을 달고싶을때: anonymousId 부여받음
         */
        List<TotalComment> totalComments = post.getTotalPostComments();

        TotalComment totalCommentWithMaxAnonymousId = null;
        try {  //게시물에서 제일 큰 id를 찾은 후 +1 한 id 를 내 댓글에 새로운 anonymousId 로 부여
            totalCommentWithMaxAnonymousId = totalComments.stream()
                    .max(Comparator.comparingLong(TotalComment::getAnonymousId))
                    .get();
            newAnonymousId = totalCommentWithMaxAnonymousId.getAnonymousId() + 1;
            return newAnonymousId;

        } catch (NoSuchElementException e) {  //게시물에 기존 익명 id 가 아예 없을때: id 로 1 부여
            newAnonymousId = Long.valueOf(1);
        }
        return newAnonymousId;
    }


    public TotalComment save(TotalComment comment) {
        em.persist(comment);
        return comment;
    }

}
