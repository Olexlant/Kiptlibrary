package com.Kipfk.Library.controllers.bookOrders;

import com.Kipfk.Library.appbook.BookOrdersRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookOrdersRestController {
    private final BookOrdersRepository bookOrdersRepository;

    public BookOrdersRestController(BookOrdersRepository bookOrdersRepository) {
        this.bookOrdersRepository = bookOrdersRepository;
    }

//GET BOOK ORDERS COUNT
    @GetMapping("/admin/bookorders/getcount")
    public Integer getBookOrdersCount() {
        return bookOrdersRepository.countAllByDeletedIsFalse();
    }
}
