package community.mingle.app.src.post.model;

import lombok.Getter;

import java.util.List;

@Getter
public class UnivPostListResponse {

    private String univName;
    private List<UnivPostListDTO> univPostListDTO;


    public UnivPostListResponse(String univName, List<UnivPostListDTO> univPostListDTOList) {
        this.univName = univName;
        this.univPostListDTO = univPostListDTOList;
    }
}
