package com.Kipfk.Library.email;

public interface EmailSender {
    void sendregistrationmail(String to, String email);
    void sendchangepasswordmail(String to,String email);
    void sendNotificationMessage(String to,String email);

}
