package Ex03.Servlets;
import Ex03.Constants.Constants;
import Ex03.Manager.ChatManager;
import Ex03.Manager.SingleGameManager;
import Ex03.Utils.ServletUtils;
import Ex03.Utils.SessionUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Handles the chat send in the game room
public class SendChatServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        SingleGameManager userGame = ServletUtils.GetGamesManager(request.getServletContext()).GetGameBySession(request.getSession());
        String username = SessionUtils.getUsername(request);

        String userChatString = request.getParameter(Constants.CHAT_PARAMETER);
        if (userChatString != null && !userChatString.isEmpty()) {
            chatManager.addChatString(userChatString, username, userGame.GetGameName());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
