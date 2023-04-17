package community.mingle.app.src.item.model;

import community.mingle.app.src.domain.Item;
import lombok.Getter;

import java.util.List;

@Getter
public class ItemListResponse {

    private String name;
    private List<ItemListDTO> itemListDTO;

    public ItemListResponse(List<ItemListDTO> itemListDTO, String boardName) {
        this.name = boardName;
        this.itemListDTO = itemListDTO;
    }

}
