package com.Kipfk.Library.controllers.categoriesOfBooks;

import com.Kipfk.Library.appbook.BookCategory;
import com.Kipfk.Library.appbook.BookCategoryRepository;
import com.Kipfk.Library.appbook.CategoriesOfBooks;
import com.Kipfk.Library.appbook.CategoriesOfBooksRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@Controller

public class AdminCategoriesOfBooks {
    private final BookCategoryRepository bookCategoryRepository;
    private final CategoriesOfBooksRepository categoriesOfBooksRepository;

    //CATEGORIES OF BOOKS
    @GetMapping("/admin/addbookcategory")
    public String showaddbookcategory(Model model){
        List<CategoriesOfBooks> categoriesOfBooks = categoriesOfBooksRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
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

    @GetMapping("/admin/books-by-category/{categoryId}")
    public String showBooksByCategory(Model model, @PathVariable Long categoryId){
        model.addAttribute("category", categoriesOfBooksRepository.findAllById(categoryId));
        model.addAttribute("books", bookCategoryRepository.findAllByCategoryId(categoryId));
        return "admin-books-by-category";
    }

}
