package community.mingle.app.src.member;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.Total.TotalPostScrap;
import community.mingle.app.src.member.model.ScrapDTO;
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

    public List<ScrapDTO> findScraps(Long id) {
        List resultList = em.createQuery("select m.univPostScraps, m.totalPostScraps from Member m where m.id = :id")
                .setParameter("id", id)
                .getResultList();
        return resultList;
    }

    public List<ScrapDTO> findScraps1(Long id) {
        List resultList = em.createQuery("select UnivPostScrap ,TotalPostScrap from UnivPostScrap us , TotalPostScrap ts where us.member.id = :id and ts.member.id = :id order by us.createdAt, ts.createdAt desc")
                .setParameter("id", id)
                .getResultList();
        return resultList;
    }
}
