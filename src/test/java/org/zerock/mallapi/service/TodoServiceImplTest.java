package org.zerock.mallapi.service;

import jakarta.persistence.PreUpdate;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.TodoDTO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class TodoServiceImplTest {

    @Autowired
    TodoService todoService;

    @Test
    public void testGet() {
        Long tno = 1l;

        log.info(todoService.get(tno));
    }

    @Test
    public void testRegister() {

        TodoDTO todoDTO = TodoDTO.builder().
                title("title..")
                .content("content")
                .dueDate(LocalDate.of(2023, 12, 31))
                .build();

        log.info(todoService.register(todoDTO));
    }

    @Test
    public void testGetList() {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
        log.info(todoService.getList(pageRequestDTO));
    }
}