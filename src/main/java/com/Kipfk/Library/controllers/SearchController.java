package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.registration.RegistrationService;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import org.springframework.data.domain.Page;
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

@Controller
public class SearchController {

    private final AppUserService appUserService;
    private final AppBookService appBookService;
    private final AppUserRepository appUserRepository;
    private final AppBookRepository appBookRepository;
    private final LikedBooksRepository likedBooksRepository;
    private final CategoriesOfBooksRepository categoriesOfBooksRepository;
    private final GroupsRepository groupsRepository;
    private final TakenBooksRepository takenBooksRepository;
    private final TakenBooksService takenBooksService;


    public SearchController(RegistrationService registrationService, ConfirmationTokenRepository confirmationTokenRepository, AppUserService appUserService, AppBookService appBookService, AppUserRepository appUserRepository, AppBookRepository appBookRepository, TakenBooksRepository takenBooksRepository, LikedBooksRepository likedBooksRepository, AppUserRepository userRepo, BookCategoryRepository bookCategoryRepository, CategoriesOfBooksRepository categoriesOfBooksRepository, CategoriesOfBooksRepository categoriesOfBooksRepository1, GroupsRepository groupsRepository, TakenBooksRepository takenBooksRepository1, TakenBooksService takenBooksService) {
        this.appUserService = appUserService;
        this.appBookService = appBookService;
        this.appUserRepository = appUserRepository;
        this.appBookRepository = appBookRepository;
        this.likedBooksRepository = likedBooksRepository;
        this.categoriesOfBooksRepository = categoriesOfBooksRepository1;
        this.groupsRepository = groupsRepository;
        this.takenBooksRepository = takenBooksRepository1;
        this.takenBooksService = takenBooksService;
    }
    //SEARCH
    @RequestMapping(path = {"/searchbook"})
    public String searchbook(@AuthenticationPrincipal UserDetails userDetails, Model model, String keyword, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        if (keyword != null) {
            ArrayList<AppBook> list = (ArrayList<AppBook>) appBookService.getAllByKeyword(keyword);
            Page<AppBook> bookPage = appBookService.searchpagepaginated(PageRequest.of(currentPage - 1, pageSize),list);
            model.addAttribute("books", bookPage);
            int[] body;
            if (bookPage.getTotalPages() > 7) {
                int totalPages = bookPage.getTotalPages();
                int pageNumber = bookPage.getNumber()+1;
                int[] head = (pageNumber > 4) ? new int[]{1, -1} : new int[]{1,2,3};
                int[] bodyBefore = (pageNumber > 4 && pageNumber < totalPages - 1) ? new int[]{pageNumber-2, pageNumber-1} : new int[]{};
                int[] bodyCenter = (pageNumber > 3 && pageNumber < totalPages - 2) ? new int[]{pageNumber} : new int[]{};
                int[] bodyAfter = (pageNumber > 2 && pageNumber < totalPages - 3) ? new int[]{pageNumber+1, pageNumber+2} : new int[]{};
                int[] tail = (pageNumber < totalPages - 3) ? new int[]{-1, totalPages} : new int[] {totalPages-2, totalPages-1, totalPages};
                body = MainController.merge(head, bodyBefore, bodyCenter, bodyAfter, tail);
            } else {
                body = new int[bookPage.getTotalPages()];
                for (int i = 0; i < bookPage.getTotalPages(); i++) {
                    body[i] = 1+i;
                }
            }
            model.addAttribute("body", body);
        } else {
            List<AppBook> list = appBookRepository.findAll();
            Page<AppBook> bookPage = appBookService.searchpagepaginated(PageRequest.of(currentPage - 1, pageSize),list);
            model.addAttribute("books", bookPage);
            int[] body;
            if (bookPage.getTotalPages() > 7) {
                int totalPages = bookPage.getTotalPages();
                int pageNumber = bookPage.getNumber()+1;
                int[] head = (pageNumber > 4) ? new int[]{1, -1} : new int[]{1,2,3};
                int[] bodyBefore = (pageNumber > 4 && pageNumber < totalPages - 1) ? new int[]{pageNumber-2, pageNumber-1} : new int[]{};
                int[] bodyCenter = (pageNumber > 3 && pageNumber < totalPages - 2) ? new int[]{pageNumber} : new int[]{};
                int[] bodyAfter = (pageNumber > 2 && pageNumber < totalPages - 3) ? new int[]{pageNumber+1, pageNumber+2} : new int[]{};
                int[] tail = (pageNumber < totalPages - 3) ? new int[]{-1, totalPages} : new int[] {totalPages-2, totalPages-1, totalPages};
                body = MainController.merge(head, bodyBefore, bodyCenter, bodyAfter, tail);
            } else {
                body = new int[bookPage.getTotalPages()];
                for (int i = 0; i < bookPage.getTotalPages(); i++) {
                    body[i] = 1+i;
                }
            }
            model.addAttribute("body", body);
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
        if (keyword != null) {
            List<AppBook> list = appBookService.getAllByKeyword(keyword);
            model.addAttribute("books", list);
        } else {
            Iterable<AppBook> books = appBookRepository.findAll();
            model.addAttribute("books", books);
        }
        return "allbooksadmin";
    }
    @RequestMapping(path = {"/searchuseradmin"})
    public String searchuser(Model model, String keyword) {
        if (keyword != null) {
            List<AppUser> user = appUserService.getByKeyword(keyword);
            model.addAttribute("Users", user);
        } else {
            Iterable<AppUser> users = appUserRepository.findAll();
            model.addAttribute("Users", users);
        }
        return "allusers";
    }

    @RequestMapping(path = {"/searchbooktakebook/{userid}"})
    public String searchbookintakebook(@PathVariable Long userid, Model model, String keyword) {
        if (keyword != null) {
            List<AppBook> list = appBookService.getAllByKeyword(keyword);
            model.addAttribute("books", list);
        } else {
            Iterable<AppBook> books = appBookRepository.findAll();
            model.addAttribute("books", books);
        }
        model.addAttribute("userid", userid);
        return "takebook";
    }

    @RequestMapping(path = {"/searchusertakebook"})
    public String searchuserintakebook(Model model, String keyword) {
        if (keyword != null) {
            List<AppUser> user = appUserService.getByKeyword(keyword);
            model.addAttribute("users", user);
        } else {
            Iterable<AppUser> users = appUserRepository.findAll();
            model.addAttribute("users", users);
        }
        return "takebookuser";
    }

    @RequestMapping(path = {"/admin/searchbooktoaddtogroup/{groupid}"})
    public String searchAddBookToGroup(@PathVariable Long groupid, Model model, String keyword) {
        if (keyword != null) {
            List<AppBook> list = appBookService.getAllByKeyword(keyword);
            model.addAttribute("books", list);
        } else {
            Iterable<AppBook> books = appBookRepository.findAll();
            model.addAttribute("books", books);
        }
        model.addAttribute("group", groupsRepository.findAllById(groupid));
        return "addBooksToGroup";
    }
    @RequestMapping(path = {"/admin/searchassignedbooks"})
    public String searchAssugendBooks(Model model, String keyword) {
        if (keyword != null) {
            List<TakenBooks> takenBooks = takenBooksService.getAllByKeyword(keyword);
            model.addAttribute("takenbooks", takenBooks);
        } else {
            return "redirect:/admin/assignedbooks";
        }
        return "assignedbooks";
    }
}
