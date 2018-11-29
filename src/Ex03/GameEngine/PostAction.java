package Ex03.GameEngine;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PostAction {
    private char m_ChangeShipPointsTo;
    private char m_ChangeHitPointsTo;
    private GameEngine.HitStatus m_ActionsResult;
    private Set<Point> m_HitPointsToChange;
    private Set<Point> m_ShipPointsToChange;
    private PlayerStatics m_Statics;
    private int m_NextTurnPlayerIndex;
    private int m_PreviousTurnPlayerIndex;
    private ShipTypesLeft m_PlayerTypesLeft;    //THOSE CREATED ONLY WITH THE SET METHOD!!! CARE!!!!
    private ShipTypesLeft m_EnemyTypesLeft;     //!!!!!!!


    public GameEngine.HitStatus GetActionsResult() {
        return m_ActionsResult;
    }

    public PostAction() {
        m_ChangeShipPointsTo = ' ';
        m_HitPointsToChange = new HashSet<>();
        m_ShipPointsToChange = new HashSet<>();
    }

    public int GetPreviousPlayerIndex() {
        return m_PreviousTurnPlayerIndex;
    }

    public void SetPlayerShipTypesLeft(int i_Hor, int i_Ver, int i_LShape) {
        m_PlayerTypesLeft = new ShipTypesLeft(i_Hor, i_Ver, i_LShape);
    }

    public void SetEnemyShipTypesLeft(int i_Hor, int i_Ver, int i_LShape) {
        m_EnemyTypesLeft = new ShipTypesLeft(i_Hor, i_Ver, i_LShape);
    }

    public char GetCharToChangeShipPointsTo() {
        return m_ChangeShipPointsTo;
    }

    public void SetCharToChangeShipPoints(char i_CharToChangeShipPointsTo) {
        this.m_ChangeShipPointsTo = i_CharToChangeShipPointsTo;
    }

    public char GetCharToChangeHitPointsTo() {
        return m_ChangeHitPointsTo;
    }

    public void SetCharToChangeHitPointsTo(char i_CharToChangeHitPointsTo) {
        this.m_ChangeHitPointsTo = i_CharToChangeHitPointsTo;
    }

    public Set<Point> GetPointsToChange() {
        return m_HitPointsToChange;
    }

    public void SetShipPointsToChange(List<Point> i_PointsToChange) {
        copyPoints(i_PointsToChange, (HashSet<Point>) m_ShipPointsToChange);
    }

    public void SetHitPointsToChange(List<Point> i_HitPointsToChange) {
        copyPoints(i_HitPointsToChange, (HashSet<Point>) m_HitPointsToChange);
    }

    private void copyPoints(List<Point> i_PointsToCopy, HashSet<Point> i_CopyTo) {
        i_CopyTo.addAll(i_PointsToCopy);

    }

    public void SetMoveResult(GameEngine.HitStatus i_HitRes) {
        m_ActionsResult = i_HitRes;
    }

    public void SetNextPlayerIndex(int nextPlayerIndex) {
        m_NextTurnPlayerIndex = nextPlayerIndex;
    }

    public void SetPreviousPlayerIndex(int i_PrevPlayerIndex) {
        m_PreviousTurnPlayerIndex = i_PrevPlayerIndex;
    }

    public void SetPlayerStatics(PlayerStatics i_Stats) {
        m_Statics
                = i_Stats.clone();
    }

    public char GetChangeShipPointsTo() {
        return m_ChangeShipPointsTo;
    }

    public char GetChangeHitPointsTo() {
        return m_ChangeHitPointsTo;
    }

    public Set<Point> GetHitPointsToChange() {
        return m_HitPointsToChange;
    }

    public Set<Point> GetShipPointsToChange() {
        return m_ShipPointsToChange;
    }

    public int GetNextTurnPlayerIndex() {
        return m_NextTurnPlayerIndex;
    }

    public ShipTypesLeft GetPlayerTypesLeft() {
        return m_PlayerTypesLeft;
    }

    public ShipTypesLeft GetEnemyTypesLeft() {
        return m_EnemyTypesLeft;
    }

    public List<Point> GetShipsPointInList() {
        LinkedList<Point> shipsPoint = new LinkedList<>();
        shipsPoint.addAll(m_ShipPointsToChange);
        return shipsPoint;
    }

    public List<Point> GetHitsPointInList() {
        LinkedList<Point> hitsPoint = new LinkedList<>();
        hitsPoint.addAll(m_HitPointsToChange);
        return hitsPoint;
    }
}
