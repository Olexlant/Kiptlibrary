package com.Kipfk.Library.controllers.takenBooks;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.AppUser;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Controller
public class AdminTakenBooksController {
    private final AppBookService appBookService;
    private final AppUserRepository appUserRepository;
    private final AppBookRepository appBookRepository;
    private final TakenBooksRepository takenBooksRepository;
    private final BookOrdersRepository bookOrdersRepository;

    @GetMapping("/admin/takebook")
    public String showUsersToTake(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<AppUserRepository.UserNoPhoto> userPage = appUserRepository.findAllByEnabledIsTrueOrderByLastName(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("users",userPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(userPage));
        return "takebookuser";
    }
    @GetMapping("/admin/takebook/{id}")
    public String showBooksToTake(Model model, @PathVariable Long id, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<AppBookRepository.BookNoFileAndPhoto> bookPage = appBookRepository.findAllByOrderByTitle(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("books",bookPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        model.addAttribute("userid", id);
        return "takebook";
    }

    @PostMapping("/admin/assigningbook/{userid}/{bookid}")
    public String createtakenbook(TakenBooks takenBooks, @PathVariable Long userid, @PathVariable Long bookid, @RequestParam Long takeCount) {
        AppUser user = appUserRepository.findById(userid).orElseThrow();
        AppBook book = appBookRepository.findAllByIdOrderByTitle(bookid);
        boolean uniquetb = takenBooksRepository.findByUserAndBookAndDeletedIsFalse(user, book).isEmpty();
        if (uniquetb){
            takenBooks.setUser(user);
            takenBooks.setBook(book);
            takenBooks.setTakenat(LocalDate.now());
            takenBooks.setCount(takeCount);
            takenBooks.setDeleted(false);
            if (book.getCount()<takeCount){
                return "redirect:/admin/assignedbooks?tomanybooks";
            }else {
                book.setCount(book.getCount()-takeCount);
            }
            List<BookOrders> bookOrder = bookOrdersRepository.findByBookAndUserAndDeletedIsFalse(book, user);
            for(BookOrders i : bookOrder){
                i.setDeleted(true);
            }
            bookOrdersRepository.saveAll(bookOrder);
            takenBooksRepository.save(takenBooks);
            appBookRepository.save(book);
        }else {
            return "redirect:/admin/assignedbooks?alreadyassigned";
        }
        return "redirect:/admin/assignedbooks?success";
    }
    @GetMapping("/admin/assignedbooks")
    public String showAssignedBooks(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<TakenBooks> takenPage = takenBooksRepository.findAllByDeletedIsFalse(PageRequest.of(currentPage - 1, pageSize, Sort.Direction.DESC,"takenat"));
        model.addAttribute("takenbooks", takenPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(takenPage));
        return "assignedbooks";
    }

    @GetMapping("/admin/assignedbooks/history")
    public String showAssignedBooksHistory(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<TakenBooks> takenPage = takenBooksRepository.findAllByDeletedIsTrue(PageRequest.of(currentPage - 1, pageSize, Sort.Direction.DESC,"returnedAt"));
        model.addAttribute("takenbooks", takenPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(takenPage));
        return "assignedBooksHistory";
    }
    @PostMapping("/admin/assignedbooks/history/removeall")
    public String clearAssignedBooksHistory(){
        takenBooksRepository.deleteAllByDeletedIsTrue();
        return "redirect:/admin/assignedbooks/history?deleted";
    }

    @PostMapping("/admin/assignedbooks/{id}/remove")
    public String removeassignedbooks(@PathVariable(value = "id") Long id) {
        TakenBooks tb = takenBooksRepository.findById(id).orElseThrow();
        AppBook appBook = appBookRepository.findAllByIdOrderByTitle(tb.getBook().getId());
        appBook.setCount(appBook.getCount()+tb.getCount());
        appBookRepository.save(appBook);
        tb.setDeleted(true);
        tb.setReturnedAt(LocalDate.now());
        takenBooksRepository.save(tb);
        return "redirect:/admin/assignedbooks?returned";
    }

    @GetMapping("/admin/usertakenadmin/{id}")
    public String showusertaken(@PathVariable(value = "id") Long userid,Model model){
        AppUser user = appUserRepository.findById(userid).orElseThrow();
        List<TakenBooks> takenBooks = takenBooksRepository.findAllByUserAndDeletedIsFalse(user);
        model.addAttribute("user",user);
        model.addAttribute("usertaken", takenBooks);
        return "usertakenadmin";
    }

}
