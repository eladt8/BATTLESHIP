package Ex03.Manager;

import javax.servlet.http.HttpSession;
import java.util.*;

// Manages user session and user names
public class UsersManager {

    private final Set<String> usersSet;
    private final Map<HttpSession,String> m_UserToSessionMap;

    public UsersManager() {
        usersSet = new HashSet<>();
        m_UserToSessionMap=new HashMap<>();
    }

    public void addUser(String username,HttpSession userSession) {
        usersSet.add(username);
        m_UserToSessionMap.put(userSession,username);
    }

    public void removeUser(String username) {
        usersSet.remove(username);
        m_UserToSessionMap.remove(username);
    }

    public String GetUserNameBySession(HttpSession i_UserSession){
        return m_UserToSessionMap.get(i_UserSession);
    }

    public Set<String> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public boolean isUserExists(String username) {
        return usersSet.contains(username);
    }

    public boolean isSessionExists(HttpSession userSession){
        return m_UserToSessionMap.containsValue(userSession);
    }
}
