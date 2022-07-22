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
            System.out.println("total_comment = " + total_comment.toString());
            if (total_comment.isAnonymous() == true && total_comment.getTotalPost().getId().equals(post.getId())) {
                System.out.println("내가 쓰려는 글에 익명 댓글을 단 이력이 있을때 ");
                anonymousId = total_comment.getAnonymousId();
                System.out.println("찾았나?  ");

                newAnonymousId = anonymousId;
                return newAnonymousId;

//            } else { //여기 안에 들어가면 안됨
//                continue;
            }
        }

        System.out.println("이력이 없을때 익명댓글 달고싶을때 anonymousId 부여받음 ");
        List<TotalComment> totalComments = post.getTotalPostComments();

        //기존 익명id 확인 전 아예 없을때 (1번 부여)

        //구글
        TotalComment totalCommentWithMaxAnonymousId = null;
        try {
//            Comparator<TotalComment> comparatorByAnonymousId = Comparator.comparingLong(TotalComment::getAnonymousId);
//            totalCommentWithMaxAnonymousId = totalComments.stream()
//                    .max(comparatorByAnonymousId)
//                    .orElseThrow(NoSuchElementException::new); //max 를 못찾음

//            carList.stream()
//                    .max(Comparator.comparingInt(Car::getPosition))
//                    .get();
//            newAnonymousId = totalCommentWithMaxAnonymousId.getAnonymousId() + 1;
//            return newAnonymousId;
            totalCommentWithMaxAnonymousId = totalComments.stream()
                    .max(Comparator.comparingLong(TotalComment::getAnonymousId))
                    .get();
            newAnonymousId = totalCommentWithMaxAnonymousId.getAnonymousId() + 1;
            return newAnonymousId;

        } catch (NoSuchElementException e) {
            newAnonymousId = Long.valueOf(1);
        } finally {
            System.out.println("totalCommentWithMaxAnonymousId = " + newAnonymousId);
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
