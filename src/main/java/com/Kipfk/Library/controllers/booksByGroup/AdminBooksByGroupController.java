package com.Kipfk.Library.controllers.booksByGroup;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.Groups;
import com.Kipfk.Library.appuser.GroupsRepository;
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

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Controller
public class AdminBooksByGroupController {
    private final AppBookService appBookService;
    private final AppBookRepository appBookRepository;
    private final GroupsRepository groupsRepository;
    private final BooksByGroupsRepository booksByGroupsRepository;

    //BOOKS BY GROUP
    @GetMapping("/admin/booksbygroup/{groupid}")
    public String showBooksByGroup(Model model, @PathVariable Long groupid) {
        Groups group = groupsRepository.findAllById(groupid);
        List<BooksByGroups> users = booksByGroupsRepository.findAllByGroups_Id(groupid);
        model.addAttribute("books", users);
        model.addAttribute("group", group);
        return "adminBooksByGroup";
    }

    @GetMapping("/admin/addbooktogroup/{groupid}")
    public String addBookToGroup(Model model, @PathVariable Long groupid, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        Groups group = groupsRepository.findAllById(groupid);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<AppBookRepository.BookNoFileAndPhoto> bookPage = appBookRepository.findAllBy(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("books",bookPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        model.addAttribute("group", group);
        return "addBooksToGroup";
    }

    @PostMapping("/admin/booksbygroup/{groupid}/{bookid}")
    public String addBookToGroup( @PathVariable Long groupid,@PathVariable Long bookid) {
        Groups group = groupsRepository.findAllById(groupid);
        AppBook book = appBookRepository.findAllById(bookid);
        boolean ispresent = booksByGroupsRepository.existsByGroups_IdAndBook_Id(groupid, bookid);
        if (!ispresent){
            BooksByGroups booksByGroups = new BooksByGroups();
            booksByGroups.setGroups(group);
            booksByGroups.setBook(book);
            booksByGroupsRepository.save(booksByGroups);
            return  "redirect:/admin/booksbygroup/"+groupid+"?bookadded";
        }else {
            return  "redirect:/admin/booksbygroup/"+groupid+"?alreadyadded";
        }
    }

    @PostMapping("/admin/booksbygroup/{groupid}/{bookid}/delete")
    public String deleteBookFromGroup(@PathVariable Long groupid,@PathVariable Long bookid) {
        List<BooksByGroups> booksByGroups = booksByGroupsRepository.findByGroups_IdAndBook_Id(groupid, bookid);
        booksByGroupsRepository.deleteAll(booksByGroups);
        return "redirect:/admin/booksbygroup/"+groupid+"?bookdeleted";
    }
}
