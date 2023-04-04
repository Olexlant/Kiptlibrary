package com.Kipfk.Library.controllers.news;

import com.Kipfk.Library.appbook.AppBookService;
import com.Kipfk.Library.news.News;
import com.Kipfk.Library.news.NewsFilesStorage;
import com.Kipfk.Library.news.NewsFilesStorageRepository;
import com.Kipfk.Library.news.NewsRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
@Getter
@Setter
@AllArgsConstructor
@Controller
public class AdminNewsController {
    private final AppBookService appBookService;
    private final NewsRepository newsRepository;
    private final NewsFilesStorageRepository newsFilesStorageRepository;
    //NEWS
    @GetMapping("/admin/news")
    public String showNews(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<NewsRepository.NewsNoFile> news = newsRepository.findAllByDeletedIsFalse(PageRequest.of(currentPage - 1, pageSize));
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
        news.setCreatedAt(LocalDateTime.now());
        newsRepository.save(news);
        for (int i = 1; i < multipartFiles.length; i++) {
            NewsFilesStorage newsFilesStorage = new NewsFilesStorage();
            newsFilesStorage.setFileName(multipartFiles[i].getOriginalFilename());
            newsFilesStorage.setFile(multipartFiles[i].getBytes());
            newsFilesStorage.setFileContentType(multipartFiles[i].getContentType());
            newsFilesStorage.setNews(news);
            newsFilesStorageRepository.save(newsFilesStorage);
        }
        return "redirect:/admin/add-news?success";
    }

    @GetMapping("/admin/news/{newsid}")
    public String getOneNews(Model model, @PathVariable Long newsid){
        NewsRepository.NewsNoFile news = newsRepository.findAllByIdIs(newsid);
        model.addAttribute("news", news);
        return "news-edit";
    }

    @PostMapping("/admin/news/{newsid}/edit")
    public String editOneNews(@PathVariable Long newsid,@RequestParam String title, @RequestParam String description, @RequestParam("files") MultipartFile[] multipartFiles) throws IOException {
        News news = newsRepository.findAllById(newsid);
        news.setTitle(title);
        news.setDescription(description);
        if (!multipartFiles[0].isEmpty()){
            news.setNewsPhoto(multipartFiles[0].getBytes());
        }
        if (!multipartFiles[1].isEmpty()){
            newsFilesStorageRepository.deleteAllByNews_Id(newsid);
            for (int i = 1; i < multipartFiles.length; i++) {
                NewsFilesStorage newsFilesStorage = new NewsFilesStorage();
                newsFilesStorage.setFileName(multipartFiles[i].getOriginalFilename());
                newsFilesStorage.setFile(multipartFiles[i].getBytes());
                newsFilesStorage.setFileContentType(multipartFiles[i].getContentType());
                newsFilesStorage.setNews(news);
                newsFilesStorageRepository.save(newsFilesStorage);
            }
        }
        newsRepository.save(news);
        return "redirect:/admin/news?saved";
    }
    @PostMapping("/admin/news/{newsid}/delete")
    public String newsDelete(@PathVariable Long newsid){
        newsFilesStorageRepository.deleteAllByNews_Id(newsid);
        newsRepository.deleteById(newsid);
        return "redirect:/admin/news?deleted";
    }

}
