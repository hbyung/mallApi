package org.zerock.mallapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "tbl_product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageList")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    private String pname;

    private int price;
    private String pdesc;
    private boolean delFlag;

    // elementCollection ? 뭐임이게 = 한번에 변경되는 이런 파일,
    // 이미지 같은건 라이프 사이클도 다르기 때문에 이게 맞다고 보는입장
    @ElementCollection
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    public void changePrice(int price) {
        this.price= price;
    }
    public void changeDesc(String desc){
        this.pdesc = desc;
    }
    public void changeName(String name){
        this.pname = name;
    }

    public void changeDel(boolean delFlag) {
        this.delFlag = delFlag;
    }

    public void addImage(ProductImage image) {

        image.setOrd(imageList.size());
        imageList.add(image);

    }

    public void addImageString(String fileName) {

        ProductImage productImage = ProductImage.builder()
                .fileName(fileName)
                .build();
        addImage(productImage);
    }

    public void clearList() {

        this.imageList.clear();
    }




}
