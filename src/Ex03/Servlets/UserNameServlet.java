package Ex03.Servlets;

import Ex03.Manager.ErrorManager;
import Ex03.Utils.Error;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static Ex03.Constants.Constants.ERROR_URL;
import static Ex03.Constants.Constants.USERNAME;

// Returns user name in JSON
public class UserNameServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String userNameFromSession = (String) request.getSession(false).getAttribute(USERNAME);
        Gson gson = new Gson();
        try {
            PrintWriter out = response.getWriter();
            out.println(gson.toJson(userNameFromSession));
            out.flush();
        } catch (Exception e) {
            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            errorManager.AddError(request.getSession(), new Error("Error trying to get user name",
                    Error.ErrorType.UNEXPECTED));
            response.sendRedirect(".." + ERROR_URL);
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