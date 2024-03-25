package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.AppBookRepository;
import com.Kipfk.Library.appbook.AppBookService;
import com.Kipfk.Library.appbook.BookOrdersRepository;
import com.Kipfk.Library.appuser.AppUserRepository;
import com.Kipfk.Library.appuser.TakenBooks;
import com.Kipfk.Library.appuser.TakenBooksRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;
@Getter
@Setter
@AllArgsConstructor
@Controller
public class AdminPanelController {

    private final AppBookService appBookService;
    private final AppUserRepository appUserRepository;
    private final AppBookRepository appBookRepository;
    private final TakenBooksRepository takenBooksRepository;
    private final BookOrdersRepository bookOrdersRepository;

    @GetMapping("/admin")
    public String showAdminHome(Model model){
        int usercount = appUserRepository.countAllByEnabledIsTrue();
        model.addAttribute("usercount", usercount);
        int bookOrdersCount = bookOrdersRepository.countAllByDeletedIsFalse();
        model.addAttribute("bookOrdersCount", bookOrdersCount);
        int bookcount = appBookRepository.countAllBy();
        model.addAttribute("bookcount",bookcount);
        int takencount = takenBooksRepository.countAllByDeletedIsFalse();
        model.addAttribute("takencount",takencount);
        return "admin";
    }

//DEBTOR USERS

    @GetMapping("/admin/debtorusers")
    public String showDebtorUsers(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<TakenBooks> takenPage = takenBooksRepository.findAllByDeletedIsFalseAndReturnExpiresAtIsBeforeAndBook_Electronic(PageRequest.of(currentPage - 1, pageSize, Sort.Direction.DESC,"takenat"), LocalDate.now(), false);
        model.addAttribute("takenbooks", takenPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(takenPage));
        return "debtor-users";
    }
}
