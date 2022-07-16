package community.mingle.app.src.auth;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.UnivName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class AuthRepository {

    private final EntityManager em;


    public UnivName findOne(int id) {
        return em.find(UnivName.class, id);
    }

    public void save(Member member) {
        em.persist(member);
    }
}
