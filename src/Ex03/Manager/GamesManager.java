package Ex03.Manager;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

// Manages active game in the system
public class GamesManager {
    private Map<HttpSession, SingleGameManager> m_SessionToGame;
    private Map<String, SingleGameManager> m_GameNameToGame;
    private LinkedList<SingleGameManager> m_AllGames;

    public GamesManager() {
        m_AllGames = new LinkedList<>();
        m_GameNameToGame = new HashMap<>();
        m_SessionToGame = new HashMap<>();
    }

    public void AddGame(SingleGameManager i_GameToAdd) {
        m_AllGames.add(i_GameToAdd);
        HttpSession userSession = i_GameToAdd.GetPlayerSessionByIndex(0);
        m_SessionToGame.put(userSession, i_GameToAdd);
        m_GameNameToGame.put(i_GameToAdd.GetGameName(), i_GameToAdd);

        userSession = i_GameToAdd.GetPlayerSessionByIndex(1);

        if (userSession != null) {
            m_SessionToGame.put(userSession, i_GameToAdd);
            m_GameNameToGame.put(i_GameToAdd.GetGameName(), i_GameToAdd);
        }
    }

    public SingleGameManager GetGameByName(String i_GameName) {
        return m_GameNameToGame.get(i_GameName);
    }

    public SingleGameManager GetGameBySession(HttpSession i_UserSession) {
        return m_SessionToGame.get(i_UserSession);
    }


    public void RemoveGame(SingleGameManager i_Game) {
        m_AllGames.remove(i_Game);
        removeGameFromHashMaps(i_Game);
    }

    public void AddSessionAndGame(HttpSession i_UserSession,SingleGameManager i_Game){
        m_SessionToGame.put(i_UserSession,i_Game);
    }

    private void removeGameFromHashMaps(SingleGameManager i_Game) {
        String gameName = i_Game.GetGameName();

        if(m_GameNameToGame.containsKey(gameName))
        {
            m_GameNameToGame.remove(gameName);
        }

        HttpSession userSession = i_Game.GetPlayerSessionByIndex(0);

        if (userSession!=null && m_SessionToGame.containsKey(userSession)){
            m_SessionToGame.remove(userSession);
        }

        userSession=i_Game.GetPlayerSessionByIndex(1);

        if (userSession!=null && m_SessionToGame.containsKey(userSession)){
            m_SessionToGame.remove(userSession);
        }
    }
}