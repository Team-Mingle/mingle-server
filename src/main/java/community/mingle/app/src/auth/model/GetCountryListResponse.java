package community.mingle.app.src.auth.model;

import community.mingle.app.src.domain.Country;
import lombok.Getter;

@Getter
public class GetCountryListResponse {
    private final int countryId;
    private final String countryName;

    public GetCountryListResponse(
            Country country
    ) {
        this.countryId = country.getId();
        this.countryName = country.getCountryName();
    }
}
