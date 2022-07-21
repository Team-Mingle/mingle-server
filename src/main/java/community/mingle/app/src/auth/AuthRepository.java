package community.mingle.app.src.auth;

import community.mingle.app.src.domain.Member;
import community.mingle.app.src.domain.UnivEmail;
import community.mingle.app.src.domain.UnivName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuthRepository {

    private final EntityManager em;


    // { [id:1, name hku], [id:2, name: hkust]....}
    public List<UnivName> findAll() {
        return em.createQuery("select u from UnivName u", UnivName.class)
                .getResultList();
    }

    public List<UnivEmail>  findByUniv(int univId) {
        return em.createQuery("select e from UnivEmail e " +
//                        "join fetch e.univName u" +
                        "WHERE e.univName.id = :univId", UnivEmail.class)
                .setParameter("univId", univId)
                .getResultList();
    }

//    public List<GetUnivDomainResponse> findByUniv2(int univId){
//        return em.createQuery(
//                "select new community.mingle.app.src.auth.authModel.GetUnivDomainResponse(e.emailIdx, e.domain)" +
//                        "from UnivEmail e" +
//                        "join e.univName u" +
//                        ""
//        )
//    }

    //List<Team> result = em.createQuery("select t from Team t "
    //	+ "join fetch t.members m where t.name='teamA'", Team.class)
    //	.getResultList();


    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }


    public UnivName findUniv(int id) {
        return em.find(UnivName.class, id);
    }


    public Member findMember(String email) {
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult(); //에러남
    }

    public Member findMemberByEmail(String email) {
        List<Member> m = em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();
        if (m.size() != 0) { //있으면 list 첫번째 element 반환. 중복은 없을테니
            return m.get(0);
        } else { //없으면 null 반환
            return null;
        }
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


//    public List<Member> findByName(String nickname) {
//        return em.createQuery("select m from Member m where m.nickname = :nickname", Member.class)
//                .setParameter("nickname", nickname)
//                .getResultList();
//    }
//
//    public List<Member> findByEmail(String email) {
//        return em.createQuery("select m from Member m where m.email = :email", Member.class)
//                .setParameter("email", email)
//                .getResultList();
//    }


}
