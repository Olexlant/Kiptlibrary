package com.Kipfk.Library.email;

import com.Kipfk.Library.appuser.TakenBooks;
import com.Kipfk.Library.appuser.TakenBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@EnableScheduling
public class NotificationSenderTask {
    @Autowired
    private TakenBooksRepository takenBooksRepository;
    private final EmailSender emailSender;

    public NotificationSenderTask(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Scheduled(fixedRate = 1000*60*60*24)
    public void run() {
        List<TakenBooks> t = takenBooksRepository.findAllByDeletedIsFalseAndNotificationSendedIsFalseAndTakenatIsBefore(LocalDate.now().minusDays(30));
        if (!t.isEmpty()){
            for(TakenBooks i: t){
                emailSender.sendNotificationMessage(i.getUser(), i.getBook(), i);
                i.setNotificationSended(true);
                takenBooksRepository.save(i);
            }
        }
    }

}
