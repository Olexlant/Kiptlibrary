package com.Kipfk.Library.email;

import com.Kipfk.Library.appbook.BookOrdersRepository;
import com.Kipfk.Library.appuser.*;
import com.Kipfk.Library.registration.token.ConfirmationToken;
import com.Kipfk.Library.registration.token.ConfirmationTokenRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@EnableAsync
@Service
@EnableScheduling
public class NotificationSenderTask {

    private final TakenBooksRepository takenBooksRepository;
    private final EmailSender emailSender;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final LikedBooksRepository likedBooksRepository;
    private final BookOrdersRepository bookOrdersRepository;
    private final AppUserRepository appUserRepository;

    public NotificationSenderTask(TakenBooksRepository takenBooksRepository, EmailSender emailSender, ConfirmationTokenRepository confirmationTokenRepository, LikedBooksRepository likedBooksRepository, BookOrdersRepository bookOrdersRepository, AppUserRepository appUserRepository) {
        this.takenBooksRepository = takenBooksRepository;
        this.emailSender = emailSender;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.likedBooksRepository = likedBooksRepository;
        this.bookOrdersRepository = bookOrdersRepository;
        this.appUserRepository = appUserRepository;
    }

    @Async
    @Scheduled(cron="0 0 8 * * *")
    public void sendEmailNotificationsAndReturnElectronicBooks() throws InterruptedException {
        List<TakenBooks> t = takenBooksRepository.findAllByDeletedIsFalseAndNotificationSendedIsFalseAndReturnExpiresAtIsBefore(LocalDate.now());
        if (!t.isEmpty()){
            for(TakenBooks i: t){
                if(!i.getBook().isElectronic()){
                    emailSender.sendNotificationMessage(i.getUser(), i.getBook(), i);
                } else {
                    i.setDeleted(true);
                    i.setReturnedAt(LocalDate.now());
                }
                i.setNotificationSended(true);
                takenBooksRepository.save(i);
            }
        }
        Thread.sleep(2000);
    }

    @Async
    @Scheduled(cron="0 0 5 * * *")
    public void deleteUsersAfterFiveYears(){
        List<ConfirmationToken> userConfirmationTokens = confirmationTokenRepository
                .findAllByCreatedAtIsBeforeAndAppUser_AppUserRole(LocalDateTime.now().minusYears(5), AppUserRole.USER);

        for (ConfirmationToken i: userConfirmationTokens){
            if (!takenBooksRepository.existsByUser_IdAndDeletedIsFalse(i.getAppUser().getId())){
                likedBooksRepository.deleteAllByUser(i.getAppUser());
                bookOrdersRepository.deleteAllByUser(i.getAppUser());
                takenBooksRepository.deleteAllByUserAndDeletedIsTrue(i.getAppUser());
                confirmationTokenRepository.deleteByAppUser(i.getAppUser());
                appUserRepository.delete(i.getAppUser());
                System.out.println("User deleted after five years: " + i.getAppUser().getEmail());
            }
        }
    }

}
