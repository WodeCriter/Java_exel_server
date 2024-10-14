package webApp.userManager;
import java.util.*;
public class UserManager
{
    private List<String> userNames = new LinkedList<>();

    public void addUser(String userName){
        if (isUserExist(userName))
            throw new RuntimeException("User already exists");
        userNames.add(userName);
    }

    public boolean isUserExist(String userName){
        return userNames.contains(userName);
    }
}
