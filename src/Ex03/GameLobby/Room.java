package Ex03.GameLobby;

import Ex03.GameEngine.AdvancedEngine;
import Ex03.GameEngine.BasicEngine;
import Ex03.GameEngine.BattleShips.BattleShipGame;
import Ex03.GameEngine.GameEngine;

import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;

// A lobby game room
public class Room {

    private String m_Name;
    private int m_BoardSize;
    private String m_Uploader;
    private GameEngine m_GameLogic;
    private List<HttpSession> m_WaitingList;
    private BattleShipGame m_RoomsBattleShipsData;    //Relevant for the room restart
    private boolean m_IsActive;
    private String m_GameType;

    public Room(String i_RoomName, String i_Uploader, int i_BoardSize, GameEngine i_Engine,
                BattleShipGame i_RoomsBattleShipData) {
        m_BoardSize = i_BoardSize;
        m_Name = i_RoomName;
        m_Uploader = i_Uploader;
        m_WaitingList = new LinkedList<>();
        m_GameLogic = i_Engine;
        m_IsActive = false;
        m_RoomsBattleShipsData = i_RoomsBattleShipData;
        m_GameType = i_Engine.GetGameType();
    }

    //Returns true if now 2 users joined the game
    public boolean AddUserToWaitList(HttpSession i_UserWaitingSession) {
        boolean isGameReady = false;

        m_WaitingList.add(i_UserWaitingSession);

        if (m_WaitingList.size() == 2) {
            isGameReady = true;
        }

        return isGameReady;
    }

    public int GetNumberOfUsersInTheRoom() {
        int res = 0;
        for (HttpSession player : m_WaitingList) {
            if (player != null)
                res++;
        }
        return res;
    }

    public void RemoveUserFromWaitList(HttpSession i_player) {

        m_WaitingList.remove(i_player);

    }

    public String GetRoomName() {
        return m_Name;
    }

    public GameEngine GetRoomLogic() {
        return m_GameLogic;
    }

    public String GetUploaderRoomName() {
        return m_Uploader;
    }

    public void RestartEngine() {
        try {
            if (m_GameLogic instanceof BasicEngine) {
                m_GameLogic = new BasicEngine(m_RoomsBattleShipsData);
            } else {
                m_GameLogic = new AdvancedEngine(m_RoomsBattleShipsData);
            }
        } catch (Exception e) {

        }
    }
}
