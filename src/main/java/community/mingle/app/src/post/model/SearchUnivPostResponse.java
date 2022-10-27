package community.mingle.app.src.post.model;

import java.util.List;

public class SearchUnivPostResponse {

    private String boardName;
    private List<SearchUnivPostDTO> postListDTO;

    public SearchUnivPostResponse(String boardName, List<SearchUnivPostDTO> postListDTO) {
        this.boardName = boardName;
        this.postListDTO = postListDTO;
    }
}
