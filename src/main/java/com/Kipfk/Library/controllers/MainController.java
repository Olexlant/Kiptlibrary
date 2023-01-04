package com.Kipfk.Library.controllers;

import com.Kipfk.Library.appbook.*;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.registration.RegistrationService;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
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

    public MainController(RegistrationService registrationService, ConfirmationTokenRepository confirmationTokenRepository, AppUserService appUserService, AppBookService appBookService, AppUserRepository appUserRepository, AppBookRepository appBookRepository, TakenBooksRepository takenBooksRepository, LikedBooksRepository likedBooksRepository, AppUserRepository userRepo, BookCategoryRepository bookCategoryRepository, CategoriesOfBooksRepository categoriesOfBooksRepository, GroupsRepository groupsRepository) {
        this.registrationService = registrationService;
        this.appUserService = appUserService;
        this.appBookService = appBookService;
        this.appBookRepository = appBookRepository;
        this.takenBooksRepository = takenBooksRepository;
        this.likedBooksRepository = likedBooksRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.categoriesOfBooksRepository = categoriesOfBooksRepository;
        this.groupsRepository = groupsRepository;
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
    public String signUp(AppUser user, @RequestParam String group) {
       user.setGroups(groupsRepository.findAllById(Long.valueOf(group)));
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
        }else {
            Page<AppBook> bookPage = appBookService.findPaginated(PageRequest.of(currentPage - 1, pageSize));
            model.addAttribute("books",bookPage);
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
        List<CategoriesOfBooks> categoriesOfBooks = categoriesOfBooksRepository.findAll();
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<LikedBooks> lb = likedBooksRepository.findAllByUser(user);
        ArrayList<AppBook> likedbooks = new ArrayList<>();
        for (LikedBooks b : lb){
            likedbooks.add(b.getBook());
        }
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

    @GetMapping("/allbooks/{id}/ebook")
    public void showEbookFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        AppBook book = appBookRepository.findAllById(id);
        InputStream is = new ByteArrayInputStream(book.getBookfile());
        IOUtils.copy(is, response.getOutputStream());
    }

    @GetMapping("/mytakenbooks")
    public String showUserAssigned(@AuthenticationPrincipal UserDetails userDetails,Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        List<TakenBooks> takenBooks = takenBooksRepository.findAll();
        Stream<TakenBooks> tbs = takenBooks.stream().filter(findEmp -> user.getId().equals(findEmp.getUser().getId()));
        List<TakenBooks> tb = tbs.toList();
        model.addAttribute("takenbooks", tb);
        return "mytakenbooks";
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
        return "myfavouritebooks";
    }
    @PostMapping("/myfavouritebooks/{id}/remove")
    public String removelikedbooks(@PathVariable(value = "id") long id) {
        LikedBooks lb = likedBooksRepository.findById(id).orElseThrow();
        likedBooksRepository.delete(lb);
        return "redirect:/myfavouritebooks";
    }

    @GetMapping("/editprofile")
    public String showEditProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model){
        AppUser user = (AppUser) appUserService.loadUserByUsername(userDetails.getUsername());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        model.addAttribute("user",user);
        return "editprofile";
    }




}