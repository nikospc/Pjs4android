package grppjs4.sug.Entities;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by otmane on 27/03/2018.
 */

public class Message {

    private String uid;
    private String username;
    private String userId;
    private String content;
    private String imageUrl;
    private Long date;

    public Message(){}


    public Message(String username, String userId, String content, String imageUrl) {
        this.username = username;
        this.userId = userId;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Map<String, String> getDate(){
        return ServerValue.TIMESTAMP;//COTER SERVEUR ENRENGISTRE
    }

/*/public String getdateformat(){
    Date date=new Date(getLongDate());
    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
    String dateText = df2.format(date);
    return dateText;
}*/




    @Override
    public boolean equals(Object obj) {
        if(obj ==this) return true;

        if(!(obj instanceof  Message)) return  false ;

        Message message = (Message) obj;
        return this.getUid().equals(message.getUid());
    }

    @Exclude
    public Long getLongDate() {
        return date;
    }

    @Exclude
    public String getdateformat(){
    Date date=new Date(getLongDate());
    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm");
    String dateText = df2.format(date);
    return dateText;
    }


}
