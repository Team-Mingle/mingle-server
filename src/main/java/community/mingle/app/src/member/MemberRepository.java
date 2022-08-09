package community.mingle.app.src.member;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPost;
import community.mingle.app.src.domain.Univ.UnivPost;
import community.mingle.app.src.domain.Univ.UnivPostScrap;
import community.mingle.app.src.member.model.UnivPostScrapDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;


    public Member findMember(Long userIdByJwt) {
        return em.find(Member.class, userIdByJwt);
    }

    public List<UnivPostScrapDTO> findScraps(Long id) {
        List resultList = em.createQuery("select m.univPostScraps, m.totalPostScraps from Member m where m.id = :id")
                .setParameter("id", id)
                .getResultList();
        return resultList;
    }

    public List<UnivPostScrapDTO> findScraps1(Long id) {
        List resultList = em.createQuery("select UnivPostScrap ,TotalPostScrap from UnivPostScrap us , TotalPostScrap ts where us.member.id = :id and ts.member.id = :id order by us.createdAt, ts.createdAt desc")
                .setParameter("id", id)
                .getResultList();
        return resultList;
    }

    public List<UnivPostScrap> findUnivScraps1(Long memberId) {
        List<UnivPostScrap> resultList = em.createQuery("select us from UnivPostScrap us join us.member m where m.id = :id order by us.createdAt desc", UnivPostScrap.class)
                .setParameter("id", memberId)
                .getResultList();
        return resultList;
    }

    public List<UnivPost> findUnivScraps(Long memberId, Long postId) { // join fetch 안했을경우 : likeCount: size()
        List<UnivPost> resultList = em.createQuery("select p from UnivPostScrap us join us.member m join us.univPost p " +
                        "where m.id = :id and p.id < :postId order by p.createdAt desc", UnivPost.class)
                .setParameter("id", memberId)
                .setParameter("postId", postId)
                .setMaxResults(20)
                .getResultList();
        return resultList;
    }


    public List<TotalPost> findTotalScraps(Long memberId, Long postId) { // join fetch 안했을경우 : likeCount: size()
        List<TotalPost> resultList = em.createQuery("select p from TotalPostScrap ts join ts.member m join ts.totalPost p" +
                        " where m.id = :id and p.id < :postId order by p.createdAt desc", TotalPost.class)
                .setParameter("id", memberId)
                .setParameter("postId", postId)
                .setMaxResults(20)
                .getResultList();
        return resultList;
    }

//    public List<UnivPost> findUnivScrapsV2(Long memberId) { // join fetch 했을경우: 다 가져옴 리스트까지. / fetch join 은 별칭이 안됨.? Hibernate 는 됨? 에러. ㅠㅠ
//        List<UnivPost> resultList = em.createQuery("select p from UnivPostScrap us join us.member m join fetch us.univPost p where m.id = :id order by us.createdAt desc", UnivPost.class)
//                .setParameter("id", memberId)
//                .getResultList();
//        return resultList;
//    }
}
