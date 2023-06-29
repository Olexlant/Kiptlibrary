package com.Kipfk.Library.controllers.bookCategory;

import com.Kipfk.Library.appbook.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Controller
public class AdminBookCategoryController {
    private final AppBookRepository appBookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final CategoriesOfBooksRepository categoriesOfBooksRepository;

//ADD CATEGORY TO BOOK
    @GetMapping("/admin/addcategorytobook/{id}")
    public String showaddcategorytobook(Model model, @PathVariable Long id){
        AppBookRepository.BookNoFileAndPhoto book = appBookRepository.findAppBookById(id);
        List<BookCategory> bc = bookCategoryRepository.findAllByBookId(id);
        List<CategoriesOfBooks> cofb = categoriesOfBooksRepository.findAll();
        model.addAttribute("book",book);
        model.addAttribute("allcategories", cofb);
        model.addAttribute("bookcategories", bc);
        return "addcategorytobook";
    }
    @PostMapping("/admin/addcategorytobook/{bookid}/add")
    public String addcategorytobook(@PathVariable Long bookid,@RequestParam Long categoryid){
        AppBook book = appBookRepository.findAllByIdOrderByTitle(bookid);
        CategoriesOfBooks category = categoriesOfBooksRepository.findAllById(categoryid);
        BookCategory bccheck = bookCategoryRepository.findByCategory_IdAndBook_Id(categoryid, bookid);
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
    public String deletecategoryfrombook(@PathVariable Long bookid,@PathVariable Long categoryid){
        BookCategory bc = bookCategoryRepository.findByCategory_IdAndBook_Id(categoryid, bookid);
        bookCategoryRepository.delete(bc);
        return "redirect:/admin/addcategorytobook/"+bookid+"?deleted";
    }
}
