package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.registration.RegistrationService;
import com.Kipfk.Library.registration.token.ConfirmationToken;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import com.google.zxing.WriterException;
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
import javax.validation.Valid;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;




@Controller
public class MainController {
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    private final RegistrationService registrationService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final AppUserService appUserService;
    private final AppBookService appBookService;
    private final AppUserRepository appUserRepository;
    private final AppBookRepository appBookRepository;
    private final TakenBooksRepository takenBooksRepository;
    private final LikedBooksRepository likedBooksRepository;
    private final AppUserRepository userRepo;
    private final BookCategoryRepository bookCategoryRepository;
    private final CategoriesOfBooksRepository categoriesOfBooksRepository;



    public MainController(RegistrationService registrationService, ConfirmationTokenRepository confirmationTokenRepository, AppUserService appUserService, AppBookService appBookService, AppUserRepository appUserRepository, AppBookRepository appBookRepository, TakenBooksRepository takenBooksRepository, LikedBooksRepository likedBooksRepository, AppUserRepository userRepo, BookCategoryRepository bookCategoryRepository, CategoriesOfBooksRepository categoriesOfBooksRepository) {
        this.registrationService = registrationService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.appUserService = appUserService;
        this.appBookService = appBookService;
        this.appUserRepository = appUserRepository;
        this.appBookRepository = appBookRepository;
        this.takenBooksRepository = takenBooksRepository;
        this.likedBooksRepository = likedBooksRepository;
        this.userRepo = userRepo;
        this.bookCategoryRepository = bookCategoryRepository;
        this.categoriesOfBooksRepository = categoriesOfBooksRepository;
    }

    //REGISTRATION
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
        if(token==null){
            return "redirect:/login";
        }else {
            registrationService.confirmToken(token);
        }
        return "confirm_success";
    }

    //ADDBOOK
    @GetMapping("/addbook")
    public String showBookAddingForm(Model model) {
        model.addAttribute("book", new AppBook());
        return "addbook";
    }

    @PostMapping("/book_adding")
    public String bookadd(AppBook appBook,@RequestParam("files") MultipartFile[] multipartFiles) throws IOException {
        try {
            appBook.setQrimg(QRCodeGenerator.getQRCodeImage(String.valueOf(appBook.getQrid()),300,300));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        appBook.setBookimg(multipartFiles[0].getBytes());
        if(appBook.getBookfileurl().isEmpty()){
            if (!multipartFiles[1].isEmpty()){
                appBook.setBookfile(multipartFiles[1].getBytes());
            }else {
                appBook.setBookfile(null);
            }
        }else {
            appBook.setBookfile(null);

        }
        appBookService.bookadd(appBook);
        appBookRepository.save(appBook);

        return "redirect:/allbooksadmin";
    }

//ALLBOOKS
    @RequestMapping(value = "/allbooks", method = RequestMethod.GET)
    public String showAllBooks(Model model,@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("category") Optional<String> category){
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
            int totalPages = bookPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }
        }else {
            Page<AppBook> bookPage = appBookService.findPaginated(PageRequest.of(currentPage - 1, pageSize));
            model.addAttribute("books",bookPage);
            int totalPages = bookPage.getTotalPages();
            if (totalPages > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }
        }
        List<CategoriesOfBooks> categoriesOfBooks = categoriesOfBooksRepository.findAll();
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

    @GetMapping("/allbooks/{id}/ebook")
    public void showEbookFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        AppBook book = appBookRepository.findAllById(id);
        InputStream is = new ByteArrayInputStream(book.getBookfile());
        IOUtils.copy(is, response.getOutputStream());

    }

//ADMIN
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
        AppBook b = appBookRepository.findAllById(id);
        ArrayList <AppBook> rbook = new ArrayList<>();
        book.ifPresent(rbook::add);

        model.addAttribute("bookd", rbook);
        return "bookadminedit";
    }
    @PostMapping("/allbooksadmin/{id}/edit")
    public String AdminBookUpdate(@PathVariable(value = "id") long id,@RequestParam Long qrid, @RequestParam String title, @RequestParam String author, @RequestParam Long year, @RequestParam Long stilaj, @RequestParam Long polka,@RequestParam("files") MultipartFile[] multipartFiles,@RequestParam String bookfileurl ) throws IOException {
        AppBook book = appBookRepository.findById(id).orElseThrow();
        book.setQrid(qrid);
        book.setTitle(title);
        book.setAuthor(author);
        book.setYear(year);
        book.setStilaj(stilaj);
        book.setPolka(polka);
        if (!multipartFiles[0].isEmpty()){
            book.setBookimg(multipartFiles[0].getBytes());
        }
        if(bookfileurl.isEmpty()){
            book.setBookfileurl("");
            if (!multipartFiles[1].isEmpty()){
                book.setBookfile(multipartFiles[1].getBytes());
            }else {
                book.setBookfile(null);
            }
        }else {
            book.setBookfile(null);
            book.setBookfileurl(bookfileurl);
        }
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
        boolean tokenpresent = confirmationTokenRepository.findByAppUser(user).isPresent();
        boolean bookspresent = likedBooksRepository.findByUser(user).isPresent();
        boolean takenBookspresent = takenBooksRepository.findByUser(user).isEmpty();
        if (tokenpresent){
            ConfirmationToken token = confirmationTokenRepository.findByAppUser(user).get();
            confirmationTokenRepository.delete(token);
        }
        if (bookspresent){
            LikedBooks books = likedBooksRepository.findByUser(user).get();
            likedBooksRepository.delete(books);
        }
        if (!takenBookspresent){
            TakenBooks takenBooks = takenBooksRepository.findByUser(user).get();
            takenBooksRepository.delete(takenBooks);
        }
        appUserRepository.delete(user);
        return "redirect:/allusers";
    }


    @GetMapping("/takebook")
    public String showAssignForm(Model model) {
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
        boolean uniquetb = takenBooksRepository.findByUserAndBook(user, book).isEmpty();
        takenBooks.setTakenat(LocalDate.now());
        if (uniquetb){
            takenBooksRepository.save(takenBooks);
        }
        model.addAttribute("takenBooks", takenBooksRepository.findAll());
        return "redirect:/assignedbooks";
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

//CATEGORIES OF BOOKS
    @GetMapping("/addbookcategory")
    public String showaddbookcategory(Model model){
        List<CategoriesOfBooks> categoriesOfBooks = categoriesOfBooksRepository.findAll();
        model.addAttribute("newcategory",new CategoriesOfBooks());
        model.addAttribute("categories",categoriesOfBooks);
        return "addbookcategory";
    }
    @PostMapping("/addbookcategory")
    public String addbookcategory(Model model,CategoriesOfBooks categoriesOfBooks){
        categoriesOfBooksRepository.save(categoriesOfBooks);
        return "redirect:/addbookcategory";
    }
    @PostMapping("/deletebookcategory/{id}")
    public String deletebookcategory(@PathVariable Long id){
        CategoriesOfBooks categories = categoriesOfBooksRepository.findById(id).orElseThrow();
        categoriesOfBooksRepository.delete(categories);
        return "redirect:/addbookcategory";
    }

//ADD CATEGORY TO BOOK
    @GetMapping("/addcategorytobook/{id}")
    public String showaddcategorytobook(Model model,@PathVariable Long id){
        List<BookCategory> bc = bookCategoryRepository.findAllByBookId(id);
        List<CategoriesOfBooks> cofb = categoriesOfBooksRepository.findAll();
        model.addAttribute("allcategories", cofb);
        model.addAttribute("bookcategories", bc);
        return "addcategorytobook";
    }
    @PostMapping("/addcategorytobook/{bookid}/{categoryid}")
    public String addcategorytobook(Model model,@PathVariable Long bookid,@PathVariable Long categoryid){

        AppBook book = appBookRepository.findById(bookid).get();
        CategoriesOfBooks category = categoriesOfBooksRepository.findById(categoryid).get();
        BookCategory bccheck = bookCategoryRepository.findByCategoryAndBook(category, book);
        if (bccheck == null){
            BookCategory bc = new BookCategory();
            bc.setBook(book);
            bc.setCategory(category);
            bookCategoryRepository.save(bc);
        }else {
            return "redirect:/addcategorytobook/"+bookid;
        }

        return "redirect:/addcategorytobook/"+bookid;
    }
    @PostMapping("/addcategorytobook/{bookid}/{categoryid}/delete")
    public String deletecategoryfrombook(Model model,@PathVariable Long bookid,@PathVariable Long categoryid){
        AppBook book = appBookRepository.findById(bookid).get();
        CategoriesOfBooks category = categoriesOfBooksRepository.findById(categoryid).get();
        BookCategory bc = bookCategoryRepository.findByCategoryAndBook(category, book);
        bookCategoryRepository.delete(bc);
        return "redirect:/addcategorytobook/"+bookid;
    }

//SEARCH
    @RequestMapping(path = {"/searchbook"})
    public String searchbook(Model model, String keyword) {
        if (keyword != null) {
            List<AppBook> list = appBookService.getAllByKeyword(keyword);
            model.addAttribute("books", list);
        } else {
            Iterable<AppBook> books = appBookRepository.findAll();
            model.addAttribute("books", books);
        }
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

}