package community.mingle.app.src.comment;


import community.mingle.app.src.comment.model.PostTotalCommentRequest;
import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalComment;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
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
     * 익명 몇 인지 찾기
     * @return
     */
    public Long findAnonymousId(TotalPost post, Member member) {
        Long anonymousId;
        Long newAnonymousId = null;
//        member.getTotal_comments().stream().findAny(member.)

        // 1번
        for (TotalComment total_comment : member.getTotal_comments()) {
            if (total_comment.isAnonymous() == true && total_comment.getTotalPost().getId().equals(post.getId())) {
                anonymousId = total_comment.getAnonymousId();
                newAnonymousId = anonymousId;
                return newAnonymousId;

            } else {
                List<TotalComment> totalComments = post.getTotalPostComments();

                //구글
                Comparator<TotalComment> comparatorByAnonymousId = Comparator.comparingLong(TotalComment::getAnonymousId);
                TotalComment totalCommentWithMaxAnonymousId = totalComments.stream()
                        .max(comparatorByAnonymousId)
                        .orElseThrow(NoSuchElementException::new);

                System.out.println("totalCommentWithMaxAnonymousId = " + totalCommentWithMaxAnonymousId.getAnonymousId());

                newAnonymousId = totalCommentWithMaxAnonymousId.getAnonymousId() + 1;
                return newAnonymousId;
            }
        }
        return newAnonymousId;
    }


//            ArrayList<TotalComment> totalComments = new ArrayList<>();

//            Collections.max(totalComments);
//            Collections.max(post.getTotalPostComments());
//
//
//            for (TotalComment totalComment : post.getTotalPostComments()) {
//
//            }
//        }
//        else {
//
//        }






        /*
        if (이미있음): post 중에 memberIdByJwt 로 쓴 댓글이있냐, 그거중에 isAnonymous = true {
            find()
        }
        else {

        }
        em.createQuery(select ... :anonymousId)
        .setParameter(anonymousId);
        */



    public TotalComment save(TotalComment comment) {
        em.persist(comment);
        return comment;

    }
}
