package Ex03.GameEngine;
import Ex03.GameEngine.BattleShips.BattleShipGame;

import java.util.HashMap;


public class AdvancedEngine extends GameEngine {

    public AdvancedEngine(BattleShipGame i_BattleShipGame) throws XMLInvalidInputException {
        super(i_BattleShipGame);
    }


//    @Override
//    public HitStatus MakeAMove(Point i_PointToHit) {
//        return null;
//    }

    @Override
    public HitStatus ShipHit(Point i_PointToChange) {
        HitStatus hitRes;
        int currentPlayerInd = GetCurrentPlayerInd();

        Player currentPlayer = GetPlayer(currentPlayerInd);
        Player enemyPlayer = GetPlayer((currentPlayerInd + 1) % k_NumberOfPlayers);
        enemyPlayer.GetPlayerShipsBoard().Draw(i_PointToChange, 'X');
        enemyPlayer.ReduceShipSquareCount();
        currentPlayer.GetPlayerHitsBoard().Draw(i_PointToChange, 'X');
        currentPlayer.GetStatics().IncreaseHits();
        hitRes = handleShipHit(enemyPlayer.GetPointToShipMap(), i_PointToChange, currentPlayer.GetStatics());
        return hitRes;
    }

    private HitStatus handleShipHit(HashMap<Point, Ship> i_PointToShipMap, Point i_PointToHit, PlayerStatics i_PlayerStats) {
        HitStatus hitRes = HitStatus.SHIP_HIT;
        Ship tempShip;
        tempShip = i_PointToShipMap.get(i_PointToHit);
        if (!tempShip.ReduceSingleShipSquareCount()) {
            hitRes = CheckWinner(HitStatus.SHIP_DROWNED);
            i_PlayerStats.IncreaseScoreWhenDrown(i_PointToShipMap.get(i_PointToHit).GetShipPointWorth());
        }
        return hitRes;
    }

    @Override
    protected void handleMineRiposte(char i_RiposteSymbol, int i_AttackerInd, Point i_AttackedPoint) {
        Player attacker = GetPlayer(i_AttackerInd);
        Player attacked = GetPlayer((i_AttackerInd + 1) % k_NumberOfPlayers);
        attacker.GetStatics().IncreaseHits();
        switch (i_RiposteSymbol) {
            case ' ':
                  attacker.GetPlayerShipsBoard().Draw(i_AttackedPoint,'O');
                  attacked.GetPlayerHitsBoard().Draw(i_AttackedPoint,'O');
                  break;
            case 'M':
                attacker.GetPlayerShipsBoard().Draw(i_AttackedPoint,'E');
                attacked.GetPlayerHitsBoard().Draw(i_AttackedPoint, 'E');
            break;
            case 'S':
                attacked.GetPlayerHitsBoard().Draw(i_AttackedPoint, 'X');
                handleShipHit(attacker.GetPointToShipMap(), i_AttackedPoint, attacked.GetStatics());
                attacker.ReduceShipSquareCount();
                attacker.GetPlayerShipsBoard().Draw(i_AttackedPoint, 'X');
                break;
            default:
                break;
        }
        attacker.GetPlayerHitsBoard().Draw(i_AttackedPoint, 'E');
        attacked.GetPlayerShipsBoard().Draw(i_AttackedPoint, 'E');
    }

    @Override
    public String GetGameType(){
        return "ADVANCE";
    }


}