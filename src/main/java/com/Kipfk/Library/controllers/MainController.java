package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.registration.RegistrationService;
import com.Kipfk.Library.registration.token.ConfirmationToken;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import com.Kipfk.Library.security.PasswordEncoder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;




@Controller
public class MainController {
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    public static int[] merge(int[]... intarrays) {
        return Arrays.stream(intarrays).flatMapToInt(Arrays::stream)
                .toArray();
    }

    private final RegistrationService registrationService;
    private final AppUserService appUserService;
    private final AppBookService appBookService;
    private final AppBookRepository appBookRepository;
    private final TakenBooksRepository takenBooksRepository;
    private final LikedBooksRepository likedBooksRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final CategoriesOfBooksRepository categoriesOfBooksRepository;
    private final GroupsRepository groupsRepository;
    private final AppUserRepository appUserRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final BooksByGroupsRepository booksByGroupsRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookOrdersRepository bookOrdersRepository;

    public MainController(RegistrationService registrationService, ConfirmationTokenRepository confirmationTokenRepository, AppUserService appUserService, AppBookService appBookService, AppUserRepository appUserRepository, AppBookRepository appBookRepository, TakenBooksRepository takenBooksRepository, LikedBooksRepository likedBooksRepository, AppUserRepository userRepo, BookCategoryRepository bookCategoryRepository, CategoriesOfBooksRepository categoriesOfBooksRepository, GroupsRepository groupsRepository, AppUserRepository appUserRepository1, ConfirmationTokenRepository confirmationTokenRepository1, BooksByGroupsRepository booksByGroupsRepository, PasswordEncoder passwordEncoder, BookOrdersRepository bookOrdersRepository) {
        this.registrationService = registrationService;
        this.appUserService = appUserService;
        this.appBookService = appBookService;
        this.appBookRepository = appBookRepository;
        this.takenBooksRepository = takenBooksRepository;
        this.likedBooksRepository = likedBooksRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.categoriesOfBooksRepository = categoriesOfBooksRepository;
        this.groupsRepository = groupsRepository;
        this.appUserRepository = appUserRepository1;
        this.confirmationTokenRepository = confirmationTokenRepository1;
        this.booksByGroupsRepository = booksByGroupsRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookOrdersRepository = bookOrdersRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title","Головна сторінка");
        return "home";
    }

//REGISTRATION
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        List<Groups> groups = groupsRepository.findAll();
        model.addAttribute("groups", groups);
        model.addAttribute("user", new AppUser());
        return "signup_form";
    }
    @PostMapping("/process_register")
    public String signUp(AppUser user, @RequestParam Long groupid) {
       Groups group = groupsRepository.findById(groupid).get();
       user.setGroups(group);
       registrationService.register(user);
       return "register_success";
    }
    @GetMapping("/registration/confirm")
    public String confirm(@RequestParam(required=false,name="token") String token) {
        return registrationService.confirmToken(token);
    }
    @GetMapping("/confirmsuccess")
    public String ShowConfirmSuccessForm(){
        return "confirm_success";
    }

    //ALLBOOKS
    @RequestMapping(value = "/allbooks", method = RequestMethod.GET)
    public String showAllBooks(Model model,@AuthenticationPrincipal UserDetails userDetails, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("category") Optional<String> category){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        String categor = "";
        if (category.isPresent()){
            categor = category.get();
        }
        if (!categor.isEmpty()){
            CategoriesOfBooks cb = categoriesOfBooksRepository.findAllByName(category.get());
            ArrayList<BookCategory> bc = bookCategoryRepository.findAllByCategoryId(cb.getId());
            Page<AppBook> bookPage = appBookService.findPaginatedWithCategory(PageRequest.of(currentPage - 1, pageSize),bc);
            model.addAttribute("books",bookPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        }else {
            Page<AppBook> bookPage = appBookRepository.findAll(PageRequest.of(currentPage - 1, pageSize));
            model.addAttribute("books",bookPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        }
        List<CategoriesOfBooks> categoriesOfBooks = categoriesOfBooksRepository.findAll();
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<LikedBooks> lb = likedBooksRepository.findAllByUser(user);
        ArrayList<AppBook> likedbooks = new ArrayList<>();
        for (LikedBooks b : lb){
            likedbooks.add(b.getBook());
        }
        if (user.getAddress()==null || user.getBirthday()==null){
            return "redirect:/editprofile?nodata";
        }
        model.addAttribute("status","allbookspage");
        model.addAttribute("likedbooks", likedbooks);
        model.addAttribute("currentcategory",categor);
        model.addAttribute("bookcategories", categoriesOfBooks);
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

    @GetMapping("/mytakenbooks")
    public String showUserAssigned(@AuthenticationPrincipal UserDetails userDetails,Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<TakenBooks> takenBooks = takenBooksRepository.findAll();
        Stream<TakenBooks> tbs = takenBooks.stream().filter(findEmp -> user.getId().equals(findEmp.getUser().getId()));
        List<TakenBooks> tb = tbs.toList();
        model.addAttribute("takenbooks", tb);
        if (user.getAddress()==null || user.getBirthday()==null){
            return "redirect:/editprofile?nodata";
        }
        return "mytakenbooks";
    }
//GET BOOK FILE TO READ
    @GetMapping("/allbooks/{id}/ebook")
    public void showEbookFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        AppBook book = appBookRepository.findAllById(id);
        InputStream is = new ByteArrayInputStream(book.getBookfile());
        IOUtils.copy(is, response.getOutputStream());
    }

//USER ADD LIKED BOOKS
    @PostMapping("/likingbook/{id}")
    public String createlikedbook(@AuthenticationPrincipal UserDetails userDetails,LikedBooks likedBooks, @PathVariable(value = "id") long bookid) {
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        likedBooks.setUser(user);
        AppBook book = appBookRepository.findById(bookid).orElseThrow();
        likedBooks.setBook(book);
        likedBooks.setAddedat(LocalDate.now());

        boolean uniquelb = likedBooksRepository.findByUserAndBook(user, book).isEmpty();
        if (uniquelb){
            likedBooksRepository.save(likedBooks);
        }
        return "redirect:/allbooks";
    }

    @PostMapping("/likingbook/{id}/deletebyuser")
    public String deletelikedbookbyuser(@AuthenticationPrincipal UserDetails userDetails,LikedBooks likedBooks, @PathVariable(value = "id") long bookid) {
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        AppBook book = appBookRepository.findById(bookid).orElseThrow();
        LikedBooks likedbook = likedBooksRepository.findByBookAndUser(book, user);
        likedBooksRepository.delete(likedbook);
        return "redirect:/allbooks";
    }

//USER FAVOURITE BOOKS
    @GetMapping("/myfavouritebooks")
    public String showUserFavouriteBooks(@AuthenticationPrincipal UserDetails userDetails,Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<LikedBooks> likedBooks = likedBooksRepository.findAll();
        Stream<LikedBooks> likedBooksStream = likedBooks.stream().filter(findEmp -> user.getId().equals(findEmp.getUser().getId()));
        List<LikedBooks> lb = likedBooksStream.toList();
        model.addAttribute("likedbooks", lb);
        if (user.getAddress()==null || user.getBirthday()==null){
            return "redirect:/editprofile?nodata";
        }
        return "myfavouritebooks";
    }
    @PostMapping("/myfavouritebooks/{id}/remove")
    public String removelikedbooks(@PathVariable(value = "id") long id) {
        LikedBooks lb = likedBooksRepository.findById(id).orElseThrow();
        likedBooksRepository.delete(lb);
        return "redirect:/myfavouritebooks";
    }

//BOOKS BY USER GROUP
    @GetMapping("/booksbygroup")
    public String showBooksByGroup(@AuthenticationPrincipal UserDetails userDetails,Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        ArrayList<BooksByGroups> booksByGroups = booksByGroupsRepository.findAllByGroups(user.getGroups());
        List<AppBook> books = new ArrayList<>();
        for (BooksByGroups b : booksByGroups){
            books.add(b.getBook());
        }
        model.addAttribute("books",books);
        if (user.getAddress()==null || user.getBirthday()==null){
            return "redirect:/editprofile?nodata";
        }
        return "booksbygroup";
    }

//EDIT PROFILE
    @GetMapping("/editprofile")
    public String showEditProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        model.addAttribute("groups", groupsRepository.findAll());
        model.addAttribute("user",user);
        return "editprofile";
    }

    @PostMapping("/editprofile/save")
    public String saveProfileChanges(@AuthenticationPrincipal UserDetails userDetails, Model model, MultipartFile imgfile, @RequestParam String firstname, @RequestParam String lastname, @RequestParam String phonenum, @RequestParam String email, @RequestParam String groupid, @RequestParam String birthday, @RequestParam String address ) throws IOException {
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);
        user.setPhonenum(phonenum);
        user.setBirthday(LocalDate.parse(birthday));
        user.setAddress(address);
        if (!imgfile.isEmpty()){
            user.setProfileimage(imgfile.getBytes());
        }
        user.setGroups(groupsRepository.findAllById(Long.valueOf(groupid)));
        appUserRepository.save(user);
        model.addAttribute("user",user);
        return "redirect:/editprofile?success";
    }

//RESET PASSWORD
    @GetMapping("/resetpassword")
    public String resetPasswordForm(){
        return "sendresetpasswordmail";
    }

    @PostMapping("/resetpassword")
    public String sendResetPasswordMail(@RequestParam String email){
        if (appUserRepository.findByEmail(email).isPresent()){
            AppUser user = appUserRepository.findByEmail(email).get();
            ConfirmationToken token = confirmationTokenRepository.findByAppUser(user).get();
            token.setPasswordChangeExpiresAt(LocalDateTime.now().plusMinutes(10));
            confirmationTokenRepository.save(token);
            registrationService.sendchangepasswordmail(user, token.getToken());
            return "redirect:/resetpassword?sended";
        }else {
            return "redirect:/resetpassword?notfound";
        }
    }

    @GetMapping("/resetpassword/reset")
    public String resetPasswordFormByMail(Model model, @RequestParam(required=false,name="token") String token) {
        model.addAttribute("token", token);
        return "resetpassword";
    }
    @PostMapping("/resetpassword/reset")
    public String changePasswordByMail(@RequestParam String newpassword, @RequestParam String confirmnewpassword, @RequestParam(required=false,name="token") String token) {
        if (newpassword.equals(confirmnewpassword)){
            return registrationService.changePasswordBytoken(newpassword,token);
        }else {
            return "redirect:/resetpassword/reset?token="+token+"&notmatch";
        }
    }

//CHANGE PASSWORD IN PROFILE
    @PostMapping("/editprofile/changepassword")
    public String changePasswordInProfile(@AuthenticationPrincipal UserDetails userDetails,@RequestParam String currentpassword,@RequestParam String newpassword, @RequestParam String confirmnewpassword) {
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        if (passwordEncoder.bCryptPasswordEncoder().matches(currentpassword, user.getPassword())){
            if (newpassword.equals(confirmnewpassword)){
                user.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(newpassword));
                appUserRepository.save(user);
                return "redirect:/editprofile?passchanged";
            }else {
                return "redirect:/editprofile?notmatch";
            }
        }else {
            return "redirect:/editprofile?currentnotmatch";
        }

    }

//GET USER PROFILE PICTURE
    @GetMapping("/profile/image")
    public void showProfileImage(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        if (user.getProfileimage()==null){
            byte[] array = Files.readAllBytes(Paths.get("src/main/resources/static/images/book.jpg"));
            user.setProfileimage(array);
        }
        InputStream is = new ByteArrayInputStream(user.getProfileimage());
        IOUtils.copy(is, response.getOutputStream());
    }

//GET BOOK IMAGE BY ID
    @GetMapping("/book/image/{bookid}")
    public void showBookImage(@PathVariable Long bookid, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        AppBook book = appBookRepository.findAllById(bookid);
        InputStream is = new ByteArrayInputStream(book.getBookimg());
        IOUtils.copy(is, response.getOutputStream());
    }

//GET BOOK QRIMAGE BY BOOK ID
    @GetMapping("/qrcode/image/{bookid}")
    public void showQrCodeImage(@PathVariable Long bookid,HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        AppBook book = appBookRepository.findAllById(bookid);
        InputStream is = new ByteArrayInputStream(book.getQrimg());
        IOUtils.copy(is, response.getOutputStream());
    }


//BOOK ORDERS
    @PostMapping("/orderbook/{bookid}")
    public String addBookOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(value = "bookid") long bookid){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        BookOrders bookOrder = new BookOrders();
        AppBook book = appBookRepository.findAllById(bookid);
        if (bookOrdersRepository.findByBookAndUser(book, user).isEmpty()){
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