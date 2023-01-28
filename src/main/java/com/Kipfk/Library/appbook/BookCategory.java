package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.AppUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class BookCategory {

    @SequenceGenerator(
            name = "book_category_sequence",
            sequenceName = "book_category_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "book_category_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private AppBook book;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoriesOfBooks category;
}
