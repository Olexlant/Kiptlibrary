package com.Kipfk.Library.news;

import com.Kipfk.Library.appuser.TakenBooks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    interface NewsNoFile {
        Long getId();
        String getTitle();
        String getDescription();
    }

    Page<NewsNoFile> findAllByDeletedIsFalse(Pageable pageable);
}
