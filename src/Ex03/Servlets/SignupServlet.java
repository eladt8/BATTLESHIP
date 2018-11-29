package Ex03.Servlets;

import Ex03.Constants.Constants;
import Ex03.Manager.ErrorManager;
import Ex03.Manager.UsersManager;
import Ex03.Utils.Error;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static Ex03.Constants.Constants.GAME_LOBBY_URL;
import static Ex03.Constants.Constants.ERROR_URL;
import static Ex03.Constants.Constants.USERNAME;


// Handles user signup process and validation
public class SignupServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            UsersManager userManager = Ex03.Utils.ServletUtils.getUserManager(getServletContext());
            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            String usernameFromParameter = request.getParameter(USERNAME);

            if (usernameFromParameter != null) {
                usernameFromParameter = usernameFromParameter.trim();
            }

            if (request.getSession(false) == null) {
                //user is not logged in yet
                if (usernameFromParameter == null || usernameFromParameter.equals("")) {
                    //no username in session and no username in parameter -
                    response.sendRedirect(request.getContextPath() + Constants.SIGNUP_URL);
                } else {
                    // Remove white spaces from username
                    if (usernameFromParameter.equals("")) {
                        response.sendRedirect(request.getContextPath() + Constants.SIGNUP_URL);
                    } else if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                        errorManager.AddError(request.getSession(), new Error(errorMessage,
                                Error.ErrorType.NAME_USED));
                        response.sendRedirect("error.html");
                    } else {
                        // No session is available for the current request-> thats a new connection
                        // The username is new- add it to the users list

                        //set the username in a session so it will be available on each request
                        // Create a new session for the user if none exist
                        HttpSession userSession = request.getSession(true);
                        addUser(userSession, userManager, usernameFromParameter);
                        //redirect the request to the chat room - in order to actually change the URL
                        response.sendRedirect(request.getContextPath() + Constants.GAME_LOBBY_URL);
                    }
                }
            } else {
                if (!(userManager.GetUserNameBySession(request.getSession()) == null || userManager.GetUserNameBySession(request.getSession()).equals(""))
                        && (!userManager.isUserExists(usernameFromParameter))) {
                    response.sendRedirect(request.getContextPath() + GAME_LOBBY_URL);
                } else {
                    request.getSession().invalidate();
                    response.sendRedirect(request.getContextPath() + Constants.SIGNUP_URL);
                }
            }
        } catch (Exception e) {
            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            errorManager.AddError(request.getSession(), new Error("Error in the signup process",
                    Error.ErrorType.UNEXPECTED));
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + ERROR_URL);
            return;
        }
    }

    private void addUser(HttpSession i_UserSession, UsersManager i_UserManager, String i_Name) {
        i_UserSession.setAttribute(USERNAME, i_Name);
        i_UserManager.addUser(i_Name, i_UserSession);
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