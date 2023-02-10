package community.mingle.app.src.home.model;

import community.mingle.app.src.domain.Banner;
import lombok.Getter;

@Getter
public class BannerResponse {
    private int id;
    private String url;
    private String link;


    public BannerResponse(Banner banner) {
        this.id = banner.getId();
        this.url = banner.getUrl();
        this.link = banner.getLink();

    }
}
