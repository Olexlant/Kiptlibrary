package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BookOrdersRepository extends JpaRepository<BookOrders, Long> {

    Page<BookOrders> findAllByDeletedIsFalse(Pageable pageable);
    List<BookOrders> findByBookAndUserAndDeletedIsFalse(AppBook book, AppUser user);

    @Transactional
    void deleteAllByBookId(Long bookId);

    int countAllByDeletedIsFalse();

    @Transactional
    void deleteAllByUser(AppUser appUser);
    @Transactional
    void deleteAllByUserAndBook(AppUser appUser, AppBook book);
    List<BookOrders> findAllById(Long bookorderid);
}
