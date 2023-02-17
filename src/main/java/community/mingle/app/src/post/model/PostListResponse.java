package community.mingle.app.src.post.model;

import java.util.List;

public class PostListResponse {
    private String boardName;
    private List<PostListDTO> postListDTO;


    public PostListResponse(String univName, List<PostListDTO> univPostListDTO) {
        this.boardName = univName;
        this.postListDTO = univPostListDTO;
    }

    public PostListResponse(List<PostListDTO> univPostListDTO) {
        this.boardName = null;
        this.postListDTO = univPostListDTO;
    }

}
