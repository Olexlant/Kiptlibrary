package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.Groups;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class BooksByGroups {

    @SequenceGenerator(
            name = "books_by_group_sequence",
            sequenceName = "books_by_group_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "books_by_group_sequence"
    )
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "book_id")
    private AppBook book;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups groups;
}
