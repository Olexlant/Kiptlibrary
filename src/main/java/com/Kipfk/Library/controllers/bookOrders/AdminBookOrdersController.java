package com.Kipfk.Library.controllers.bookOrders;

import com.Kipfk.Library.appbook.AppBookService;
import com.Kipfk.Library.appbook.BookOrders;
import com.Kipfk.Library.appbook.BookOrdersRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Controller
public class AdminBookOrdersController {

    private final AppBookService appBookService;
    private final BookOrdersRepository bookOrdersRepository;
    //BOOK ORDERS
    @GetMapping("/admin/bookorders")
    public String showBookOrders(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<BookOrders> bookOrders = bookOrdersRepository.findAllByDeletedIsFalse(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("bookOrders", bookOrders);
        model.addAttribute("body", appBookService.bodyArrayForPages(bookOrders));
        return "orders";
    }

    @PostMapping("/admin/bookorders/{bookorderid}/delete")
    public String deleteBookOrder (@PathVariable Long bookorderid){
        bookOrdersRepository.deleteById(bookorderid);
        return "redirect:/admin/bookorders?deletesuccess";
    }
}
