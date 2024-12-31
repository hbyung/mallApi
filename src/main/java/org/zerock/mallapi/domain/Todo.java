package org.zerock.mallapi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_todo")
public class Todo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tno;

    @Column(length = 500, nullable = false)
    private String title;
    private String content;

    private boolean complete;

    private LocalDate dueDate;


    public void ChangeTitle(String title) {
        this.title = title;
    }

    public void ChangeContent(String content) {
        this.content = content;
    }

    public void ChangeComplete(boolean complete) {
        this.complete = complete;
    }

    public void ChangeDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
