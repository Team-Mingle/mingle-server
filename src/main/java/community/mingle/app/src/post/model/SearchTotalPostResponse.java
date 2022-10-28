package community.mingle.app.src.post.model;

import java.util.List;

public class SearchTotalPostResponse {

    private String boardName;
    private List<SearchTotalPostDTO> postListDTO;

    public SearchTotalPostResponse(String boardName, List<SearchTotalPostDTO> postListDTO) {
        this.boardName = boardName;
        this.postListDTO = postListDTO;
    }
}
