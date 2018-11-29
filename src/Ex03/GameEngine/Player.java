package Ex03.GameEngine;


import Ex03.GameEngine.BattleShips.BattleShipGame;
import jdk.nashorn.internal.objects.annotations.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Player implements Serializable{

    private static final long serialVersionUID = 1L;
    private int m_ShipsSquaresCount;
    private int m_ShipsCount;
    private PlayerShipsBoard m_PlayerShipsBoard;
    private PlayerHitsBoard m_PlayerHitsBoard;
    private int m_minesAmount;
    private PlayerStatics m_PlayerStats;
    private long m_CurrentTurnTime;

    public int GetCurrentMinesCount() {
        return m_minesAmount;
    }

    public Player(BattleShipGame i_battleShipGame, int i_PlayerIndex, HashMap<String, BattleShipGame.ShipTypes.ShipType> i_ShipTypeMap)
            throws  XMLInvalidInputException {
        m_ShipsCount = i_battleShipGame.getShipTypes().getShipType().size();

        if (i_battleShipGame.getMine() != null) {
            m_minesAmount = i_battleShipGame.getMine().getAmount();
        }

        m_CurrentTurnTime = 0;
        m_PlayerShipsBoard = new PlayerShipsBoard(i_battleShipGame,i_PlayerIndex,i_ShipTypeMap);
        m_PlayerHitsBoard = new PlayerHitsBoard(i_battleShipGame.getBoardSize());
        m_ShipsSquaresCount =m_PlayerShipsBoard.GetShipsSquaresCount();
        m_PlayerStats = new PlayerStatics();
    }

    public void ReduceMineCount() {
        m_minesAmount--;
    }


    @Getter
    public Board GetPlayerShipsBoard() {
        return m_PlayerShipsBoard;
    }

    @Getter
    public Board GetPlayerHitsBoard() {
        return m_PlayerHitsBoard;
    }

    public char GetPointSymbolInShipBoard(Point i_PointToCheck) {
        return m_PlayerShipsBoard.GetPointInBoard(i_PointToCheck);
    }

    public PlayerStatics GetStatics() {
        return m_PlayerStats;
    }

    public void StartTurnCount() {
        m_CurrentTurnTime = System.currentTimeMillis();
    }

    public int GetShipsCount() {
        return m_ShipsCount;
    }

    public int GetShipsSqauresCount() {
        return m_ShipsSquaresCount;
    }

    public void ReduceShipSquareCount() {
        m_ShipsSquaresCount--;
    }

    public void EndTurnCount() {
        m_CurrentTurnTime = (System.currentTimeMillis() - m_CurrentTurnTime) / 1000;
        m_PlayerStats.AddToTimePlayed(m_CurrentTurnTime);
        m_CurrentTurnTime = 0;
    }

    public void SetPlayerShipBoard(PlayerShipsBoard playerShipsBoard) {
        m_PlayerShipsBoard=playerShipsBoard;
    }

    public void SetPlayerHitBoard(PlayerHitsBoard playerHitsBoard) {
        m_PlayerHitsBoard=playerHitsBoard;
    }

    public void SetPlayerStats(PlayerStatics playerStatics) {
        m_PlayerStats=playerStatics;
    }

    public HashMap<Point,Ship> GetPointToShipMap(){
        return m_PlayerShipsBoard.GetPointToShipMap();
    }

}