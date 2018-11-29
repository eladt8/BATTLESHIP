package Ex03.Utils;

public class RoomInitializeInfo {

    private String playerName;
    private int playerIndx;
    private int boardSize;
    private int mineAmount;
    private char[][] shipsBoard;
    private String gameType;
    private String roomName;

    public RoomInitializeInfo(String playerName, int playerIndx, int boardSize, int mineAmount, char[][] shipsBoard,
                              String i_GameType, String i_RoomName) {
        this.boardSize = boardSize;
        this.mineAmount = mineAmount;
        this.shipsBoard = shipsBoard;
        this.playerIndx = playerIndx;
        this.playerName = playerName;
        gameType = i_GameType;
        roomName = i_RoomName;
    }
}
