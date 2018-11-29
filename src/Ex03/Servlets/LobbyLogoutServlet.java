package Ex03.Servlets;

import Ex03.Manager.ErrorManager;
import Ex03.Manager.UsersManager;
import Ex03.Utils.Error;
import Ex03.Utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Handles user logout from the system
public class LobbyLogoutServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String usernameFromSession = SessionUtils.getUsername(request);
            UsersManager userManager = Ex03.Utils.ServletUtils.getUserManager(getServletContext());

            //get the user name map, and delete the user name of the request
            if (usernameFromSession != null) {
                SessionUtils.clearSession(request);
                userManager.removeUser(usernameFromSession);
            }
            //return to the login page
            response.sendRedirect("usersignup");
        } catch (Exception e) {
            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            errorManager.AddError(request.getSession(), new Error("An error occurred during the logout process",
                    Error.ErrorType.UNEXPECTED));
            response.sendRedirect("error.html");
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
