package community.mingle.app.src.post.model;

import lombok.Getter;

import java.util.List;

@Getter
public class BestUnivPostListResponse {

    private String boardName;
    private List<BestUnivPostDTO> postListDTO;

    public BestUnivPostListResponse(String boardName, List<BestUnivPostDTO> postListDTO) {
        this.boardName = boardName;
        this.postListDTO = postListDTO;
    }
}
