package community.mingle.app.src.post;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

//    private final EntityManager em;
//
//    public Member findMemberbyId (Long id){
//        List<Member> m = em.createQuery("select m from Member m where m.id = :id", Member.class)
//                .setParameter("id", id)
//                .getResultList();
//        if (m.size() != 0) { //있으면 list 첫번째 element 반환. 중복은 없을테니
//            return m.get(0);
//        } else { //없으면 null 반환
//            return null;
//        }
//
//    }
//
//    public List<TotalPost> getTotalPosts(){
//        List
//    }
}
