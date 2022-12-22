package community.mingle.app.src.member.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeleteMemberRequest {

    private String email;
    private String pwd;
}
