package Ex03.Servlets;

import Ex03.Constants.Constants;
import Ex03.Manager.*;
import Ex03.Utils.Error;
import Ex03.Utils.ServletUtils;
import com.google.gson.Gson;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import static Ex03.Constants.Constants.ERROR_URL;
import static Ex03.Constants.Constants.GAME_LOBBY_URL;
import static Ex03.Constants.Constants.GAME_ROOM_URL;

public class AddPlayerToRoomServlet extends HttpServlet {

    // Adds a signing players into the game room - max 2 allowed
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try {
            HttpSession enteringUserSession = request.getSession();
            String roomName = request.getParameter("roomToEnter");
            LobbyRoomsManager roomManager = ServletUtils.GetLobbyRoomManager(getServletContext());


            // Room name is ok, and the user is not registered to any other games
            GamesManager gamesManager = ServletUtils.GetGamesManager(getServletContext());
            if (roomManager.IsRoomExist(roomName) && gamesManager.GetGameBySession(enteringUserSession) == null && gamesManager.GetGameByName(roomName) == null) {

                roomManager.GetRoomByName(roomName).AddUserToWaitList(enteringUserSession);
                SingleGameManager newGame = new SingleGameManager(roomManager.GetRoomByName(roomName).GetRoomLogic(),
                        roomName, enteringUserSession);
                ServletUtils.GetGamesManager(getServletContext()).AddGame(newGame);


            } else if (gamesManager.GetGameByName(roomName) != null && gamesManager.GetGameByName(roomName).GetNumberOfWaitingUsers() == 1) {
                SingleGameManager gameToJoin = gamesManager.GetGameByName(roomName);
                gameToJoin.AddUserToGame(request.getSession());
                gamesManager.AddSessionAndGame(request.getSession(), gameToJoin);
                roomManager.GetRoomByName(roomName).AddUserToWaitList(enteringUserSession);

            } else {
                response.sendError(0);
            }

        } catch (Exception e) {
            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            errorManager.AddError(request.getSession(), new Error("An error occurred while adding player", Error.ErrorType.UNEXPECTED));
            response.sendRedirect(".." + Constants.ERROR_URL);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        processRequest(request, response);


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        processRequest(request, response);


    }
}