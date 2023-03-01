package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.BookOrdersRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BooksRestController {
    private final BookOrdersRepository bookOrdersRepository;

    public BooksRestController(BookOrdersRepository bookOrdersRepository) {
        this.bookOrdersRepository = bookOrdersRepository;
    }

//GET BOOK ORDERS COUNT
    @GetMapping("/admin/bookorders/getcount")
    public Integer getBookOrdersCount() {
        return bookOrdersRepository.countAllByDeletedIsFalse();
    }
}
