package com.Kipfk.Library.appuser;

import com.Kipfk.Library.appbook.AppBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TakenBooksRepository extends JpaRepository <TakenBooks,Long>, JpaSpecificationExecutor<TakenBooks> {
    List<TakenBooks> findByUser(AppUser appUser);
    List<TakenBooks> findByUserAndBookAndDeletedIsFalse(AppUser appUser, AppBook appBook);
    Optional<TakenBooks> findByBookId(Long id);
    List<TakenBooks> findAllByUser(AppUser appUser);
    List<TakenBooks> findAllByBook(AppBook book);
    Page<TakenBooks> findAllByDeletedIsFalse(Pageable pageable);
    Page<TakenBooks> findAllByDeletedIsTrue(Pageable pageable);
    List<TakenBooks> findAllByDeletedIsFalseAndNotificationSendedIsFalseAndTakenatIsBefore(LocalDate returnedAt);
    List<TakenBooks> findAllByDeletedIsTrue();
    int countAllBy();
}
