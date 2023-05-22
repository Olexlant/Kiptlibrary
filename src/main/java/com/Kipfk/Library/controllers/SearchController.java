package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.AppBook;
import com.Kipfk.Library.appbook.AppBookService;
import com.Kipfk.Library.appbook.CategoriesOfBooksRepository;
import com.Kipfk.Library.appuser.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Controller
public class SearchController {

    private final AppUserService appUserService;
    private final AppBookService appBookService;
    private final LikedBooksRepository likedBooksRepository;
    private final CategoriesOfBooksRepository categoriesOfBooksRepository;
    private final GroupsRepository groupsRepository;
    private final TakenBooksService takenBooksService;

    @RequestMapping(path = {"/searchbook"})
    public String searchbook(@AuthenticationPrincipal UserDetails userDetails, Model model, String keyword, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        if (!keyword.equals("")) {
            ArrayList<AppBook> list = (ArrayList<AppBook>) appBookService.getAllByKeyword(keyword);
            Page<AppBook> bookPage = appBookService.searchpagepaginated(PageRequest.of(currentPage - 1, pageSize),list);
            model.addAttribute("books", bookPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        } else {
            return "redirect:/allbooks";
        }
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<LikedBooks> lb = likedBooksRepository.findAllByUser(user);
        ArrayList<AppBook> likedbooks = new ArrayList<>();
        for (LikedBooks b : lb){
            likedbooks.add(b.getBook());
        }
        model.addAttribute("keyword",keyword);
        model.addAttribute("status","search");
        model.addAttribute("bookcategories", categoriesOfBooksRepository.findAll());
        model.addAttribute("likedbooks", likedbooks);
        return "allbooks";
    }
    @RequestMapping(path = {"/searchbookadmin"})
    public String searchbookadmin(Model model, String keyword) {
        if (!keyword.equals("")) {
            List<AppBook> list = appBookService.getAllByKeyword(keyword);
            Page<AppBook> booksPage = new PageImpl<>(list);
            model.addAttribute("books", booksPage);
            model.addAttribute("keyword", keyword);
        } else {
            return "redirect:/admin/allbooksadmin";
        }
        model.addAttribute("keyword", keyword);
        return "allbooksadmin";
    }
    @RequestMapping(path = {"/searchuseradmin"})
    public String searchuser(Model model, String keyword) {
        if (!keyword.equals("")) {
            List<AppUser> users = appUserService.getAllByKeyword(keyword);
            Page<AppUser> userPage = new PageImpl<>(users);
            model.addAttribute("Users", userPage);
            model.addAttribute("keyword", keyword);
        } else {
            return "redirect:/admin/allusers";
        }
        return "allusers";
    }

    @RequestMapping(path = {"/searchbooktakebook/{userid}"})
    public String searchbookintakebook(@PathVariable Long userid, Model model, String keyword) {
        if (!keyword.equals("")) {
            List<AppBook> list = appBookService.getAllByKeyword(keyword);
            Page<AppBook> booksPage = new PageImpl<>(list);
            model.addAttribute("books", booksPage);
            model.addAttribute("keyword", keyword);
        } else {
            return "redirect:/admin/takebook/"+userid;
        }
        model.addAttribute("userid", userid);
        return "takebook";
    }

    @RequestMapping(path = {"/searchusertakebook"})
    public String searchuserintakebook(Model model, String keyword) {
        if (!keyword.equals("")) {
            List<AppUser> users = appUserService.getAllByKeyword(keyword);
            Page<AppUser> usersPage = new PageImpl<>(users);
            model.addAttribute("users", usersPage);
            model.addAttribute("keyword", keyword);
        } else {
            return "redirect:/admin/takebook";
        }
        return "takebookuser";
    }

    @RequestMapping(path = {"/admin/searchbooktoaddtogroup/{groupid}"})
    public String searchAddBookToGroup(@PathVariable Long groupid, Model model, String keyword) {
        if (!keyword.equals("")) {
            List<AppBook> list = appBookService.getAllByKeyword(keyword);
            Page<AppBook> booksPage = new PageImpl<>(list);
            model.addAttribute("books", booksPage);
            model.addAttribute("keyword", keyword);
        } else {
            return "redirect:/admin/addbooktogroup/"+groupid;
        }
        model.addAttribute("group", groupsRepository.findAllById(groupid));
        return "addBooksToGroup";
    }
    @RequestMapping(path = {"/admin/searchassignedbooks"})
    public String searchAssugendBooks(Model model, String keyword) {
        if (!keyword.equals("")) {
            List<TakenBooks> takenBooks = takenBooksService.getAllByKeyword(keyword);
            takenBooks.removeIf(TakenBooks::isDeleted);
            Page<TakenBooks> takenBooksPage = new PageImpl<>(takenBooks);
            model.addAttribute("takenbooks", takenBooksPage);
            model.addAttribute("keyword", keyword);
        } else {
            return "redirect:/admin/assignedbooks";
        }
        return "assignedbooks";
    }

    @RequestMapping(path = {"/admin/searchassignedbookshistory"})
    public String searchAssignedBooksHistory(Model model, String keyword) {
        if (keyword != null) {
            List<TakenBooks> takenBooks = takenBooksService.getAllByKeyword(keyword);
            takenBooks.removeIf(TakenBooks::isDeleted);
            Page<TakenBooks> takenBooksPage = new PageImpl<>(takenBooks);
            model.addAttribute("takenbooks", takenBooksPage);
            model.addAttribute("keyword", keyword);
        } else {
            return "redirect:/admin/assignedbooks/history";
        }
        return "assignedBooksHistory";
    }
}
