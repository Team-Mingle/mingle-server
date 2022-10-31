package community.mingle.app.src.post.model;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchUnivPostResponse {

    private String boardName;
    private List<SearchUnivPostDTO> postListDTO;

    public SearchUnivPostResponse(String boardName, List<SearchUnivPostDTO> postListDTO) {
        this.boardName = boardName;
        this.postListDTO = postListDTO;
    }
}
