package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.registration.RegistrationService;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    private final AppUserService appUserService;
    private final AppBookService appBookService;
    private final AppUserRepository appUserRepository;
    private final AppBookRepository appBookRepository;
    private final LikedBooksRepository likedBooksRepository;


    public SearchController(RegistrationService registrationService, ConfirmationTokenRepository confirmationTokenRepository, AppUserService appUserService, AppBookService appBookService, AppUserRepository appUserRepository, AppBookRepository appBookRepository, TakenBooksRepository takenBooksRepository, LikedBooksRepository likedBooksRepository, AppUserRepository userRepo, BookCategoryRepository bookCategoryRepository, CategoriesOfBooksRepository categoriesOfBooksRepository) {
        this.appUserService = appUserService;
        this.appBookService = appBookService;
        this.appUserRepository = appUserRepository;
        this.appBookRepository = appBookRepository;
        this.likedBooksRepository = likedBooksRepository;
    }
    //SEARCH
    @RequestMapping(path = {"/searchbook"})
    public String searchbook(@AuthenticationPrincipal UserDetails userDetails, Model model, String keyword) {
        if (keyword != null) {
            List<AppBook> list = appBookService.getAllByKeyword(keyword);
            model.addAttribute("books", list);
        } else {
            Iterable<AppBook> books = appBookRepository.findAll();
            model.addAttribute("books", books);
        }
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<LikedBooks> lb = likedBooksRepository.findAllByUser(user);
        ArrayList<AppBook> likedbooks = new ArrayList<>();
        for (LikedBooks b : lb){
            likedbooks.add(b.getBook());
        }
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
}
