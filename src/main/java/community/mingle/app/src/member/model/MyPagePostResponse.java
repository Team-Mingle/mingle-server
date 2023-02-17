package community.mingle.app.src.member.model;

import lombok.Getter;

import java.util.List;

@Getter
public class MyPagePostResponse {
    private String boardName;
    private List<MyPagePostDTO> postListDTO;

    public MyPagePostResponse(String boardName, List<MyPagePostDTO> postListDTO) {
        this.boardName = boardName;
        this.postListDTO  = postListDTO;
    }
}
