package Ex03.GameEngine;


import java.io.Serializable;

public class Point implements Serializable{
    private static final long serialVersionUID = 1L;
    private int m_X;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point point = (Point) o;

        if (m_X != point.GetX()) return false;
        return m_Y == point.GetY();
    }

    @Override
    public int hashCode() {
        int result = m_X;
        result = 31 * result + m_Y;
        return result;
    }

    private int m_Y;

    public int GetX() {
        return m_X;
    }

    public int GetY() {
        return m_Y;
    }

    public void SetY(int y) {
        this.m_Y = y;
    }
    public void SetX(int x) {
        this.m_X = x;
    }

    public Point(int x, int y) {
        m_X = x;
        m_Y = y;
    }
    public Point(){}
}
