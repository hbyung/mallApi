package org.zerock.mallapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.zerock.mallapi.domain.Todo;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.TodoDTO;
import org.zerock.mallapi.repository.TodoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public TodoDTO get(Long tno) {
        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();

        //default로 선언하고 걍 집어넣기만 하면되네 간편
        return entityToDTO(todo);
    }

    @Override
    public Long register(TodoDTO dto) {

        Todo todo = dtoToEntity(dto);
        Todo result = todoRepository.save(todo);

        return result.getTno();
    }

    @Override
    public void modify(TodoDTO dto) {

        Todo todo = todoRepository.findById(dto.getTno()).orElseThrow();
        todo.ChangeTitle(dto.getTitle());
        todo.ChangeContent(dto.getContent());
        todo.ChangeComplete(dto.isComplete());
        todo.ChangeDueDate(dto.getDueDate());

        todoRepository.save(todo);

    }

    @Override
    public void remove(Long dto) {

        todoRepository.deleteById(dto);

    }

    @Override
    public PageResponseDTO<TodoDTO> getList(PageRequestDTO pageRequestDTO) {

        //JPA
        Page<Todo> result = todoRepository.search1(pageRequestDTO);

        List<TodoDTO> dtoList = result.get().map(todo -> entityToDTO(todo)).collect(Collectors.toList());

        PageResponseDTO<TodoDTO> responseDTO =
                PageResponseDTO.<TodoDTO>withAll()
                        .dtoList(dtoList)
                        .pageRequestDTO(pageRequestDTO)
                        .total(result.getTotalElements())
                        .build();

        return responseDTO;
    }
}
