package com.Kipfk.Library.appbook;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class CategoriesOfBooks {

    @SequenceGenerator(
            name = "categories_of_books_sequence",
            sequenceName = "categories_of_books_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "categories_of_books_sequence"
    )
    private Long id;

    private String name;


}
