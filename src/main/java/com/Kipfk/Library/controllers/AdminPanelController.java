package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.news.News;
import com.Kipfk.Library.news.NewsRepository;
import com.Kipfk.Library.registration.RegistrationService;
import com.Kipfk.Library.registration.token.ConfirmationToken;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import com.google.zxing.WriterException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminPanelController {
    @Bean
    public MultipartResolver adminMultipartResolver() {
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
    private final GroupsRepository groupsRepository;
    private final TakenBooksService takenBooksService;
    private final BooksByGroupsRepository booksByGroupsRepository;
    private final BookOrdersRepository bookOrdersRepository;
    private final NewsRepository newsRepository;

    public AdminPanelController(RegistrationService registrationService, ConfirmationTokenRepository confirmationTokenRepository, AppUserService appUserService, AppBookService appBookService, AppUserRepository appUserRepository, AppBookRepository appBookRepository, TakenBooksRepository takenBooksRepository, LikedBooksRepository likedBooksRepository, AppUserRepository userRepo, BookCategoryRepository bookCategoryRepository, CategoriesOfBooksRepository categoriesOfBooksRepository, GroupsRepository groupsRepository, TakenBooksService takenBooksService, BooksByGroupsRepository booksByGroupsRepository, BookOrdersRepository bookOrdersRepository, NewsRepository newsRepository) {
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
        this.takenBooksService = takenBooksService;
        this.booksByGroupsRepository = booksByGroupsRepository;
        this.bookOrdersRepository = bookOrdersRepository;
        this.newsRepository = newsRepository;
    }

//ADDBOOK
    @GetMapping("/admin/addbook")
    public String showBookAddingForm(Model model) {
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
                appBook.setElectronic(true);
            }else {
                appBook.setBookfile(null);
            }
        }else {
            appBook.setBookfile(null);
            appBook.setElectronic(true);
        }
        appBookService.bookadd(appBook);
        appBookRepository.save(appBook);

        return "redirect:/admin/addbook?success";
    }
    @GetMapping("/admin")
    public String showAdminHome(Model model){
        int usercount = appUserRepository.countAllByEnabledIsTrue();
        model.addAttribute("usercount", usercount);
        int bookOrdersCount = bookOrdersRepository.countAllByDeletedIsFalse();
        model.addAttribute("bookOrdersCount", bookOrdersCount);
        int bookcount = appBookRepository.countAllBy();
        model.addAttribute("bookcount",bookcount);
        int takencount = takenBooksRepository.countAllBy();
        model.addAttribute("takencount",takencount);
        return "admin";
    }

    @GetMapping("/admin/allbooksadmin")
    public String showAllBooksAdmin(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("category") Optional<String> category){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        String categor = "";
        Page<AppBookRepository.BookNoFileAndPhoto> bookPage;
        if (category.isPresent()){
            categor = category.get();
        }
        if (categor.equals("electronic")){
            bookPage = appBookRepository.findAllByElectronicIsTrue(PageRequest.of(currentPage - 1, pageSize));
            model.addAttribute("books", bookPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        }else if (categor.equals("physical")){
            bookPage = appBookRepository.findAllByElectronicIsFalse(PageRequest.of(currentPage - 1, pageSize));
            model.addAttribute("books", bookPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        }else {
            Page<AppBookRepository.BookNoFileAndPhoto> allBooksPage = appBookRepository.findAllBy(PageRequest.of(currentPage - 1, pageSize));
            model.addAttribute("books", allBooksPage);
            model.addAttribute("body", appBookService.bodyArrayForPages(allBooksPage));
        }
        model.addAttribute("category", categor);
        return "allbooksadmin";
    }


    @GetMapping("/admin/allbooksadmin/{id}/edit")
    public String AdminBookEdit(@PathVariable(value = "id") long id, Model model){
        if (!appBookRepository.existsById(id)){
            return "redirect:/allbooksadmin";
        }
        AppBook book = appBookRepository.findAllById(id);
        model.addAttribute("bookd", book);
        return "bookadminedit";
    }
    @PostMapping("/admin/allbooksadmin/{id}/edit")
    public String AdminBookUpdate(@PathVariable(value = "id") long id,@RequestParam Long qrid, @RequestParam String title, @RequestParam String author, @RequestParam Long year, @RequestParam("files") MultipartFile[] multipartFiles,@RequestParam String bookfileurl, @RequestParam String description, @RequestParam Long count) throws IOException {
        AppBook book = appBookRepository.findAllById(id);
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
        book.setDescription(description);
        book.setCount(count);
        if (!multipartFiles[0].isEmpty()){
            book.setBookimg(multipartFiles[0].getBytes());
            book.setElectronic(true);
        }
        if(bookfileurl.isEmpty()){
            book.setBookfileurl("");
            if (!multipartFiles[1].isEmpty()){
                book.setBookfile(multipartFiles[1].getBytes());
                book.setElectronic(true);
            }
        }else {
            book.setBookfile(null);
            book.setBookfileurl(bookfileurl);
            book.setElectronic(true);
        }
        appBookRepository.save(book);
        return "redirect:/admin/allbooksadmin?changessaved";
    }
    @PostMapping("/admin/allbooksadmin/{id}/remove")
    public String AdminBookDelete(@PathVariable(value = "id") long id) {
        if (takenBooksRepository.findByBookId(id).isPresent()){
            return "redirect:/admin/allbooksadmin?usernotreturn";
        } else {
            AppBook book = appBookRepository.findAllById(id);
            List<LikedBooks> likedBooks = likedBooksRepository.findAllByBook(book);
            if (!likedBooks.isEmpty()){
                likedBooksRepository.deleteAll(likedBooks);
            }
            List<BookCategory> bookCategories = bookCategoryRepository.findAllByBookId(id);
            if (!bookCategories.isEmpty()){
                bookCategoryRepository.deleteAll(bookCategories);
            }
            List<BooksByGroups> booksByGroups = booksByGroupsRepository.findAllByBook(book);
            if (!booksByGroups.isEmpty()){
                booksByGroupsRepository.deleteAll(booksByGroups);
            }
            List<BookOrders> bookOrders = bookOrdersRepository.findAllByBook(book);
            if (!bookOrders.isEmpty()){
                bookOrdersRepository.deleteAll(bookOrders);
            }
            appBookRepository.deleteById(id);
            return "redirect:/admin/allbooksadmin?deletesuccess";
        }

    }


    @GetMapping("/admin/adduser")
    public String showAddUserForm(Model model) {
        List<Groups> groups = groupsRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        model.addAttribute("groups", groups);
        model.addAttribute("user", new AppUser());
        return "adduser";
    }

    @PostMapping("/admin/processuseradd")
    public String signUpByAdd(AppUser user, @RequestParam String groupid, @RequestParam String role) {
        boolean UserExists = appUserRepository.findByEmail(user.getEmail()).isPresent();
        if(UserExists){
            return "redirect:/admin/adduser?emailpresent";
        }
        if (groupid.equals("")){
            user.setGroups(null);
        }else {
            user.setGroups(groupsRepository.findAllById(Long.valueOf(groupid)));
        }
        if (role.equals("ADMIN")){
            user.setAppUserRole(AppUserRole.ADMIN);
        }
        if (role.equals("USER")){
            user.setAppUserRole(AppUserRole.USER);
        }
        if (role.equals("TEACHER")){
            user.setAppUserRole(AppUserRole.TEACHER);
        }
        appUserService.signUpUser(user);
        appUserService.enableAppUser(user.getEmail());
        return "redirect:/admin/adduser?useradded";
    }

    @GetMapping("/admin/allusers")
    public String listUsers(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("category") Optional<String> category) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        String categor = "";
        Page<AppUserRepository.UserNoPhoto> userPage;
        if (category.isPresent()){
            categor = category.get();
        }
        if (categor.equals("students")) {
            userPage = appUserRepository.findAllByAppUserRoleAndEnabledIsTrue(PageRequest.of(currentPage - 1, pageSize), AppUserRole.USER);
        } else if (categor.equals("teachers")){
            userPage = appUserRepository.findAllByAppUserRoleAndEnabledIsTrue(PageRequest.of(currentPage - 1, pageSize), AppUserRole.TEACHER);
        }else{
            userPage = appUserRepository.findAllByEnabledIsTrue(PageRequest.of(currentPage - 1, pageSize));
        }
        model.addAttribute("Users",userPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(userPage));
        return "allusers";
    }

    @GetMapping("/admin/allusers/{id}/edit")
    public String AdminUserEdit(@PathVariable(value = "id") long id, Model model){
        if (!appUserRepository.existsById(id)){
            return "redirect:/allusers";
        }
        List<Groups> groups = groupsRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        model.addAttribute("groups", groups);
        Optional <AppUser> user = appUserRepository.findById(id);
        ArrayList <AppUser> ruser = new ArrayList<>();
        user.ifPresent(ruser::add);
        model.addAttribute("userd", ruser);
        return "useradminedit";
    }
    @PostMapping("/admin/allusers/{id}/edit")
    public String AdminUserUpdate(@PathVariable(value = "id") long id,@RequestParam String firstname, @RequestParam String lastname, @RequestParam String phonenum, @RequestParam String password, @RequestParam String email, @RequestParam String groupid,@RequestParam String role) {
        AppUser user = appUserRepository.findById(id).orElseThrow();
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);
        user.setPhonenum(phonenum);
        user.setPassword(password);
        if (groupid.equals("")){
            user.setGroups(null);
        }else {
            user.setGroups(groupsRepository.findAllById(Long.valueOf(groupid)));
        }
        if (role.equals("ADMIN")){
            user.setAppUserRole(AppUserRole.ADMIN);
        }
        if (role.equals("USER")){
            user.setAppUserRole(AppUserRole.USER);
        }
        if (role.equals("TEACHER")){
            user.setAppUserRole(AppUserRole.TEACHER);
        }
        appUserRepository.save(user);
        return "redirect:/admin/allusers?changessaved";
    }
    @PostMapping("/admin/allusers/{id}/remove")
    public String AdminUserDelete(@PathVariable(value = "id") long id) {
        AppUser user = appUserRepository.findById(id).orElseThrow();
        boolean tokenpresent = confirmationTokenRepository.findByAppUser(user).isPresent();
        boolean takenBookspresent = takenBooksRepository.findByUser(user).isEmpty();
        if (!takenBookspresent){
            return "redirect:/admin/usertakenadmin/"+id+"?notreturn";
        }else {
            List<LikedBooks> likedBooks = likedBooksRepository.findAllByUser(user);
            if (!likedBooks.isEmpty()){
                likedBooksRepository.deleteAll(likedBooks);
            }
            if (tokenpresent){
                ConfirmationToken token = confirmationTokenRepository.findByAppUser(user).get();
                confirmationTokenRepository.delete(token);
            }
            List<BookOrders> bookOrders = bookOrdersRepository.findAllByUser(user);
            if (!bookOrders.isEmpty()){
                bookOrdersRepository.deleteAll(bookOrders);
            }
            appUserRepository.delete(user);
            return "redirect:/admin/allusers?success";
        }

    }


    @GetMapping("/admin/takebook")
    public String showUsersToTake(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<AppUserRepository.UserNoPhoto> userPage = appUserRepository.findAllBy(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("users",userPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(userPage));
        return "takebookuser";
    }
    @GetMapping("/admin/takebook/{id}")
    public String showBooksToTake(Model model, @PathVariable Long id, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<AppBookRepository.BookNoFileAndPhoto> bookPage = appBookRepository.findAllBy(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("books",bookPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(bookPage));
        model.addAttribute("userid", id);
        return "takebook";
    }

    @PostMapping("/admin/assigningbook/{userid}/{bookid}")
    public String createtakenbook(TakenBooks takenBooks, @PathVariable Long userid, @PathVariable Long bookid, @RequestParam Long takeCount) {
        AppUser user = appUserRepository.findById(userid).orElseThrow();
        AppBook book = appBookRepository.findAllById(bookid);
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
        takenBooksRepository.deleteAll(takenBooksRepository.findAllByDeletedIsTrue());
        return "redirect:/admin/assignedbooks/history?deleted";
    }

    @PostMapping("/admin/assignedbooks/{id}/remove")
    public String removeassignedbooks(@PathVariable(value = "id") long id) {
        TakenBooks tb = takenBooksRepository.findById(id).orElseThrow();
        AppBook appBook = appBookRepository.findAllById(tb.getBook().getId());
        appBook.setCount(appBook.getCount()+tb.getCount());
        appBookRepository.save(appBook);
        tb.setDeleted(true);
        tb.setReturnedAt(LocalDate.now());
        takenBooksRepository.save(tb);
        return "redirect:/admin/assignedbooks?returned";
    }

    @GetMapping("/admin/usertakenadmin/{id}")
    public String showusertaken(@PathVariable(value = "id") long userid,Model model){
        AppUser user = appUserRepository.findById(userid).orElseThrow();
        List<TakenBooks> takenBooks = takenBooksRepository.findAllByUserAndDeletedIsFalse(user);
        model.addAttribute("user",user);
        model.addAttribute("usertaken", takenBooks);
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
    public String addbookcategory(CategoriesOfBooks categoriesOfBooks){
        categoriesOfBooksRepository.save(categoriesOfBooks);
        return "redirect:/admin/addbookcategory?success";
    }
    @PostMapping("/admin/deletebookcategory/{id}")
    public String deletebookcategory(@PathVariable Long id){
        CategoriesOfBooks categories = categoriesOfBooksRepository.findById(id).orElseThrow();
        ArrayList<BookCategory> bookCategory = bookCategoryRepository.findAllByCategoryId(id);
        bookCategoryRepository.deleteAll(bookCategory);
        categoriesOfBooksRepository.delete(categories);
        return "redirect:/admin/addbookcategory?deleted";
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
    @PostMapping("/admin/addcategorytobook/{bookid}/add")
    public String addcategorytobook(Model model,@PathVariable Long bookid,@RequestParam Long categoryid){
        AppBook book = appBookRepository.findAllById(bookid);
        CategoriesOfBooks category = categoriesOfBooksRepository.findById(categoryid).get();
        BookCategory bccheck = bookCategoryRepository.findByCategoryAndBook(category, book);
        if (bccheck == null){
            BookCategory bc = new BookCategory();
            bc.setBook(book);
            bc.setCategory(category);
            bookCategoryRepository.save(bc);
        }else {
            return "redirect:/admin/addcategorytobook/"+bookid+"?alreadyadded";
        }
        return "redirect:/admin/addcategorytobook/"+bookid+"?success";
    }

    @PostMapping("/admin/addcategorytobook/{bookid}/{categoryid}/delete")
    public String deletecategoryfrombook(Model model,@PathVariable Long bookid,@PathVariable Long categoryid){
        AppBook book = appBookRepository.findAllById(bookid);
        CategoriesOfBooks category = categoriesOfBooksRepository.findAllById(categoryid);
        BookCategory bc = bookCategoryRepository.findByCategoryAndBook(category, book);
        bookCategoryRepository.delete(bc);
        return "redirect:/admin/addcategorytobook/"+bookid+"?deleted";
    }

//GROUPS
    @GetMapping("/admin/groups")
    public String showGroupsForm(Model model) {
        List<Groups> groups = groupsRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        model.addAttribute("groups", groups);
        model.addAttribute("group", new Groups());
        return "groups";
    }

    @PostMapping("/admin/groups/save")
    public String saveNewGroup(Groups groups) {
        groupsRepository.save(groups);
        return "redirect:/admin/groups?success";
    }
    @PostMapping("/admin/groups/{groupid}/delete")
    public String deleteGroup(@PathVariable Long groupid) {
        Groups group = groupsRepository.findAllById(groupid);
        List<AppUser> users = appUserRepository.findAllByGroups(group);
        for (AppUser user : users){
            user.setGroups(null);
        }
        appUserRepository.saveAll(users);
        groupsRepository.delete(group);
        return "redirect:/admin/groups?deleted";
    }
    @PostMapping("/admin/groups/{groupId}/{groupName}/update")
    public String updateGroupName(@PathVariable Long groupId,@PathVariable String groupName) {
        Groups group = groupsRepository.findAllById(groupId);
        group.setName(groupName);
        groupsRepository.save(group);
        return "redirect:/admin/groups?saved";
    }
//USERS BY GROUP
    @GetMapping("/admin/usersbygroup/{groupid}")
    public String showUsersByGroup(Model model, @PathVariable Long groupid) {
        Groups group = groupsRepository.findAllById(groupid);
        List<AppUser> users = appUserRepository.findAllByGroups(group);
        model.addAttribute("users", users);
        model.addAttribute("group", group);
        return "usersByGroup";
    }

    @PostMapping("/admin/usersbygroup/{groupid}/{userid}")
    public String deleteUserFromGroup(Model model, @PathVariable Long groupid,@PathVariable Long userid) {
        AppUser user = appUserRepository.findById(userid).get();
        user.setGroups(null);
        appUserRepository.save(user);
        return "redirect:/admin/usersbygroup/"+groupid+"?userdeletedfromgroup";
    }

//BOOKS BY GROUP
    @GetMapping("/admin/booksbygroup/{groupid}")
    public String showBooksByGroup(Model model, @PathVariable Long groupid) {
        Groups group = groupsRepository.findAllById(groupid);
        List<BooksByGroups> users = booksByGroupsRepository.findAllByGroups(group);
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
        boolean ispresent = booksByGroupsRepository.findByGroupsAndBook(group, book).isEmpty();
        if (ispresent){
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
        Groups group = groupsRepository.findAllById(groupid);
        AppBook book = appBookRepository.findAllById(bookid);
        List<BooksByGroups> booksByGroups = booksByGroupsRepository.findByGroupsAndBook(group, book);
        booksByGroupsRepository.deleteAll(booksByGroups);
        return  "redirect:/admin/booksbygroup/"+groupid+"?bookdeleted";
    }

//BOOK ORDERS
    @GetMapping("/admin/bookorders")
    public String showBookOrders(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<BookOrders> bookOrders = bookOrdersRepository.findAllByDeletedIsFalse(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("bookOrders", bookOrders);
        model.addAttribute("body", appBookService.bodyArrayForPages(bookOrders));
        return "orders";
    }

    @PostMapping("/admin/bookorders/{bookorderid}/delete")
    public String deleteBookOrder (@PathVariable Long bookorderid){
        List<BookOrders> bookOrder = bookOrdersRepository.findAllById(bookorderid);
        bookOrdersRepository.deleteAll(bookOrder);
        return "redirect:/admin/bookorders?deletesuccess";
    }
//NEWS
    @GetMapping("/admin/news")
    public String showNews(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<News> news = newsRepository.findAll(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("news", news);
        model.addAttribute("body", appBookService.bodyArrayForPages(news));
        return "news";
    }

    @GetMapping("/admin/add-news")
    public String showAddNewsForm(Model model){
        model.addAttribute("news", new News());
        return "add-news";
    }

    @PostMapping("/admin/news_adding")
    public String newsAdd(News news,@RequestParam("files") MultipartFile[] multipartFiles) throws IOException {
        news.setNewsPhoto(multipartFiles[0].getBytes());
        news.setNewsFile(multipartFiles[1].getBytes());
        news.setCreatedAt(LocalDateTime.now());
        newsRepository.save(news);
        return "redirect:/admin/add-news?success";
    }

    @GetMapping("/admin/news/{newsid}")
    public String getOneNews(Model model, @PathVariable Long newsid){
        News news = newsRepository.findById(newsid).get();
        model.addAttribute("news", news);
        return "news-edit";
    }

    @PostMapping("/admin/news/{newsid}/edit")
    public String editOneNews(Model model, @PathVariable Long newsid,@RequestParam String title, @RequestParam String description, @RequestParam("files") MultipartFile[] multipartFiles) throws IOException {
        News news = newsRepository.findById(newsid).get();
        news.setTitle(title);
        news.setDescription(description);
        if (!multipartFiles[0].isEmpty()){
            news.setNewsPhoto(multipartFiles[0].getBytes());
        }
        if (!multipartFiles[1].isEmpty()) {
            news.setNewsFile(multipartFiles[1].getBytes());
        }
        newsRepository.save(news);
        return "redirect:/admin/news?saved";
    }
    @PostMapping("/admin/news/{newsid}/delete")
    public String newsDelete(@PathVariable Long newsid){
        newsRepository.delete(newsRepository.findById(newsid).get());
        return "redirect:/admin/news?deleted";
    }

    @GetMapping("/news/{newsid}")
    public String showOneNews(Model model, @PathVariable Long newsid){
        News news = newsRepository.findById(newsid).get();
        model.addAttribute("news", news);
       return "news-details";
    }

    @GetMapping("/news/image/{newsid}")
    public void showNewsImage(@PathVariable Long newsid, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        News news = newsRepository.findById(newsid).get();
        InputStream is = new ByteArrayInputStream(news.getNewsPhoto());
        IOUtils.copy(is, response.getOutputStream());
    }

    @GetMapping("/news/file/{newsid}")
    public void showNewsFile(@PathVariable Long newsid, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        News news = newsRepository.findById(newsid).get();
        InputStream is = new ByteArrayInputStream(news.getNewsFile());
        IOUtils.copy(is, response.getOutputStream());
    }

//DEBTOR USERS
    @GetMapping("/admin/debtorusers")
    public String showDebtorUsers(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<TakenBooks> takenPage = takenBooksRepository.findAllByDeletedIsFalseAndTakenatIsBefore(PageRequest.of(currentPage - 1, pageSize, Sort.Direction.DESC,"takenat"), LocalDate.now().minusDays(30));
        model.addAttribute("takenbooks", takenPage);
        model.addAttribute("body", appBookService.bodyArrayForPages(takenPage));
        return "debtor-users";
    }
}
