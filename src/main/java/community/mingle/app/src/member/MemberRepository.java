package community.mingle.app.src.member;

import community.mingle.app.src.domain.Member;
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

}
