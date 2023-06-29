package com.Kipfk.Library.controllers.bookOrders;

import com.Kipfk.Library.appbook.AppBook;
import com.Kipfk.Library.appbook.AppBookRepository;
import com.Kipfk.Library.appbook.BookOrders;
import com.Kipfk.Library.appbook.BookOrdersRepository;
import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.AppUserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Controller
public class BookOrdersController {
    private final AppUserService appUserService;
    private final AppBookRepository appBookRepository;
    private final BookOrdersRepository bookOrdersRepository;


    //BOOK ORDERS
    @PostMapping("/orderbook/{bookid}")
    public String addBookOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(value = "bookid") long bookid){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        BookOrders bookOrder = new BookOrders();
        AppBook book = appBookRepository.findAllByIdOrderByTitle(bookid);
        if (bookOrdersRepository.findByBookAndUserAndDeletedIsFalse(book, user).isEmpty()){
            bookOrder.setBook(book);
            bookOrder.setUser(user);
            bookOrder.setCreatedat(LocalDate.now());
            bookOrder.setDeleted(false);
            bookOrdersRepository.save(bookOrder);
            return "redirect:/allbooks/"+bookid+"?ordered";
        }else{
            return "redirect:/allbooks/"+bookid+"?orderedlater";
        }
    }
}
