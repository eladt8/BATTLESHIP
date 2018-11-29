package Ex03.Servlets;

import Ex03.Constants.Constants;
import Ex03.GameEngine.PlayerStatics;
import Ex03.GameLobby.Room;
import Ex03.Manager.*;
import Ex03.Utils.Error;
import Ex03.Utils.ServletUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class EndOfGameServlet extends HttpServlet {

    // End of game actions - refresh engine, other user data
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            ChatManager chatManager = ServletUtils.getChatManager(request.getServletContext());
            GamesManager gamesManager = ServletUtils.GetGamesManager(getServletContext());
            SingleGameManager userGame = ServletUtils.GetGamesManager(getServletContext()).GetGameBySession(request.getSession());
            Room gameRoom = ServletUtils.GetLobbyRoomManager(getServletContext()).GetRoomByName(userGame.GetGameName());
            PrintWriter out = response.getWriter();
            gameRoom.RemoveUserFromWaitList(request.getSession());
            Gson gson = new Gson();
            PlayerStatics enemyStatics = userGame.GetGameEngine().
                    GetPlayer((userGame.GetUserIndexBySession(request.getSession()) + 1) % 2).GetStatics();
            out.println(gson.toJson(enemyStatics));
            out.flush();

            if (gameRoom.GetNumberOfUsersInTheRoom() == 0) {
                chatManager.ClearChatFromGame(userGame.GetGameName());
                userGame.SetIsActive(false);
                gameRoom.RestartEngine();   // Make room data ready for a new game
                gamesManager.RemoveGame(userGame); /*remove the game from the GamesManager*/
            }

        } catch (Exception e) {
            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            errorManager.AddError(request.getSession(), new Error("An error occurred while finishing game ",
                    Error.ErrorType.UNEXPECTED));
            response.sendRedirect(".." + Constants.ERROR_URL);
        }
    }
}