package com.Kipfk.Library.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.Kipfk.Library.appbook.AppBook;
import com.Kipfk.Library.appbook.AppBookRepository;
import com.Kipfk.Library.appbook.AppBookService;
import com.Kipfk.Library.appbook.QRCodeGenerator;
import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.AppUserRepository;
import com.Kipfk.Library.appuser.AppUserService;
import com.Kipfk.Library.email.EmailSender;
import com.Kipfk.Library.email.EmailService;
import com.Kipfk.Library.registration.RegistrationService;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import com.Kipfk.Library.registration.token.ConfirmationTokenService;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;


@Controller
public class MainController {

    private final EmailSender emailSender;
    private final EmailService emailService;
    private final RegistrationService registrationService;
    private final ConfirmationTokenService confirmationTokenService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final AppUserService appUserService;
    private final AppBookService appBookService;
    private final AppUserRepository appUserRepository;
    private final AppBookRepository appBookRepository;

    @Autowired
    private AppUserRepository userRepo;

    public MainController(EmailSender emailSender, EmailService emailService, RegistrationService registrationService, ConfirmationTokenService confirmationTokenService, ConfirmationTokenRepository confirmationTokenRepository, AppUserService appUserService, AppBookService appBookService, AppUserRepository appUserRepository, AppBookRepository appBookRepository) {
        this.emailSender = emailSender;
        this.emailService = emailService;
        this.registrationService = registrationService;
        this.confirmationTokenService = confirmationTokenService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.appUserService = appUserService;
        this.appBookService = appBookService;
        this.appUserRepository = appUserRepository;
        this.appBookRepository = appBookRepository;
    }


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title","Головна сторінка");
        return "home";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "signup_form";
    }

    @PostMapping("/process_register")
    public String signUp(AppUser user) {
       registrationService.register(user);
       return "register_success";
    }

    @GetMapping("/registration/confirm")
    public String confirm(@RequestParam(required=false,name="token") String token) {
        registrationService.confirmToken(token);
        return "confirm_success";
    }


    @GetMapping("/addbook")
    public String showBookAddingForm(Model model) {
        model.addAttribute("book", new AppBook());
        return "addbook";
    }

    @PostMapping("/book_adding")
    public String bookadd(AppBook appBook, MultipartFile photo) throws IOException {
        try {
            appBook.setQrimg(QRCodeGenerator.getQRCodeImage(String.valueOf(appBook.getQrid()),300,300));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        appBook.setBookimg(photo.getBytes());
        appBookService.bookadd(appBook);
        appBookRepository.save(appBook);

        return "bookadd_success";
    }

    @GetMapping("/allbooks")
    public String showAllBooks(Model model){
        Iterable<AppBook> books = appBookRepository.findAll();
        model.addAttribute("books",books);
        return "allbooks";
    }

    @GetMapping("/allbooks/{id}")
    public String showBookDetails(@PathVariable(value = "id") long id, Model model){
        if (!appBookRepository.existsById(id)){
            return "redirect:/allbooks";
        }
        Optional <AppBook> book = appBookRepository.findById(id);
        ArrayList <AppBook> rbook = new ArrayList<>();
        book.ifPresent(rbook::add);
        model.addAttribute("bookd", rbook);
        return "bookdetails";
    }


    @GetMapping("/allbooksadmin")
    public String showAllBooksAdmin(Model model){
        Iterable<AppBook> books = appBookRepository.findAll();
        model.addAttribute("books",books);
        return "allbooksadmin";
    }

    @GetMapping("/allbooksadmin/{id}")
    public String showAdminBookDetails(@PathVariable(value = "id") long id, Model model){

        Optional <AppBook> book = appBookRepository.findById(id);
        ArrayList <AppBook> rbook = new ArrayList<>();
        book.ifPresent(rbook::add);
        model.addAttribute("bookd", rbook);
        return "bookadmindetails";
    }

    @GetMapping("/allbooksadmin/{id}/edit")
    public String AdminBookEdit(@PathVariable(value = "id") long id, Model model){
        if (!appBookRepository.existsById(id)){
            return "redirect:/allbooksadmin";
        }
        Optional <AppBook> book = appBookRepository.findById(id);
        ArrayList <AppBook> rbook = new ArrayList<>();
        book.ifPresent(rbook::add);
        model.addAttribute("bookd", rbook);
        return "bookadminedit";
    }
    @PostMapping("/allbooksadmin/{id}/edit")
    public String AdminBookUpdate(@PathVariable(value = "id") long id,@RequestParam Long qrid, @RequestParam String title, @RequestParam String author, @RequestParam Long year, @RequestParam Long stilaj, @RequestParam Long polka, MultipartFile photo ) throws IOException {
        AppBook book = appBookRepository.findById(id).orElseThrow();
        book.setQrid(qrid);
        book.setTitle(title);
        book.setAuthor(author);
        book.setYear(year);
        book.setStilaj(stilaj);
        book.setPolka(polka);
        book.setBookimg(photo.getBytes());
        appBookRepository.save(book);
        return "redirect:/allbooksadmin";
    }
    @PostMapping("/allbooksadmin/{id}/remove")
    public String AdminBookDelete(@PathVariable(value = "id") long id) {
        AppBook book = appBookRepository.findById(id).orElseThrow();
        appBookRepository.delete(book);
        return "redirect:/allbooksadmin";
    }



    @GetMapping("/adduser")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "adduser";
    }

    @PostMapping("/process_useradd")
    public String signUpByAdd(AppUser user) {
        registrationService.register(user);
        return "adduser_success";
    }

    @GetMapping("/allusers")
    public String listUsers(Model model) {
        List<AppUser> listUsers = userRepo.findAll();
        model.addAttribute("Users", listUsers);
        return "allusers";
    }

    @GetMapping("/allusers/{id}/edit")
    public String AdminUserEdit(@PathVariable(value = "id") long id, Model model){
        if (!appUserRepository.existsById(id)){
            return "redirect:/allusers";
        }
        Optional <AppUser> user = appUserRepository.findById(id);
        ArrayList <AppUser> ruser = new ArrayList<>();
        user.ifPresent(ruser::add);
        model.addAttribute("userd", ruser);
        return "useradminedit";
    }
    @PostMapping("/allusers/{id}/edit")
    public String AdminUserUpdate(@PathVariable(value = "id") long id,@RequestParam String firstname, @RequestParam String lastname, @RequestParam String phonenum, @RequestParam String password, @RequestParam String email, @RequestParam String groups) {
        AppUser user = appUserRepository.findById(id).orElseThrow();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);
        user.setPhonenum(phonenum);
        user.setPassword(password);
        user.setGroups(groups);
        appUserRepository.save(user);
        return "redirect:/allusers";
    }
    @PostMapping("/allusers/{id}/remove")
    public String AdminUserDelete(@PathVariable(value = "id") long id) {
        AppUser user = appUserRepository.findById(id).orElseThrow();
        appUserRepository.delete(user);
        return "redirect:/allusers";
    }

    @GetMapping("/contact")
    public String ShowContact(){
        return "contact";
    }

    @GetMapping("/about")
    public String ShowAbout(){
        return "about";
    }
}