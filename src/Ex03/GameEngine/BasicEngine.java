package Ex03.GameEngine;

import Ex03.GameEngine.BattleShips.BattleShipGame;

public class BasicEngine extends GameEngine {

    public BasicEngine(BattleShipGame i_battleShipGame) throws XMLInvalidInputException {
        super(i_battleShipGame);
    }

    @Override
    public HitStatus ShipHit(Point i_PointToChange) {
        HitStatus hitRes =HitStatus.SHIP_HIT;
        int currentPlayerInd = GetCurrentPlayerInd();
        Player currentPlayer = GetPlayer(currentPlayerInd);
        Player enemyPlayer = GetPlayer((currentPlayerInd + 1) % k_NumberOfPlayers);
        enemyPlayer.GetPlayerShipsBoard().Draw(i_PointToChange, 'X');
        enemyPlayer.ReduceShipSquareCount();
        currentPlayer.GetPlayerHitsBoard().Draw(i_PointToChange, 'X');
        currentPlayer.GetStatics().IncreaseHits();
        Ship tempShip = enemyPlayer.GetPointToShipMap().get(i_PointToChange);
        if (!tempShip.ReduceSingleShipSquareCount()) {
            hitRes = CheckWinner(HitStatus.SHIP_DROWNED);
        }
        return hitRes;
    }

    // AttackerInd= the index of the player who initially attacked the mine
    @Override
    protected void handleMineRiposte(char i_RiposteSymbol, int i_AttackerInd, Point i_AttackedPoint) {
        Player attacker = GetPlayer(i_AttackerInd);
        Player attacked = GetPlayer((i_AttackerInd + 1) % k_NumberOfPlayers);
        attacker.GetStatics().IncreaseHits();
        switch (i_RiposteSymbol) {
            case ' ':
            case 'M':
                attacked.GetPlayerHitsBoard().Draw(i_AttackedPoint, 'O');
                break;
            case 'S':
                attacked.GetPlayerHitsBoard().Draw(i_AttackedPoint, 'X');
                attacked.GetStatics().IncreaseScoreWhenHit();
                attacker.ReduceShipSquareCount();
                attacker.GetPlayerShipsBoard().Draw(i_AttackedPoint, 'X');
                break;
            default:
                break;
        }
        attacker.GetPlayerHitsBoard().Draw(i_AttackedPoint, 'X');
        attacked.GetPlayerShipsBoard().Draw(i_AttackedPoint, 'X');
    }

    @Override
    public String GetGameType() {
        return "BASIC";
    }
}
