package com.Kipfk.Library.news;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    interface NewsNoFile {
        Long getId();
        String getTitle();
        String getDescription();
        LocalDateTime getCreatedAt();
    }
    News findAllById(Long id);
    Page<NewsNoFile> findAllByDeletedIsFalse(Pageable pageable);
    NewsNoFile findAllByIdIs(Long id);
}
