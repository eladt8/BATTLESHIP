package Ex03.GameEngine;


import java.io.Serializable;
import java.util.LinkedList;

public class Ship implements Serializable {
    private static final long serialVersionUID = 1L;
    private LinkedList<Point> m_Coords;
    private int m_ShipPointWorth;
    private int m_ShipPointSize;
    private boolean m_IsAlive;

    public String getDirection() {
        return m_Direction;
    }

    private String m_Direction;

    public Ship(int i_ShipSize, Point i_StartingCoord, String i_ShipDirection, int i_PointWorth) {
        m_Coords = new LinkedList<Point>();
        m_ShipPointWorth = i_PointWorth;
        m_IsAlive = true;
        m_Direction = i_ShipDirection;
        calculateShipCoords(i_StartingCoord, i_ShipDirection, i_ShipSize);
    }

    private void calculateShipCoords(Point i_StartingCoord, String i_ShipDirection, int i_ShipSize) {
        if (i_ShipDirection.equals("ROW")) {
            insertRowShipCoords(i_StartingCoord, i_ShipSize);
        } else if (i_ShipDirection.equals("COLUMN")) {
            insertColumnShipCoords(i_StartingCoord, i_ShipSize);
        } else {
            insertLShapeShipCoords(i_StartingCoord, i_ShipDirection, i_ShipSize);
        }
        m_ShipPointSize = m_Coords.size();
    }

    public boolean ReduceSingleShipSquareCount() {   // Returns true if no points left for the ship
        boolean isDestroyed = false;

        m_ShipPointSize--;

        if (m_ShipPointSize == 0) {
            m_IsAlive = !true;
        }

        return m_IsAlive;
    }

    private void insertLShapeShipCoords(Point i_StartingCoord, String i_ShipDirection, int i_ShipSize) {
        Point nextShipPart = new Point();
        nextShipPart.SetX(i_StartingCoord.GetX());
        nextShipPart.SetY(i_StartingCoord.GetY());

        if (i_ShipDirection.equals("DOWN_RIGHT")) {
            nextShipPart.SetX(i_StartingCoord.GetX() - i_ShipSize);
            insertColumnShipCoords(nextShipPart, i_ShipSize);
            nextShipPart.SetX(i_StartingCoord.GetX());
            nextShipPart.SetY(i_StartingCoord.GetY() + 1);
            insertRowShipCoords(nextShipPart, i_ShipSize - 1);
        } else if (i_ShipDirection.equals("RIGHT_UP")) {
            nextShipPart.SetY(i_StartingCoord.GetY() - i_ShipSize);
            insertRowShipCoords(nextShipPart, i_ShipSize);
            nextShipPart.SetY(i_StartingCoord.GetY());
            nextShipPart.SetX(i_StartingCoord.GetX() - i_ShipSize);
            insertColumnShipCoords(nextShipPart, i_ShipSize - 1);
        } else if (i_ShipDirection.equals("UP_RIGHT")) {
            insertRowShipCoords(i_StartingCoord, i_ShipSize);
            nextShipPart.SetX(i_StartingCoord.GetX() + 1);
            insertColumnShipCoords(nextShipPart, i_ShipSize - 1);
        } else if (i_ShipDirection.equals("RIGHT_DOWN")) {
            nextShipPart.SetY(i_StartingCoord.GetY() - i_ShipSize);
            insertRowShipCoords(nextShipPart, i_ShipSize);
            nextShipPart.SetX(i_StartingCoord.GetX() + 1);
            nextShipPart.SetY(i_StartingCoord.GetY());
            insertColumnShipCoords(nextShipPart, i_ShipSize - 1);
        }
    }

    private void insertRowShipCoords(Point i_StartCoord, int i_ShipSize) {
        for (int i = 0; i < i_ShipSize; i++) {
            addToShipCoords(new Point(i_StartCoord.GetX(), i_StartCoord.GetY() + i));
        }
    }

    private void insertColumnShipCoords(Point i_StartCoord, int i_ShipSize) {
        for (int i = 0; i < i_ShipSize; i++) {
            addToShipCoords(new Point(i_StartCoord.GetX() + i, i_StartCoord.GetY()));
        }
    }

    private void addToShipCoords(Point i_PointToAdd) {
        m_Coords.add(i_PointToAdd);
    }

    public int GetShipPointWorth() {
        return m_ShipPointWorth;
    }

    public LinkedList<Point> GetShipCoords() {
        return m_Coords;
    }

}