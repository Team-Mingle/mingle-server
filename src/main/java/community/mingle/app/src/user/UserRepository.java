package community.mingle.app.src.user;

import community.mingle.app.src.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;



    public Member findMemberbyId(Long id) {
        List<Member> m = em.createQuery("select m from Member m where m.id = :id", Member.class)
                .setParameter("id", id)
                .getResultList();
        if (m.size() != 0) {
            return m.get(0);
        } else {
            return null;
        }
    }

    @Transactional
    public void deleteMember(Long id) {
        em.createQuery("update Member m set m.status = 'INACTIVE' where m.id = : id", Member.class)
                .setParameter("id", id)
                .executeUpdate();


    }
}
