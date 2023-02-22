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

    Optional<AppBook> findById(Long id);
    AppBook findAllById(Long id);
    Page<AppBook> findAllByBookfileIsNotNullAndBookfileurlIsNotNull(Pageable pageable);
    Page<AppBook> findAllByBookfileIsNullOrBookfileurlIsNull(Pageable pageable);
    int countAllBy();


}
