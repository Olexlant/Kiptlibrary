package com.Kipfk.Library.controllers;

import com.Kipfk.Library.news.NewsRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class MainController {
    private final NewsRepository newsRepository;

    @GetMapping("/")
    public String home(Model model) {
        Page<NewsRepository.NewsNoFile> news = newsRepository.findAllByDeletedIsFalse(PageRequest.of(0, 5, Sort.Direction.DESC,"createdAt"));
        model.addAttribute("news", news);
        model.addAttribute("title","Головна сторінка");
        return "home";
    }
    @GetMapping("/librariancontact")
    public String about(Model model) {
        model.addAttribute("title", "Про нас");
        return "libr-contact";
    }
}