package community.mingle.app.src.post.model;

import lombok.Getter;

import java.util.List;

@Getter
public class UnivPostListResponse {

    private String boardName;
    private List<UnivPostListDTO> postListDTO;


    public UnivPostListResponse(String univName, List<UnivPostListDTO> univPostListDTO) {
        this.boardName = univName;
        this.postListDTO = univPostListDTO;
    }
}
