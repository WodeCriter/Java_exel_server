package webApp.userManager;
import java.util.*;
public class UserManager
{
    private List<String> userNames = new LinkedList<>();

    public void addUser(String userName){
        if (isUserExists(userName))
            throw new RuntimeException("User already exists");
        userNames.add(userName);
    }

    public boolean removeUser(String userName){
        return userNames.remove(userName);
    }

    public boolean isUserExists(String userName) {
        return userNames.contains(userName);
    }
}
