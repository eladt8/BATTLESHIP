package Ex03.Servlets;

import Ex03.Constants.Constants;
import Ex03.GameEngine.*;
import Ex03.Manager.ErrorManager;
import Ex03.Manager.SingleGameManager;
import Ex03.Utils.Error;
import Ex03.Utils.ServletUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import static Ex03.Constants.Constants.ERROR_URL;

// Game move servlet- attack, plant a mine, quit , etc
public class GameMoveServlet extends HttpServlet {

    private final String COLUMN_PARAMETER = "COL";
    private final String ROW_PARAMETER = "ROW";
    private final String TYPE_PARAMETER = "TYPE";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        GameEngine.HitStatus hitStatus = GameEngine.HitStatus.QUIT;
        Point attackedPoint = null;
        String typeOfGameMove = request.getParameter(TYPE_PARAMETER);
        String isMineDrop = request.getParameter("isMine");

        try {
            PrintWriter out = response.getWriter();
            SingleGameManager userGame = ServletUtils.GetGamesManager(getServletContext()).GetGameBySession(request.getSession());

            if (userGame != null) {
                Gson gson = new Gson();
                GameEngine userGameEngine = userGame.GetGameEngine();
                //If it is indeed the user turn
                if ((userGameEngine.GetCurrentPlayerInd() == userGame.GetUserIndexBySession(request.getSession()))
                        && (userGame.IsActive() || (typeOfGameMove.equals("QUIT")))) {
                    if (!isMineDrop.equals("TRUE")) {   // Can be null as well
                        if (typeOfGameMove.equals("POINT")) {
                            attackedPoint = getPointFromRequestParameters(request);
                            hitStatus = userGameEngine.MakeAMove(attackedPoint);
                        }

                        PostAction postMoveResults = createUserPostActionResult(hitStatus, userGameEngine, userGame.GetUserIndexBySession(request.getSession()), attackedPoint);
                        userGameEngine.SaveLastActionData(postMoveResults);
                        out.println(gson.toJson(postMoveResults));
                    } else if (isMineDrop.equals("TRUE")) {
                        Point minePoint = getPointFromRequestParameters(request);
                        if (userGameEngine.PlantAMine(minePoint)) {
                            PostAction plantedMinePostAction = createPlantedMinePostAction(userGameEngine, minePoint);
                            // Not saving last action data= irrelevant for the other player
                            out.println(gson.toJson(plantedMinePostAction));
                        }
                    }
                }
            }
        } catch (Exception e) {
            response.sendRedirect(".." + Constants.ERROR_URL);
        }
    }

    private PostAction createPlantedMinePostAction(GameEngine userGameEngine, Point minePoint) {
        PostAction afterPlant = new PostAction();
        List<Point> pointToChangeInShipBoard = new LinkedList();

        afterPlant.SetPreviousPlayerIndex(userGameEngine.GetCurrentPlayerInd());
        afterPlant.SetNextPlayerIndex((userGameEngine.GetCurrentPlayerInd() + 1) % 2);
        afterPlant.SetMoveResult(GameEngine.HitStatus.MINE_PLANTED);
        updateShipsLeftOnPostAction(userGameEngine, userGameEngine.GetCurrentPlayerInd(), afterPlant);
        endPlayerTurn(userGameEngine, userGameEngine.GetCurrentPlayerInd(),
                (userGameEngine.GetCurrentPlayerInd() + 1) % userGameEngine.k_NumberOfPlayers, GameEngine.HitStatus.MINE_PLANTED);
        afterPlant.SetPlayerStatics(userGameEngine.GetPlayer((userGameEngine.GetCurrentPlayerInd() + 1) % 2).GetStatics());
        userGameEngine.SaveLastActionData(afterPlant);  // Saving the post action before the insertion of the points list - the enemy should not update his board after mine planted
        pointToChangeInShipBoard.add(minePoint);
        afterPlant.SetShipPointsToChange(pointToChangeInShipBoard);
        afterPlant.SetCharToChangeShipPoints('M');
        return afterPlant;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SingleGameManager userGame = ServletUtils.GetGamesManager(getServletContext()).GetGameBySession(request.getSession());
        PostAction postActionForPlayer;

        if (userGame.IsActive() && userGame.GetGameEngine().GetPlayer(0).GetStatics().GetTurnsTaken() != 0) {
            postActionForPlayer = getPostActionDataForWaitingPlayer(request.getSession());
        } else if (userGame.IsActive()) {
            postActionForPlayer = new PostAction();
            postActionForPlayer.SetMoveResult(GameEngine.HitStatus.FIRST_MOVE);
            userGame.GetGameEngine().GetPlayer(0).StartTurnCount();

        } else {
            postActionForPlayer = new PostAction();
            postActionForPlayer.SetNextPlayerIndex(-1);
        }

        try {
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();
            out.println(gson.toJson(postActionForPlayer));
        } catch (Exception e) {
            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            errorManager.AddError(request.getSession(), new Error("Error while making a move", Error.ErrorType.UNEXPECTED));
            response.sendRedirect(".." + ERROR_URL);
        }


    }

    private PostAction getPostActionDataForWaitingPlayer(HttpSession i_UserSession) {
        SingleGameManager userGame = ServletUtils.GetGamesManager(getServletContext()).GetGameBySession(i_UserSession);
        GameEngine userLogic = userGame.GetGameEngine();
        PostAction enemyPostActions = userLogic.GetLastTurnResults();
        PostAction playerPostActions = new PostAction();
        reverseAndUpdatePostActionData(enemyPostActions, playerPostActions, userLogic,
                userGame.GetUserIndexBySession(i_UserSession));
        return playerPostActions;
    }

    private void reverseAndUpdatePostActionData(PostAction i_From, PostAction i_To, GameEngine i_UserLogic, int i_PlayerInd) {

        ShipTypesLeft typesLeft = i_From.GetEnemyTypesLeft();

        if (typesLeft != null) {
            i_To.SetPlayerShipTypesLeft(typesLeft.GetHorizontalShips(), typesLeft.GetVerticalShips(), typesLeft.GetLShapeShips());
            typesLeft = i_From.GetPlayerTypesLeft();
            i_To.SetEnemyShipTypesLeft(typesLeft.GetHorizontalShips(), typesLeft.GetVerticalShips(), typesLeft.GetLShapeShips());
        }
        i_To.SetCharToChangeShipPoints(i_From.GetChangeHitPointsTo());
        i_To.SetCharToChangeHitPointsTo(i_From.GetCharToChangeShipPointsTo());
        i_To.SetPlayerStatics(i_UserLogic.GetPlayer(i_PlayerInd).GetStatics());
        i_To.SetMoveResult(i_From.GetActionsResult());
        i_To.SetNextPlayerIndex(i_From.GetNextTurnPlayerIndex());

        if (i_From.GetActionsResult() != GameEngine.HitStatus.MINE_PLANTED) {
            i_To.SetShipPointsToChange(i_From.GetHitsPointInList());
            i_To.SetHitPointsToChange(i_From.GetShipsPointInList());
        }

        i_To.SetPreviousPlayerIndex(i_From.GetPreviousPlayerIndex());
    }


    private Point getPointFromRequestParameters(HttpServletRequest request) {
        int col = Integer.parseInt(request.getParameter(COLUMN_PARAMETER));
        int row = Integer.parseInt(request.getParameter(ROW_PARAMETER));
        return new Point(row, col);
    }

    private PostAction createUserPostActionResult(GameEngine.HitStatus i_HitStatus, GameEngine i_UserGameEngine, int i_CurrentPlayerIndex,
                                                  Point i_AttackedPoint) {
        PostAction postAction = new PostAction();
        LinkedList<Point> hitPointsToChange = new LinkedList<>();
        GameEngine.HitStatus hitRes = i_HitStatus;
        int nextPlayerIndex = i_CurrentPlayerIndex;
        char mineHitChar;

        if (i_AttackedPoint != null) {
            mineHitChar = i_UserGameEngine.GetPlayer((i_CurrentPlayerIndex + 1) % 2).GetPlayerShipsBoard().GetPointInBoard(i_AttackedPoint);
            if (mineHitChar == 'E' && i_HitStatus == GameEngine.HitStatus.WINNER) {
                hitRes = GameEngine.HitStatus.MINE_HIT;
            }
        }

        switch (hitRes) {
            case SHIP_HIT:
                nextPlayerIndex = i_CurrentPlayerIndex;
                postAction.SetCharToChangeHitPointsTo('X');
                hitPointsToChange.add(i_AttackedPoint);
                postAction.SetHitPointsToChange(hitPointsToChange);
                break;
            case SHIP_DROWNED:
                nextPlayerIndex = i_CurrentPlayerIndex;
                updateHitBoardDestroyedShipsOnPostAction(i_UserGameEngine, i_AttackedPoint, i_CurrentPlayerIndex, postAction);
                i_UserGameEngine.updateShipsRemaining(i_AttackedPoint, i_CurrentPlayerIndex);
                break;
            case MISS:
                nextPlayerIndex = (i_CurrentPlayerIndex + 1) % 2;
                postAction.SetCharToChangeHitPointsTo('O');
                hitPointsToChange.add(i_AttackedPoint);
                postAction.SetHitPointsToChange(hitPointsToChange);
                break;
            case MINE_HIT:
                postAction.SetCharToChangeHitPointsTo('E');
                hitPointsToChange.add(i_AttackedPoint);
                postAction.SetHitPointsToChange(hitPointsToChange);
                nextPlayerIndex = (i_CurrentPlayerIndex + 1) % 2;
                hitRes = checkIfMineHitAnObject(i_UserGameEngine, i_AttackedPoint, i_CurrentPlayerIndex, hitRes, postAction);
                break;
            case SAME_POINT_HIT:
                nextPlayerIndex = i_CurrentPlayerIndex;
            case QUIT:
                break;
            case WINNER:
                updateHitBoardDestroyedShipsOnPostAction(i_UserGameEngine, i_AttackedPoint, i_CurrentPlayerIndex, postAction);
                endPlayerTurn(i_UserGameEngine, i_CurrentPlayerIndex, nextPlayerIndex, hitRes);
                break;
        }
        endPlayerTurn(i_UserGameEngine, i_CurrentPlayerIndex, nextPlayerIndex, hitRes);

        if (hitRes == GameEngine.HitStatus.GAME_ENDED_BY_MINE) {
            hitRes = GameEngine.HitStatus.WINNER;
        }

        postAction.SetMoveResult(hitRes);
        postAction.SetNextPlayerIndex(nextPlayerIndex);
        postAction.SetPreviousPlayerIndex(i_CurrentPlayerIndex);
        postAction.SetPlayerStatics(i_UserGameEngine.GetPlayer(i_CurrentPlayerIndex).GetStatics());
        updateShipsLeftOnPostAction(i_UserGameEngine, i_CurrentPlayerIndex, postAction);
        return postAction;
    }

    private void updateShipsLeftOnPostAction(GameEngine i_Engine, int i_CurrentPlayerInd, PostAction i_Post) {
        PlayerShipsBoard playerToUpdateShipBoard = (PlayerShipsBoard) i_Engine.GetPlayer(i_CurrentPlayerInd).GetPlayerShipsBoard();
        i_Post.SetPlayerShipTypesLeft(playerToUpdateShipBoard.getHorizontalShipsLeft(), playerToUpdateShipBoard.getVerticalShipsLeft(),
                playerToUpdateShipBoard.getLShapeShipsLeft());
        playerToUpdateShipBoard = (PlayerShipsBoard) i_Engine.GetPlayer((i_CurrentPlayerInd + 1) % 2).GetPlayerShipsBoard();
        i_Post.SetEnemyShipTypesLeft(playerToUpdateShipBoard.getHorizontalShipsLeft(), playerToUpdateShipBoard.getVerticalShipsLeft(),
                playerToUpdateShipBoard.getLShapeShipsLeft());
    }

    private void updateHitBoardDestroyedShipsOnPostAction(GameEngine i_UserEngine, Point i_ShipPoint, int i_PlayerIndex, PostAction i_PostAction) {
        List<Point> destroyedShipPoints = i_UserEngine.UpdateDestroyedShip(i_ShipPoint, (i_PlayerIndex + 1) % 2);
        i_PostAction.SetCharToChangeHitPointsTo('D');
        i_PostAction.SetHitPointsToChange(destroyedShipPoints);
    }

    private void updateShipBoardDestroyedShipsOnPostAction(GameEngine i_UserEngine, Point i_ShipPoint, int i_PlayerIndex, PostAction i_PostAction) {
        List<Point> destroyedShipPoints = i_UserEngine.UpdateDestroyedShip(i_ShipPoint, (i_PlayerIndex + 1) % 2);
        i_PostAction.SetCharToChangeShipPoints('D');
        i_PostAction.SetShipPointsToChange(destroyedShipPoints);
    }

    private GameEngine.HitStatus checkIfMineHitAnObject(GameEngine i_UserEngine, Point i_ButtonCoords, int i_CurrentPlayerIndex,
                                                        GameEngine.HitStatus i_CurrentHitStatus, PostAction i_PostAction) {
        GameEngine.HitStatus mineReturnHit = i_CurrentHitStatus;
        char mineReturnHitButton = i_UserEngine.GetPlayer(i_CurrentPlayerIndex).GetPointSymbolInShipBoard(i_ButtonCoords);
        LinkedList<Point> shipPoints = new LinkedList<>();

        if (mineReturnHitButton == 'X') {
            handleMineReflectedAttackOnShip(i_UserEngine, i_ButtonCoords, i_CurrentPlayerIndex, i_PostAction);

            if (i_UserEngine.GetPlayer(i_CurrentPlayerIndex).GetShipsSqauresCount() == 0) {
                mineReturnHit = GameEngine.HitStatus.GAME_ENDED_BY_MINE;
            }
        } else {
            i_PostAction.SetCharToChangeShipPoints(mineReturnHitButton);
            shipPoints.add(i_ButtonCoords);
            i_PostAction.SetShipPointsToChange(shipPoints);
        }

        return mineReturnHit;
    }

    private void handleMineReflectedAttackOnShip(GameEngine i_UserEngine, Point i_Point, int i_CurrentPlayerIndex, PostAction i_PostAction) {
        char currentPlayerReturnHitChar = i_UserEngine.GetPlayer(i_CurrentPlayerIndex).GetPlayerShipsBoard().GetPointInBoard(i_Point);

        if (i_UserEngine.CheckIfShipNeedsToBeDestroyed(i_Point, i_CurrentPlayerIndex)) {
            updateShipBoardDestroyedShipsOnPostAction(i_UserEngine, i_Point, (i_CurrentPlayerIndex + 1) % 2, i_PostAction);
            i_UserEngine.updateShipsRemaining(i_Point, (i_CurrentPlayerIndex + 1) % 2);
        } else {
            i_PostAction.SetCharToChangeShipPoints('X');
            List<Point> pointToChange = new LinkedList<>();
            pointToChange.add(i_Point);
            i_PostAction.SetShipPointsToChange(pointToChange);
        }

    }

    private void endPlayerTurn(GameEngine i_UserEngine, int i_playerWhoPlayed, int i_playerWhoPlaysNext, GameEngine.HitStatus i_MoveRes) {
        if (i_MoveRes != GameEngine.HitStatus.SAME_POINT_HIT) {
            i_UserEngine.SwitchTurnToPlayer(i_playerWhoPlaysNext);
            i_UserEngine.GetPlayer(i_playerWhoPlaysNext).StartTurnCount();
            i_UserEngine.IncreaseTurnCountToPlayer(i_playerWhoPlayed);
        }
    }
}


