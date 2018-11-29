package Ex03.Servlets;

import Ex03.Manager.SingleGameManager;
import Ex03.Manager.UsersManager;
import Ex03.Utils.ServletUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ActiveGamePlayerList extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    // Returns in JSON the active players in room ( for the chat )
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            UsersManager usersManager = ServletUtils.getUserManager(request.getServletContext());
            SingleGameManager singleGame = ServletUtils.GetGamesManager(request.getServletContext()).GetGameBySession(request.getSession());

            if (singleGame.IsActive()) {
                HttpSession[] gameUsersSession = singleGame.GetUsersSessions();
                Set<String> usersSet = createUsersSetFromSession(gameUsersSession, usersManager);
                String json = gson.toJson(usersSet);
                out.println(json);
                out.flush();
            } else {
                throw new Exception("Room is not full yet");
            }
        } catch (Exception e) { //  So the chat will be initialized only when 2 players are signed in
            response.sendError(400);
        }
    }

    private Set<String> createUsersSetFromSession(HttpSession[] gameUsersSession, UsersManager i_UsersManager) {
        Set<String> userNames = new HashSet<>();

        for (HttpSession session : gameUsersSession) {
            userNames.add(i_UsersManager.GetUserNameBySession(session));
        }

        return userNames;
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
