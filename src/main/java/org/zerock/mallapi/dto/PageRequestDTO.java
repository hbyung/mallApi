package org.zerock.mallapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;

import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page =1;

    @Builder.Default
    private int size =10;


}
