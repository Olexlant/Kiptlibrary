package com.Kipfk.Library.appuser;

import com.Kipfk.Library.appbook.AppBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TakenBooksRepository extends JpaRepository <TakenBooks,Long>, JpaSpecificationExecutor<TakenBooks> {
    List<TakenBooks> findByUserAndDeletedIsFalse(AppUser appUser);

    boolean existsByUserIdAndBookIdAndDeletedIsFalse(Long userId, Long bookId);
    Optional<TakenBooks> findByBookIdAndDeletedIsFalse(Long id);

    List<TakenBooks> findAllByUserAndDeletedIsFalse(AppUser appUser);

    List<TakenBooks> findAllByBookAndDeletedIsTrue(AppBook book);
    Page<TakenBooks> findAllByDeletedIsFalse(Pageable pageable);
    Page<TakenBooks> findAllByDeletedIsTrue(Pageable pageable);
    List<TakenBooks> findAllByDeletedIsFalseAndNotificationSendedIsFalseAndReturnExpiresAtIsBefore(LocalDate now);
    Page<TakenBooks> findAllByDeletedIsFalseAndReturnExpiresAtIsBefore(Pageable pageable, LocalDate now);

    @Transactional
    void deleteAllByUserAndDeletedIsTrue(AppUser appUser);

    @Transactional
    void deleteAllByDeletedIsTrue();

    int countAllByDeletedIsFalse();
}
