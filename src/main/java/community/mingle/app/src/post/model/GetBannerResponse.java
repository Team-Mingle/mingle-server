package community.mingle.app.src.post.model;

import community.mingle.app.src.domain.Banner;
import lombok.Getter;

@Getter
public class GetBannerResponse {
    private int id;
    private String url;

    public GetBannerResponse(Banner banner) {
        this.id = banner.getId();
        this.url = banner.getUrl();

    }
}
