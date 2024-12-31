package org.zerock.mallapi.repository.search;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.ProductDTO;

public interface ProductSearch {

    Page<Tuple> searchList(PageRequestDTO pageRequestDTO);
}
