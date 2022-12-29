package community.mingle.app.src.post.model;

import lombok.Data;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Data
public class UpdatePostRequest {
    private String title;
//    @NotNull
    private String content;
}
