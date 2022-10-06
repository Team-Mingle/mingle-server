package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Category;
import community.mingle.app.src.domain.UnivName;
import lombok.Getter;

import java.util.List;

@Getter
public class GetPostCategoryResponse {

    private int categoryId;
    private String categoryName;

    public GetPostCategoryResponse(Category m) {
        this.categoryId = m.getId();
        this.categoryName = m.getName();
    }
}
