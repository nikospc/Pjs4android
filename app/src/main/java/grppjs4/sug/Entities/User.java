package grppjs4.sug.Entities;

/**
 * Created by nikola on 30/03/18.
 */

public class User {



    private String username;
    private String id;


    public  User(){



    }


    public User (String username,String id){

        this.username=username;
        this.id=id;
    }

    public String getUsername(){
        return username;
    }

    public String getId(){
        return id;
    }
}
