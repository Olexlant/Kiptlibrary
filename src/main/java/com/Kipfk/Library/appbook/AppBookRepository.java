package com.Kipfk.Library.appbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppBookRepository extends JpaRepository<AppBook, Long>, JpaSpecificationExecutor<AppBook> {
    interface BookNoFileAndPhoto {
        Long getId();
        Long getQrid();
        Long getYear();
        Long getCount();
        String getTitle();
        String getDescription();
        String getAuthor();
        String getBookfileurl();
    }

    Optional<AppBook> findById(Long id);
    AppBook findAllById(Long id);
    Page<AppBook> findAllByBookfileIsNotNullOrBookfileurlIsNotLike(Pageable pageable,String empty);
    Page<AppBook> findAllByBookfileIsNullAndBookfileurlIsLike(Pageable pageable, String empty);
    int countAllBy();


}
