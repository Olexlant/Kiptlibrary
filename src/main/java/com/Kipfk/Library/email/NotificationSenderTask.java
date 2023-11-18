package com.Kipfk.Library.email;

import com.Kipfk.Library.appuser.TakenBooks;
import com.Kipfk.Library.appuser.TakenBooksRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@EnableScheduling
public class NotificationSenderTask {

    private final TakenBooksRepository takenBooksRepository;
    private final EmailSender emailSender;

    public NotificationSenderTask(TakenBooksRepository takenBooksRepository, EmailSender emailSender) {
        this.takenBooksRepository = takenBooksRepository;
        this.emailSender = emailSender;
    }

    @Scheduled(fixedRate = 1000*60*60*24)
    public void run() {
        List<TakenBooks> t = takenBooksRepository.findAllByDeletedIsFalseAndNotificationSendedIsFalseAndReturnExpiresAtIsBefore(LocalDate.now());
        if (!t.isEmpty()){
            for(TakenBooks i: t){
                if(!i.getBook().isElectronic()){
                    emailSender.sendNotificationMessage(i.getUser(), i.getBook(), i);
                }
                i.setNotificationSended(true);
                takenBooksRepository.save(i);
            }
        }
    }

}
