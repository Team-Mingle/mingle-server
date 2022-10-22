package community.mingle.app.src.post.model;

import lombok.Getter;

import java.util.List;

@Getter
public class TotalPostListResponse {

    private String boardName;
    private List<TotalPostListDTO> postListDTO;


    public TotalPostListResponse(String boardName, List<TotalPostListDTO> totalPostListDTO) {
        this.boardName = boardName;
        this.postListDTO  = totalPostListDTO;
    }
}
