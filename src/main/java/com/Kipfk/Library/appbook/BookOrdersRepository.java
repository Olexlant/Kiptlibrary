package com.Kipfk.Library.appbook;

import com.Kipfk.Library.appuser.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookOrdersRepository extends JpaRepository<BookOrders, Long> {
    Page<BookOrders> findAllByDeletedIsFalse(Pageable pageable);
    List<BookOrders> findByBookAndUserAndDeletedIsFalse(AppBook book, AppUser user);
    List<BookOrders> findAllByBook(AppBook book);
    List<BookOrders> findAllByUser(AppUser user);
    int countAllByDeletedIsFalse();
}
