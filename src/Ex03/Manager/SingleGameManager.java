package Ex03.Manager;

import Ex03.GameEngine.GameEngine;

import javax.servlet.http.HttpSession;

public class SingleGameManager {
    private final int k_NumberOfPlayers = 2;

    private GameEngine m_GameEngine;
    private HttpSession[] m_PlayersSession;
    private boolean m_IsGameActive;
    private String m_GameName;


    public SingleGameManager(GameEngine i_GameEngine, String i_GameName, HttpSession i_FirstPlayerSession) {
        m_GameEngine = i_GameEngine;
        m_PlayersSession = new HttpSession[k_NumberOfPlayers];
        m_PlayersSession[0] = i_FirstPlayerSession;
        m_PlayersSession[1] = null;
        m_IsGameActive = false;
        m_GameName = i_GameName;
    }

    public String GetGameName() {
        return m_GameName;
    }

    public int GetUserIndexBySession(HttpSession i_UserSession) {
        int userIndex = 0;

        if (m_PlayersSession[0] == i_UserSession) {
            userIndex = 0;
        } else if (m_PlayersSession[1] == i_UserSession) {
            userIndex = 1;
        }

        return userIndex;
    }

    public GameEngine GetGameEngine() {
        return m_GameEngine;
    }

    // Returns: true if there is 2 users logged in to the game
    public boolean AddUserToGame(HttpSession i_UserToAdd) throws Exception {


        if (m_PlayersSession[0] == null) {
            m_PlayersSession[0] = i_UserToAdd;
        } else if (m_PlayersSession[1] == null) {
            m_PlayersSession[1] = i_UserToAdd;
            m_IsGameActive = true;
        } else {
            throw new Exception("Game is already full");
        }


        return m_IsGameActive;
    }

    public HttpSession GetPlayerSessionByIndex(int i_PlayerIndex) {
        return m_PlayersSession[i_PlayerIndex];
    }

    public int GetNumberOfWaitingUsers() {
        int waitingUsers = 0;

        for (HttpSession session : m_PlayersSession) {
            if (session != null) {
                waitingUsers++;
            }
        }
        return waitingUsers;
    }

    public boolean IsActive() {
        return m_IsGameActive;
    }

    public void SetIsActive(boolean res) {
        m_IsGameActive = res;
    }

    public HttpSession[] GetUsersSessions() {
        return m_PlayersSession;
    }

}
