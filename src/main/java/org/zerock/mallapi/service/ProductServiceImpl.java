package org.zerock.mallapi.service;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.domain.ProductImage;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.ProductDTO;
import org.zerock.mallapi.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() -1, pageRequestDTO.getSize(),
                Sort.by("pno").descending());

        Page<Object[]> result = productRepository.selectList(pageable);

        //Object => 0 product 1 ProductImage

        List<ProductDTO> dtoList = result.get().map(arr -> {
            ProductDTO productDTO = null;

            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];

            productDTO = ProductDTO.builder()
                    .pno(product.getPno())
                    .pname(product.getPname())
                    .pdesc(product.getPdesc())
                    .price(product.getPrice())
                    .build();
            String imageStr = productImage.getFileName();
            productDTO.setUploadFileNames(List.of(imageStr));

            return productDTO;
        }).collect(Collectors.toList());

        long total = result.getTotalElements();
        return PageResponseDTO.<ProductDTO>withAll()
                .dtoList(dtoList)
                .total(total)
                .pageRequestDTO(pageRequestDTO)
                .build();

        //querydsl 사용방법

//        Page<Tuple> result = productRepository.searchList(pageRequestDTO);
//        List<ProductDTO> dtoList = new ArrayList<>();
//
//        for (Tuple tuple : result) {
//            Product product = tuple.get(0, Product.class);
//            ProductImage productImage = tuple.get(1, ProductImage.class);
//        }
    }

    private ProductDTO entityToDTO(Product product) {
        ProductDTO productDTO = ProductDTO.builder()
                .pno(product.getPno())
                .pname(product.getPname())
                .pdesc(product.getPdesc())
                .price(product.getPrice())
                .delFlag(product.isDelFlag())
                .build();

        List<ProductImage> imageList = product.getImageList();

        if (imageList == null || imageList.isEmpty()) {
            return productDTO;
        }

        List<String> fileNameList = imageList.stream().map(productImage ->
                productImage.getFileName()
        ).toList();

        productDTO.setUploadFileNames(fileNameList);

        return productDTO;
    }

    private Product dtoToEntity(ProductDTO productDTO) {

        Product product= Product.builder()
                .pno(productDTO.getPno())
                .pname(productDTO.getPname())
                .pdesc(productDTO.getPdesc())
                .price(productDTO.getPrice())
                .delFlag(productDTO.isDelFlag())
                .build();

        List<String> uploadFileNames = productDTO.getUploadFileNames();

        if (uploadFileNames == null || uploadFileNames.size() == 0) {
            return product;
        }

        uploadFileNames.forEach(fileNames -> {
            product.addImageString(fileNames);
        });

        return product;
    }

    @Override
    public Long register(ProductDTO productDTO) {

        Product product = dtoToEntity(productDTO);

        log.info("==============================");
        log.info(product);
        log.info(product.getImageList());

        Long pno = productRepository.save(product).getPno();

        return pno;
    }

    @Override
    public ProductDTO get(Long pno) {

        Product product = productRepository.findById(pno).orElseThrow();

        return entityToDTO(product);
    }

    @Override
    public void modify(ProductDTO productDTO) {

        //조회
        Product product = productRepository.findById(productDTO.getPno()).orElseThrow();

        //변경 내용 반영
        product.changePrice(productDTO.getPrice());
        product.changeName(productDTO.getPname());
        product.changeDesc(productDTO.getPdesc());
        product.changeDel(productDTO.isDelFlag());

        //이미지 처리

        List<String> uploadFileNames = productDTO.getUploadFileNames();

        product.clearList();

        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(uploadName -> {
                product.addImageString(uploadName);
            });
        }

        //저장
        productRepository.save(product);

    }

    @Override
    public void remove(Long pno) {

        productRepository.deleteById(pno);
    }
}
