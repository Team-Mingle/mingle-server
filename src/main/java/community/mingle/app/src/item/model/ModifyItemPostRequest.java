package community.mingle.app.src.item.model;

import lombok.Getter;

@Getter
public class ModifyItemPostRequest {
    private String title;
    private String content;
    private Long price;
    private String location;
    private String chatUrl;
}
