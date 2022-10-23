package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Category;
import lombok.Getter;

@Getter
public class PostCategoryResponse {

    private int categoryId;
    private String categoryName;

    public PostCategoryResponse(Category m) {
        this.categoryId = m.getId();
        this.categoryName = m.getName();
    }
}
