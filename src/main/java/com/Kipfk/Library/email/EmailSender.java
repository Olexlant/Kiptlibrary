package com.Kipfk.Library.email;

import com.Kipfk.Library.appbook.AppBook;
import com.Kipfk.Library.appuser.AppUser;
import com.Kipfk.Library.appuser.TakenBooks;

public interface EmailSender {
    void sendregistrationmail(AppUser user, String link);
    void sendchangepasswordmail(AppUser user, String link);
    void sendNotificationMessage(AppUser user, AppBook book, TakenBooks takenBooks);

}
