package org.zerock.mallapi.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class CartItemListDTO {

    public Long cino;

    public int qty;

    public String pname;

    private int price;

    private Long pno;

    private String imageFile;


    public CartItemListDTO(Long cino, int qty, String pname, int price,Long pno ,String imageFile) {
        this.cino = cino;
        this.qty = qty;
        this.pname = pname;
        this.price = price;
        this.pno = pno;
        this.imageFile = imageFile;
    }
}
