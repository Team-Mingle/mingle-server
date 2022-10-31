package community.mingle.app.src.post.model;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchTotalPostResponse {

    private String boardName;
    private List<SearchTotalPostDTO> postListDTO;

    public SearchTotalPostResponse(String boardName, List<SearchTotalPostDTO> postListDTO) {
        this.boardName = boardName;
        this.postListDTO = postListDTO;
    }
}
