package org.zerock.mallapi.repository;

import jakarta.persistence.PreUpdate;
import lombok.extern.log4j.Log4j2;
import org.hibernate.event.spi.PreUpdateEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.dto.PageRequestDTO;

import java.util.Arrays;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;


    @Test
    public void testInsert() {

        for (int i = 0; i < 10; i++) {

            Product product  = Product.builder().pname("test"+i ).pdesc("test desc"+i).price(1000).build();

            product.addImageString(UUID.randomUUID()+"_"+"IMAGE.jpg");

            product.addImageString(UUID.randomUUID()+"_"+"IMAGE4.jpg");

            productRepository.save(product);
        }

    }

    @Commit
    @Transactional
    @Test
    public void testDelete() {

        Long pno = 1l;

        productRepository.updateToDelete(1l,false);
    }

    @Test
    public void testUpdate() {

        Product product = productRepository.selectOne(1l).get();

        product.changePrice(3000);

        product.clearList();

        product.addImageString(UUID.randomUUID()+"_"+"PIMAGE1.jpg");
        product.addImageString(UUID.randomUUID()+"_"+"PIMAGE2.jpg");
        product.addImageString(UUID.randomUUID()+"_"+"PIMAGE3.jpg");

        productRepository.save(product);
    }

    @Test
    public void  testList() {

        Pageable pageable = PageRequest.of(0,10, Sort.by("pno").descending());

        Page<Object[]> result = productRepository.selectList(pageable);

        result.getContent().forEach(arr -> log.info(Arrays.toString(arr)));
    }

    @Test
    public void testSearch() {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();

        productRepository.searchList(pageRequestDTO);
    }






}