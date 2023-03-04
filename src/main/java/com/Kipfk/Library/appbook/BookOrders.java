package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.AppUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class BookOrders {

    @SequenceGenerator(
            name = "book_orders_sequence",
            sequenceName = "book_orders_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "book_orders_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private AppBook book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    private LocalDate createdat;
    private boolean deleted;

}
