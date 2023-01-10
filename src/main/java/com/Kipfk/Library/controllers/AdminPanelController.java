package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.registration.RegistrationService;
import com.Kipfk.Library.registration.token.ConfirmationToken;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import com.google.zxing.WriterException;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
public class AdminPanelController {
    @Bean
    public MultipartResolver adminMultipartResolver() {
        return new StandardServletMultipartResolver();
    }

    public static int[] merge(int[]... intarrays) {
        return Arrays.stream(intarrays).flatMapToInt(Arrays::stream)
                .toArray();
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
    private final GroupsRepository groupsRepository;


    public AdminPanelController(RegistrationService registrationService, ConfirmationTokenRepository confirmationTokenRepository, AppUserService appUserService, AppBookService appBookService, AppUserRepository appUserRepository, AppBookRepository appBookRepository, TakenBooksRepository takenBooksRepository, LikedBooksRepository likedBooksRepository, AppUserRepository userRepo, BookCategoryRepository bookCategoryRepository, CategoriesOfBooksRepository categoriesOfBooksRepository, GroupsRepository groupsRepository) {
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
        this.groupsRepository = groupsRepository;
    }

//ADDBOOK
    @GetMapping("/admin/addbook")
    public String showBookAddingForm(Model model) {
        List<Groups> groups = groupsRepository.findAll();
        model.addAttribute("groups", groups);
        model.addAttribute("book", new AppBook());
        return "addbook";
    }

    @PostMapping("/admin/book_adding")
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

        return "redirect:/admin/allbooksadmin";
    }
    @GetMapping("/admin")
    public String showAdminHome(Model model){
        int usercount = appUserRepository.countAllBy();
        model.addAttribute("usercount", usercount);
        int bookcount = appBookRepository.countAllBy();
        model.addAttribute("bookcount",bookcount);
        int takencount = takenBooksRepository.countAllBy();
        model.addAttribute("takencount",takencount);
        return "admin";
    }

    @GetMapping("/admin/allbooksadmin")
    public String showAllBooksAdmin(Model model){
        List<AppBook> books = appBookRepository.findAll();
        model.addAttribute("books",books);
        return "allbooksadmin";
    }

    @GetMapping("/admin/allbooksadmin/{id}/edit")
    public String AdminBookEdit(@PathVariable(value = "id") long id, Model model){
        if (!appBookRepository.existsById(id)){
            return "redirect:/allbooksadmin";
        }
        Optional<AppBook> book = appBookRepository.findById(id);
        AppBook b = appBookRepository.findAllById(id);
        ArrayList<AppBook> rbook = new ArrayList<>();
        book.ifPresent(rbook::add);

        model.addAttribute("bookd", rbook);
        return "bookadminedit";
    }
    @PostMapping("/admin/allbooksadmin/{id}/edit")
    public String AdminBookUpdate(@PathVariable(value = "id") long id,@RequestParam Long qrid, @RequestParam String title, @RequestParam String author, @RequestParam Long year, @RequestParam Long stilaj, @RequestParam Long polka,@RequestParam("files") MultipartFile[] multipartFiles,@RequestParam String bookfileurl, @RequestParam String description, @RequestParam Long count) throws IOException {
        AppBook book = appBookRepository.findById(id).orElseThrow();
        if (!book.getQrid().equals(qrid)){
            try {
                book.setQrimg(QRCodeGenerator.getQRCodeImage(String.valueOf(qrid),300,300));
            } catch (WriterException | IOException e) {
                e.printStackTrace();
            }
        }
        book.setQrid(qrid);
        book.setTitle(title);
        book.setAuthor(author);
        book.setYear(year);
        book.setStilaj(stilaj);
        book.setPolka(polka);
        book.setDescription(description);
        book.setCount(count);
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
        return "redirect:/admin/allbooksadmin";
    }
    @PostMapping("/admin/allbooksadmin/{id}/remove")
    public String AdminBookDelete(@PathVariable(value = "id") long id) {
        AppBook book = appBookRepository.findById(id).orElseThrow();
        appBookRepository.delete(book);
        return "redirect:/allbooksadmin";
    }


    @GetMapping("/admin/adduser")
    public String showAddUserForm(Model model) {
        List<Groups> groups = groupsRepository.findAll();
        model.addAttribute("groups", groups);
        model.addAttribute("user", new AppUser());
        return "adduser";
    }

    @PostMapping("/admin/process_useradd")
    public String signUpByAdd(AppUser user, @RequestParam String groupid) {
        user.setGroups(groupsRepository.findAllById(Long.valueOf(groupid)));
        registrationService.register(user);
        return "redirect:/allusers";
    }

    @GetMapping("/admin/allusers")
    public String listUsers(Model model) {
        List<AppUser> listUsers = userRepo.findAll(Sort.by(Sort.Direction.ASC, "lastName"));
        model.addAttribute("Users", listUsers);
        return "allusers";
    }

    @GetMapping("/admin/allusers/{id}/edit")
    public String AdminUserEdit(@PathVariable(value = "id") long id, Model model){
        if (!appUserRepository.existsById(id)){
            return "redirect:/allusers";
        }
        List<Groups> groups = groupsRepository.findAll();
        model.addAttribute("groups", groups);
        Optional <AppUser> user = appUserRepository.findById(id);
        ArrayList <AppUser> ruser = new ArrayList<>();
        user.ifPresent(ruser::add);
        model.addAttribute("userd", ruser);
        return "useradminedit";
    }
    @PostMapping("/admin/allusers/{id}/edit")
    public String AdminUserUpdate(@PathVariable(value = "id") long id,@RequestParam String firstname, @RequestParam String lastname, @RequestParam String phonenum, @RequestParam String password, @RequestParam String email, @RequestParam String groupid) {
        AppUser user = appUserRepository.findById(id).orElseThrow();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);
        user.setPhonenum(phonenum);
        user.setPassword(password);
        user.setGroups(groupsRepository.findAllById(Long.valueOf(groupid)));
        appUserRepository.save(user);
        return "redirect:/admin/allusers";
    }
    @PostMapping("/admin/allusers/{id}/remove")
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
        return "redirect:/admin/allusers";
    }


    @GetMapping("/admin/takebook")
    public String showUsersToTake(Model model) {
        List<AppUser> listUsers = userRepo.findAll();
        model.addAttribute("users", listUsers);
        return "takebookuser";
    }
    @GetMapping("/admin/takebook/{id}")
    public String showBooksToTake(Model model, @PathVariable Long id) {
        List<AppBook> books = appBookRepository.findAll();
        model.addAttribute("userid", id);
        model.addAttribute("books", books);
        return "takebook";
    }

    @PostMapping("/admin/assigningbook/{userid}/{bookid}")
    public String createtakenbook(TakenBooks takenBooks, Model model, @PathVariable Long userid, @PathVariable Long bookid) {
        AppUser user = appUserRepository.findById(userid).orElseThrow();
        takenBooks.setUser(user);
        AppBook book = appBookRepository.findById(bookid).orElseThrow();
        takenBooks.setBook(book);
        boolean uniquetb = takenBooksRepository.findByUserAndBook(user, book).isEmpty();
        takenBooks.setTakenat(LocalDate.now());
        if (uniquetb){
            takenBooksRepository.save(takenBooks);
        }else {
            return "redirect:/admin/assignedbooks?alreadyassigned";
        }
        model.addAttribute("takenBooks", takenBooksRepository.findAll());
        return "redirect:/admin/assignedbooks?success";
    }
    @GetMapping("/admin/assignedbooks")
    public String showassignedbooks(Model model){
        List<TakenBooks> takenBooks = takenBooksRepository.findAll();
        model.addAttribute("takenbooks", takenBooks);
        return "assignedbooks";
    }

    @PostMapping("/admin/assignedbooks/{id}/remove")
    public String removeassignedbooks(@PathVariable(value = "id") long id) {
        TakenBooks tb = takenBooksRepository.findById(id).orElseThrow();
        takenBooksRepository.delete(tb);
        return "redirect:/admin/assignedbooks";
    }

    @PostMapping("/admin/usertakenadmin/{id}")
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
    @GetMapping("/admin/addbookcategory")
    public String showaddbookcategory(Model model){
        List<CategoriesOfBooks> categoriesOfBooks = categoriesOfBooksRepository.findAll();
        model.addAttribute("newcategory",new CategoriesOfBooks());
        model.addAttribute("categories",categoriesOfBooks);
        return "addbookcategory";
    }
    @PostMapping("/admin/addbookcategory")
    public String addbookcategory(Model model,CategoriesOfBooks categoriesOfBooks){
        categoriesOfBooksRepository.save(categoriesOfBooks);
        return "redirect:/admin/addbookcategory";
    }
    @PostMapping("/admin/deletebookcategory/{id}")
    public String deletebookcategory(@PathVariable Long id){
        CategoriesOfBooks categories = categoriesOfBooksRepository.findById(id).orElseThrow();
        categoriesOfBooksRepository.delete(categories);
        return "redirect:/admin/addbookcategory";
    }


//ADD CATEGORY TO BOOK
    @GetMapping("/admin/addcategorytobook/{id}")
    public String showaddcategorytobook(Model model,@PathVariable Long id){
        AppBook book = appBookRepository.findAllById(id);
        List<BookCategory> bc = bookCategoryRepository.findAllByBookId(id);
        List<CategoriesOfBooks> cofb = categoriesOfBooksRepository.findAll();
        model.addAttribute("book",book);
        model.addAttribute("allcategories", cofb);
        model.addAttribute("bookcategories", bc);
        return "addcategorytobook";
    }
    @PostMapping("/admin/addcategorytobook/{bookid}/{categoryid}")
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
            return "redirect:/admin/addcategorytobook/"+bookid;
        }

        return "redirect:/admin/addcategorytobook/"+bookid;
    }
    @PostMapping("/admin/addcategorytobook/{bookid}/{categoryid}/delete")
    public String deletecategoryfrombook(Model model,@PathVariable Long bookid,@PathVariable Long categoryid){
        AppBook book = appBookRepository.findAllById(bookid);
        CategoriesOfBooks category = categoriesOfBooksRepository.findAllById(categoryid);
        BookCategory bc = bookCategoryRepository.findByCategoryAndBook(category, book);
        bookCategoryRepository.delete(bc);
        return "redirect:/admin/addcategorytobook/"+bookid;
    }

//GROUPS
    @GetMapping("/admin/groups")
    public String showGroupsForm(Model model) {
        List<Groups> groups = groupsRepository.findAll();
        model.addAttribute("groups", groups);
        model.addAttribute("group", new Groups());
        return "groups";
    }

    @PostMapping("/admin/groups/save")
    public String saveNewGroup(Groups groups) {
        groupsRepository.save(groups);
        return "redirect:/admin/groups";
    }
    @PostMapping("/admin/groups/{groupid}/delete")
    public String saveNewGroup(@PathVariable Long groupid) {
        groupsRepository.delete(groupsRepository.findAllById(groupid));
        return "redirect:/admin/groups";
    }

}
