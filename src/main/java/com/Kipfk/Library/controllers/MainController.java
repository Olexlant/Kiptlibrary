package com.Kipfk.Library.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.Kipfk.Library.appbook.AppBook;
import com.Kipfk.Library.appbook.AppBookRepository;
import com.Kipfk.Library.appbook.AppBookService;
import com.Kipfk.Library.appbook.QRCodeGenerator;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.email.EmailSender;
import com.Kipfk.Library.email.EmailService;
import com.Kipfk.Library.registration.RegistrationService;
import com.Kipfk.Library.registration.token.ConfirmationToken;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import com.Kipfk.Library.registration.token.ConfirmationTokenService;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;


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
    private final TakenBooksRepository takenBooksRepository;
    private final LikedBooksRepository likedBooksRepository;


    @Autowired
    private AppUserRepository userRepo;
    private ConfirmationToken confirmationToken;


    public MainController(EmailSender emailSender, EmailService emailService, RegistrationService registrationService, ConfirmationTokenService confirmationTokenService, ConfirmationTokenRepository confirmationTokenRepository, AppUserService appUserService, AppBookService appBookService, AppUserRepository appUserRepository, AppBookRepository appBookRepository, TakenBooksRepository takenBooksRepository, LikedBooksRepository likedBooksRepository) {
        this.emailSender = emailSender;
        this.emailService = emailService;
        this.registrationService = registrationService;
        this.confirmationTokenService = confirmationTokenService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.appUserService = appUserService;
        this.appBookService = appBookService;
        this.appUserRepository = appUserRepository;
        this.appBookRepository = appBookRepository;
        this.takenBooksRepository = takenBooksRepository;
        this.likedBooksRepository = likedBooksRepository;
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

        return "redirect:/allbooksadmin";
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

    @GetMapping("/admin")
    public String showAdminHome(Model model){
        List <AppUser> users =  appUserRepository.findAll();
        int usercount = users.size();
        model.addAttribute("usercount", usercount);
        List <AppBook> books = appBookRepository.findAll();
        int bookcount = books.size();
        model.addAttribute("bookcount",bookcount);
        List <TakenBooks> taken = takenBooksRepository.findAll();
        int takencount = taken.size();
        model.addAttribute("takencount",takencount);
        return "admin";
    }

    @GetMapping("/allbooksadmin")
    public String showAllBooksAdmin(Model model){
        List<AppBook> books = appBookRepository.findAll();
        model.addAttribute("books",books);
        return "allbooksadmin";
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
        return "redirect:/allusers";
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
        ConfirmationToken token = confirmationTokenRepository.findByAppUser(user).orElseThrow();
        confirmationTokenRepository.delete(token);
        appUserRepository.delete(user);
        return "redirect:/allusers";
    }


    @GetMapping("/takebook")
    public String showAssignForm(TakenBooks takenBooks,Model model) {
        List<AppUser> listUsers = userRepo.findAll();
        model.addAttribute("users", listUsers);
        List<AppBook> listBooks = appBookRepository.findAll();
        model.addAttribute("books", listBooks);
        return "takebook";
    }

    @PostMapping("/assigningbook")
    public String createtakenbook(TakenBooks takenBooks, Model model, @Valid Long bookid, @Valid Long userid) {
        AppUser user = appUserRepository.findById(userid).orElseThrow();
        takenBooks.setUser(user);
        AppBook book = appBookRepository.findById(bookid).orElseThrow();
        takenBooks.setBook(book);
        takenBooks.setTakenat(LocalDate.now());
        takenBooksRepository.save(takenBooks);
        model.addAttribute("takenBooks", takenBooksRepository.findAll());
        return "redirect:/takebook";
    }
    @GetMapping("/assignedbooks")
    public String showassignedbooks(Model model){
        List<TakenBooks> takenBooks = takenBooksRepository.findAll();
        model.addAttribute("takenbooks", takenBooks);
        return "assignedbooks";
    }

    @GetMapping("/userassigned")
    public String showUserAssigned(@AuthenticationPrincipal UserDetails userDetails,Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<TakenBooks> takenBooks = takenBooksRepository.findAll();
        Stream<TakenBooks> tbs = takenBooks.stream().filter(findEmp -> user.getId().equals(findEmp.getUser().getId()));
        List<TakenBooks> tb = tbs.toList();
        model.addAttribute("takenbooks", tb);
        return "userassigned";
    }
    @PostMapping("/assignedbooks/{id}/remove")
    public String removeassignedbooks(@PathVariable(value = "id") long id) {
        TakenBooks tb = takenBooksRepository.findById(id).orElseThrow();
        takenBooksRepository.delete(tb);
        return "redirect:/assignedbooks";
    }

    @PostMapping("/likingbook/{id}")
    public String createlikedbook(@AuthenticationPrincipal UserDetails userDetails,LikedBooks likedBooks, Model model, @PathVariable(value = "id") long bookid) {
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        likedBooks.setUser(user);
        AppBook book = appBookRepository.findById(bookid).orElseThrow();
        likedBooks.setBook(book);
        likedBooks.setAddedat(LocalDate.now());
        likedBooksRepository.save(likedBooks);

        return "redirect:/allbooks";
    }

    @GetMapping("/myfavouritebooks")
    public String showUserFavouriteBooks(@AuthenticationPrincipal UserDetails userDetails,Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<LikedBooks> likedBooks = likedBooksRepository.findAll();
        Stream<LikedBooks> likedBooksStream = likedBooks.stream().filter(findEmp -> user.getId().equals(findEmp.getUser().getId()));
        List<LikedBooks> lb = likedBooksStream.toList();
        model.addAttribute("likedbooks", lb);
        return "myfavouritebooks";
    }
    @PostMapping("/myfavouritebooks/{id}/remove")
    public String removelikedbooks(@PathVariable(value = "id") long id) {
        LikedBooks lb = likedBooksRepository.findById(id).orElseThrow();
        likedBooksRepository.delete(lb);
        return "redirect:/myfavouritebooks";
    }
    @PostMapping("/usertakenadmin/{id}")
    public String showusertaken(@PathVariable(value = "id") long userid,Model model){
        AppUser user = appUserRepository.findById(userid).orElseThrow();
        List<TakenBooks> takenBooks = takenBooksRepository.findAll();
        Stream<TakenBooks> tbs = takenBooks.stream().filter(findEmp -> user.getId().equals(findEmp.getUser().getId()));
        List<TakenBooks> tb = tbs.toList();
        model.addAttribute("user",user);
        model.addAttribute("usertaken", tb);
        return "usertakenadmin";
    }

}