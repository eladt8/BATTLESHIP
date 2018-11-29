package Ex03.GameEngine;


import Ex03.GameEngine.BattleShips.BattleShipGame;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Ex03.GameEngine.BattleShips.BattleShipGame.ShipTypes;

public class PlayerShipsBoard extends Board {

    private List<Ship> m_shipsList;
    private HashMap<Point, Ship> m_PointToShipMap;
    private int m_horizontalShipsLeft = 0;
    private int m_verticalShipsLeft = 0;
    private int m_LShapeShipsLeft = 0;

    public int getHorizontalShipsLeft() {
        return m_horizontalShipsLeft;
    }

    public int getVerticalShipsLeft() {
        return m_verticalShipsLeft;
    }

    public int getLShapeShipsLeft() {
        return m_LShapeShipsLeft;
    }


    public HashMap<Point, Ship> GetPointToShipMap() {
        return m_PointToShipMap;
    }

    public PlayerShipsBoard(BattleShipGame i_battleShipGame, int i_PlayerIndex, HashMap<String, ShipTypes.ShipType> i_ShipTypeMap)
            throws XMLInvalidInputException {
        super(i_battleShipGame.getBoardSize());
        m_shipsList = new LinkedList<Ship>();
        m_PointToShipMap = new HashMap<Point, Ship>();
        updateShipList(i_battleShipGame.getBoards().getBoard().get(i_PlayerIndex).getShip(), i_battleShipGame, i_ShipTypeMap);
        if (checkShipsLocations()) {
            updateShipsOnBoardAndMap();
        }

    }

    public Ship GetShipByPoint(Point i_ShipPoint) {
        return m_PointToShipMap.get(i_ShipPoint);
    }

    private Boolean checkShipsLocations() throws XMLInvalidInputException {
        boolean isGoodLocation = true;

            for (Ship ItrShip_A : m_shipsList) {
                for (Ship ItrShip_B : m_shipsList) {
                    if (!ItrShip_A.equals(ItrShip_B) && isGoodLocation) {
                        if (!checkTwoShipsValidation(ItrShip_A, ItrShip_B)) {
                            isGoodLocation = false;
                        }
                    }
                }
            }
        return isGoodLocation;
    }

    private boolean checkTwoShipsValidation(Ship i_Ship_A, Ship i_Ship_B) throws XMLInvalidInputException {
        boolean isGoodLocation = true;
        for (Point ItrPoint_A : i_Ship_A.GetShipCoords()) {
            for (Point ItrPoint_B : i_Ship_B.GetShipCoords()) {
                if (!checkValidPointNighborhood(ItrPoint_A, ItrPoint_B)) {
                    isGoodLocation = false;
                    throw new XMLInvalidInputException("Illeagal ship placement at: X: " + ItrPoint_A.GetX() +
                            ", Y: " + ItrPoint_A.GetY());
                }

            }
        }
        return isGoodLocation;
    }

    private boolean checkValidPointNighborhood(Point i_Point_a, Point i_Point_b) {
        return ((Math.abs(i_Point_a.GetX() - i_Point_b.GetX()) > 1) || (Math.abs(i_Point_a.GetY() - i_Point_b.GetY()) > 1));
    }

    private void updateShipList(List<BattleShipGame.Boards.Board.Ship> i_ShipList, BattleShipGame i_battleShipGame,
                                HashMap<String, ShipTypes.ShipType> i_ShipTypeMap) {
        for (BattleShipGame.Boards.Board.Ship ShipItr : i_ShipList) {
            m_shipsList.add(new Ship(i_ShipTypeMap.get(ShipItr.getShipTypeId()).getLength(), new Point(ShipItr.getPosition().getX() - 1,
                    ShipItr.getPosition().getY() - 1), ShipItr.getDirection()
                    , i_ShipTypeMap.get(ShipItr.getShipTypeId()).getScore()));

            if (ShipItr.getDirection().equals("ROW")) {
                m_horizontalShipsLeft++;
            } else if (ShipItr.getDirection().equals("COLUMN")) {
                m_verticalShipsLeft++;
            } else {
                m_LShapeShipsLeft++;
            }

        }
    }

    private void updateShipsOnBoardAndMap() {
        for (Ship ItrShip : m_shipsList) {
            for (Point ItrPoint : ItrShip.GetShipCoords()) {
                Draw(ItrPoint, 'S');
                m_PointToShipMap.put(ItrPoint, ItrShip);
            }
        }
    }

    public int GetShipsSquaresCount() {
        int shipSquaresCount = 0;

        for (Ship i_CurrentShip : m_shipsList) {
            shipSquaresCount += i_CurrentShip.GetShipCoords().size();
        }
        return shipSquaresCount;
    }

//    public Ship GetShipFromShipList(Point i_PointInShip) {
//        Ship shipRes = null;
//        for (Ship ItrShip : m_shipsList) {
//            for (Point ItrPoint : ItrShip.GetShipCoords()) {
//                if (ItrPoint.GetX() == i_PointInShip.GetX() && ItrPoint.GetY() == i_PointInShip.GetY()) {
//                    shipRes = ItrShip;
//                }
//            }
//        }
//        return shipRes;
//    }

    public boolean CheckCoordAvailability(Point i_PointToCheck) {
        boolean checkResult = true;

        //  for (Ship ItrShip_A : m_shipsList) {
        for (Point ItrPoint_A : m_PointToShipMap.keySet()) {//optional to do foreach on the shipList
            if (!(checkValidPointNighborhood(i_PointToCheck, ItrPoint_A) && GetPointInBoard(i_PointToCheck) == ' ')) {
                checkResult = false;
                break;
            }
        }

        return checkResult;
    }

    public void DecrementHorizontalShipsLeft() {
        if (m_horizontalShipsLeft > 0) {
            m_horizontalShipsLeft--;
        }
    }

    public void DecrementVerticalShipsLeft() {
        if (m_verticalShipsLeft > 0) {
            m_verticalShipsLeft--;
        }
    }

    public void DecrementLShapeShipsLeft() {
        if (m_LShapeShipsLeft > 0) {
            m_LShapeShipsLeft--;
        }
    }
}