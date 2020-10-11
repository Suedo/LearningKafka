package com.kafka.bootdemo.dto;

import com.kafka.bootdemo.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class BookDto implements Serializable {
    private Integer bookId;

    @NotBlank
    private String bookName;

    @NotBlank
    private String bookAuthor;

    public BookDto(Book that) {
        this.bookId = that.getBookId();
        this.bookName = that.getBookName();
        this.bookAuthor = that.getBookAuthor();
    }
}
