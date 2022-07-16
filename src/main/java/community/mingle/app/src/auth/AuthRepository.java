package community.mingle.app.src.auth;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.UnivName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuthRepository {

    private final EntityManager em;



    public UnivName findUniv(int id) {
        return em.find(UnivName.class, id);
    }

    public boolean findNickname (String nickname) {
        List<Member> duplicatedNickname= em.createQuery("select m from Member m where m.nickname = :nickname", Member.class)
                .setParameter("nickname", nickname)
                .getResultList();
        if (duplicatedNickname.size() != 0) {
            return true;
        }

        else if (duplicatedNickname.size() == 0){
            return false;
        }

        else return false;
    }

    public boolean findEmail (String email) {
        List<Member> duplicatedEmail= em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();
        if (duplicatedEmail.size() != 0) {
            return true;
        }

        else if (duplicatedEmail.size() == 0){
            return false;
        }

        else return false;
    }

    public List<Member> findByName(String nickname) {
        return em.createQuery("select m from Member m where m.nickname = :nickname", Member.class)
                .setParameter("nickname", nickname)
                .getResultList();
    }

    public List<Member> findByEmail(String email) {
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();
    }


    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }


}
