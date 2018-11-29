package Ex03.GameEngine;

public class ShipTypesLeft {
    private int m_HorizontalShips;
    private int m_VerticalShips;
    private int m_LShapeShpis;

    public int GetHorizontalShips() {
        return m_HorizontalShips;
    }

    public int GetVerticalShips() {
        return m_VerticalShips;
    }

    public int GetLShapeShips() {
        return m_LShapeShpis;
    }

    public ShipTypesLeft(int i_Hor, int i_Ver, int i_LShape) {
        m_HorizontalShips = i_Hor;
        m_VerticalShips = i_Ver;
        m_LShapeShpis = i_LShape;
    }

}
