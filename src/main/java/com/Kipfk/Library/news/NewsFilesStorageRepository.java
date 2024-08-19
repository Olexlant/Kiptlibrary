package com.Kipfk.Library.news;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface NewsFilesStorageRepository extends JpaRepository<NewsFilesStorage, Long> {
    interface NewsFileInfo{
        Long getId();
        String getFileName();
        String getFileContentType();
        News getNews();
    }
    NewsFilesStorage findAllById(Long newsfileid);
    List<NewsFileInfo> findAllByNews_Id(Long newsId);
    @Transactional
    void deleteAllByNews_Id(Long newsId);
}
