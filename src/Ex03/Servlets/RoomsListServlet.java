package Ex03.Servlets;

import Ex03.Constants.Constants;
import Ex03.GameLobby.Room;
import Ex03.Manager.ErrorManager;
import Ex03.Manager.LobbyRoomsManager;
import Ex03.Utils.Error;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

// Returns list of the game rooms in JSON
public class RoomsListServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LobbyRoomsManager LobbyRoomsManager = Ex03.Utils.ServletUtils.GetLobbyRoomManager(getServletContext());
        response.setContentType("application/json");
        try {
            PrintWriter out = response.getWriter();
            Gson json = new Gson();
            Set<Room> RoomsSet = LobbyRoomsManager.GetLobbyRooms();
            out.println(json.toJson(RoomsSet));
            out.flush();
        } catch (Exception e) {
            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            errorManager.AddError(request.getSession(), new Error("Error trying to get the room list",
                    Error.ErrorType.UNEXPECTED));
            response.sendRedirect(".." + Constants.ERROR_URL);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);


    }
}
