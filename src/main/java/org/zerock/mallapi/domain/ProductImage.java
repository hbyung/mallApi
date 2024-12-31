package org.zerock.mallapi.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.*;

@Embeddable
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

    private String fileName;

    private int ord;

    public void setOrd(int ord) {
        this.ord = ord;
    }
}
