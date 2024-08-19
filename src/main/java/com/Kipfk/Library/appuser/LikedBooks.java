package com.Kipfk.Library.appuser;

import com.Kipfk.Library.appbook.AppBook;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class LikedBooks {

    @Getter
    @Setter
    @SequenceGenerator(
            name = "liked_books_sequence",
            sequenceName = "liked_books_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "liked_books_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private AppBook book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;


    private LocalDate addedat;


}