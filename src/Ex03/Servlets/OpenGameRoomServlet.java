package Ex03.Servlets;

import Ex03.GameEngine.GameEngine;
import Ex03.Manager.ErrorManager;
import Ex03.Manager.GamesManager;
import Ex03.Utils.Error;
import Ex03.Utils.RoomInitializeInfo;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// Opens a new game room- if all data is ok
public class OpenGameRoomServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        GamesManager gamesManager = Ex03.Utils.ServletUtils.GetGamesManager(getServletContext());
        GameEngine GameLogic = gamesManager.GetGameBySession(request.getSession()).GetGameEngine();
        int playerIndx = gamesManager.GetGameBySession(request.getSession()).GetUserIndexBySession(request.getSession());
        String playerName = Ex03.Utils.ServletUtils.getUserManager(getServletContext()).GetUserNameBySession(request.getSession());
        response.setContentType("application/json");
        try {
            PrintWriter out = response.getWriter();
            Gson json = new Gson();
            RoomInitializeInfo roomInfo = new RoomInitializeInfo(playerName, playerIndx, GameLogic.GetLinesInBoard(),
                    GameLogic.GetPlayer(playerIndx).GetCurrentMinesCount(),
                    GameLogic.GetPlayer(playerIndx).GetPlayerShipsBoard().GetBoard(), GameLogic.GetGameType(),
                    gamesManager.GetGameBySession(request.getSession()).GetGameName());
            out.println(json.toJson(roomInfo));
            out.flush();
        } catch (Exception e) {
            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            errorManager.AddError(request.getSession(), new Error("An error occurred while opening a game room",
                    Error.ErrorType.UNEXPECTED));
            response.sendRedirect("error.html");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
