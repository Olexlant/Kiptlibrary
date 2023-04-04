package com.Kipfk.Library.controllers.news;

import com.Kipfk.Library.news.News;
import com.Kipfk.Library.news.NewsFilesStorage;
import com.Kipfk.Library.news.NewsFilesStorageRepository;
import com.Kipfk.Library.news.NewsRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@Controller
public class NewsController {
    private final NewsRepository newsRepository;
    private final NewsFilesStorageRepository newsFilesStorageRepository;
    @GetMapping("/news/{newsid}")
    public String showOneNews(Model model, @PathVariable Long newsid){
        News news = newsRepository.findAllById(newsid);
        List<NewsFilesStorageRepository.NewsFileInfo> newsFiles = newsFilesStorageRepository.findAllByNews_Id(newsid);
        model.addAttribute("newsFiles", newsFiles);
        model.addAttribute("news", news);
        return "news-details";
    }

    @GetMapping("/news/image/{newsid}")
    public void showNewsImage(@PathVariable Long newsid, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        News news = newsRepository.findAllById(newsid);
        InputStream is = new ByteArrayInputStream(news.getNewsPhoto());
        IOUtils.copy(is, response.getOutputStream());
    }

    @GetMapping("/news/file/{newsfileid}")
    public void showNewsFile(@PathVariable Long newsfileid, HttpServletResponse response) throws IOException {
        NewsFilesStorage filesStorage = newsFilesStorageRepository.findAllById(newsfileid);
        response.setContentType(filesStorage.getFileContentType());
        InputStream is = new ByteArrayInputStream(filesStorage.getFile());
        IOUtils.copy(is, response.getOutputStream());
    }
}
