package org.zerock.mallapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.ProductDTO;
import org.zerock.mallapi.service.ProductService;
import org.zerock.mallapi.util.CustomFileUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CustomFileUtil fileUtil;
    private final ProductService productService;

//    @PostMapping("/")
//    public Map<String, String> register(ProductDTO productDTO) {
//
//        log.info("register" + productDTO);
//        List<MultipartFile> files = productDTO.getFiles();
//        List<String> uploadFileNames = fileUtil.saveFiles(files);
//        productDTO.setUploadFileNames(uploadFileNames);
//        log.info(uploadFileNames);
//
//        return Map.of("RESULT", "SUCCESS");
//    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable("fileName") String fileName) {

        return fileUtil.getFile(fileName);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<ProductDTO> responseDTO = productService.getList(pageRequestDTO);

        return responseDTO;
    }

    @PostMapping("/")
    public  Map<String, Long> ProductRegister(ProductDTO productDTO) {

        //파일 업로드
        List<MultipartFile> files = productDTO.getFiles();

        List<String> uploadFileNames = fileUtil.saveFiles(files);
        productDTO.setUploadFileNames(uploadFileNames);

        log.info(uploadFileNames);

        Long pno = productService.register(productDTO);

        return Map.of("result", pno);

    }

    @GetMapping("/{pno}")
    public ProductDTO read(@PathVariable("pno") Long pno) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return productService.get(pno);
    }

    @PutMapping("/{pno}")
    public Map<String,String> modify(@PathVariable Long pno, ProductDTO productDTO) {

        productDTO.setPno(pno);

        //old Product
        ProductDTO oldProduct = productService.get(pno);

        //파일 업로드
        List<MultipartFile> files = productDTO.getFiles();
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);

        //이전 파일
        List<String> uploadFileNames = productDTO.getUploadFileNames();

        //신규 파일 저장
        if (currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
            uploadFileNames.addAll(currentUploadFileNames);
        }

        productService.modify(productDTO);

        List<String> oldFileNames = oldProduct.getUploadFileNames();

        //기존 파일 저장 및 삭제
        if (oldFileNames != null && oldFileNames.size() > 0) {

            List<String> removeFiles = oldFileNames.stream().filter(fileName -> !uploadFileNames.contains(fileName)).collect(Collectors.toList());

            fileUtil.deleteFiles(removeFiles);

        }

        return Map.of("RESULT","Success");

    }

    @DeleteMapping("/{pno}")
    public Map<String,String> remove(@PathVariable("pno")Long pno) {

        List<String> oldFIleNames = productService.get(pno).getUploadFileNames();

        productService.remove(pno);

        fileUtil.deleteFiles(oldFIleNames);

        return Map.of("RESULT","SUCCEES");
    }


}
