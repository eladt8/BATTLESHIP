package Ex03.Servlets;

import Ex03.Constants.Constants;
import Ex03.Manager.ErrorManager;
import Ex03.Manager.LobbyRoomsManager;
import Ex03.Utils.Error;
import Ex03.Utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Handles room game room deletion - possible by only the room creator
public class RemoveRoomServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String roomName = request.getParameter("roomToRemove");
        try {
            LobbyRoomsManager roomsManager = ServletUtils.GetLobbyRoomManager(getServletContext());
            String userWhoWantToDelete = Ex03.Utils.ServletUtils.getUserManager(getServletContext()).GetUserNameBySession(request.getSession());
            String roomUploader = roomsManager.GetRoomByName(roomName).GetUploaderRoomName();

            if (roomUploader.equals(userWhoWantToDelete)) {
                roomsManager.RemoveRoom(roomName);
            }
        } catch (Exception e) {
            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            errorManager.AddError(request.getSession(), new Error("An error occurred while trying to delete a room",
                    Error.ErrorType.UNEXPECTED));
            response.sendRedirect(".." + Constants.ERROR_URL);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
