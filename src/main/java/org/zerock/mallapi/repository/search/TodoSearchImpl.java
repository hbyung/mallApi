package org.zerock.mallapi.repository.search;

import com.querydsl.jpa.JPQLQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.mallapi.domain.QTodo;
import org.zerock.mallapi.domain.Todo;
import org.zerock.mallapi.dto.PageRequestDTO;

import java.util.List;


@Slf4j
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {

    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<Todo> search1(PageRequestDTO pageRequestDTO) {

        log.info("search1..............");

        QTodo todo = QTodo.todo;

        JPQLQuery<Todo> query = from(todo);

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() -1,pageRequestDTO.getSize(), Sort.by("tno").descending());

        this.getQuerydsl().applyPagination(pageable, query);

        List<Todo> list = query.fetch();

        long total = query.fetchCount();


        return new PageImpl<>(list, pageable, total);
    }
}
