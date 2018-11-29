package Ex03.Utils;

import Ex03.Manager.*;

import javax.servlet.ServletContext;

public class ServletUtils {

    public static final String USER_MANAGER_ATTRIBUTE_NAME = "usersManager";
    public static final String LOBBYROOM_MANAGER_ATTRIBUTE_NAME = "roomManager";
    public static final String GAMES_MANAGER_ATTRIBUTE_NAME = "gamesManager";
    public static final String ERROR_MANAGER_ATTRIBUTE_NAME = "errorManager";
    public static final String CHAT_MANAGER_ATTRIBUTE_NAME="chatManager";


    public static UsersManager getUserManager(ServletContext servletContext) {
        if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UsersManager());
        }
        return (UsersManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static LobbyRoomsManager GetLobbyRoomManager(ServletContext servletContext) {
        if (servletContext.getAttribute(LOBBYROOM_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(LOBBYROOM_MANAGER_ATTRIBUTE_NAME, new LobbyRoomsManager());
        }
        return (LobbyRoomsManager) servletContext.getAttribute(LOBBYROOM_MANAGER_ATTRIBUTE_NAME);
    }

    public static GamesManager GetGamesManager(ServletContext servletContext) {
        if (servletContext.getAttribute(GAMES_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(GAMES_MANAGER_ATTRIBUTE_NAME, new GamesManager());
        }
        return (GamesManager) servletContext.getAttribute(GAMES_MANAGER_ATTRIBUTE_NAME);
    }

    public static ErrorManager GetErrorManager(ServletContext servletContext) {
        if (servletContext.getAttribute(ERROR_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(ERROR_MANAGER_ATTRIBUTE_NAME, new ErrorManager());
        }
        return (ErrorManager) servletContext.getAttribute(ERROR_MANAGER_ATTRIBUTE_NAME);
    }

    public static ChatManager getChatManager(ServletContext servletContext) {
        if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
        }
        return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }
}
