package Ex03.GameEngine;

import jdk.nashorn.internal.objects.annotations.Getter;

import java.io.Serializable;

public abstract class Board implements Serializable {
    protected static final long serialVersionUID = 1L;
    private char[][] m_Board;
    private int m_sizeOfBoard;

    public Board(int i_sizeOfBoard) {
        m_Board = new char[i_sizeOfBoard][i_sizeOfBoard];
        m_sizeOfBoard = i_sizeOfBoard;
        for (int i = 0; i < i_sizeOfBoard; i++) {
            for (int j = 0; j < i_sizeOfBoard; j++) {
                Draw(new Point(i, j), ' ');
            }
        }
    }

    public char GetPointInBoard(Point i_PointToHit)//return value???
    {
        return m_Board[i_PointToHit.GetX()][i_PointToHit.GetY()];
    }


    @Getter
    public char[][] GetBoard() {
        return m_Board;
    }

    @Getter
    public char[] GetBoardLine(int i_BoardLine) {
        return m_Board[i_BoardLine];
    }

    public void Draw(Point i_point, char i_char) {
        m_Board[i_point.GetX()][i_point.GetY()] = i_char;

    }


    public int GetSizeOfBoard() {
        return m_sizeOfBoard;
    }


}

