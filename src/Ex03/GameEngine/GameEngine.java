package Ex03.GameEngine;

import Ex03.GameEngine.BattleShips.BattleShipGame;
import jdk.nashorn.internal.objects.annotations.Getter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

import Ex03.GameEngine.BattleShips.BattleShipGame.ShipTypes;
import Ex03.GameEngine.BattleShips.BattleShipGame.Boards.Board.Ship;

public abstract class GameEngine implements Serializable {

    public abstract String GetGameType();

    public enum HitStatus {
        SHIP_HIT, MISS, SAME_POINT_HIT, MINE_HIT, MINE_PLANTED, WINNER, GAME_ENDED_BY_MINE, QUIT, SHIP_DROWNED, FIRST_MOVE
    }

    protected static final long serialVersionUID = 1L;
    public final int k_MaxNumberOfLines = 20;
    public final int k_NumberOfPlayers = 2;
    private int m_LinesInBoard;
    private Player[] m_Players;
    private int m_CurrentPlayer = 0;
    private Long m_StartTime;
    private PostAction m_LastTurnServletResult; // Pre Json- before sended to player need to swap hit and ship points

    private int m_MineGeneralAmount;

    public void SaveLastActionData(PostAction i_PostAction) {
        m_LastTurnServletResult = i_PostAction;
    }

    public PostAction GetLastTurnResults() {
        return m_LastTurnServletResult;
    }

    private transient Map<String, BattleShipGame.ShipTypes.ShipType> m_ShipTypeMap;

    public HitStatus MakeAMove(Point i_PointToHit) {   // Makes an attack and returns result

        HitStatus hitStatus = HitStatus.MISS;    // Just for initialization
        char boardSymbol = GetPlayer((GetCurrentPlayerInd() + 1) % k_NumberOfPlayers).GetPointSymbolInShipBoard(i_PointToHit);

        // On ships board: a square that was hit before =x , a square that has ship = s,
        // On hits board: Ship hit = v , ship miss = O
        switch (boardSymbol) {
            case 'S':
                hitStatus = ShipHit(i_PointToHit);
                break;
            case ' ':
                hitStatus = ShipMiss(i_PointToHit);
                break;
            case 'E':   // Possibilities for attacking a point that already been attacked
            case 'D':
            case 'O':
            case 'X':
                hitStatus = HitStatus.SAME_POINT_HIT;
                break;
            case 'M':
                hitStatus = MineHit(i_PointToHit);
                break;
        }

        if (hitStatus != HitStatus.SAME_POINT_HIT) {
            GetPlayer(GetCurrentPlayerInd()).EndTurnCount();
        }

        return hitStatus;
    }

    public void SetStartTime() {
        m_StartTime = System.currentTimeMillis();
    }

    public long GetTimeFromTheStart() {
        return System.currentTimeMillis() - m_StartTime;
    }

    public LinkedList<Point> UpdateDestroyedShip(Point i_pointToHit, int i_PlayerThatLostShip) {
        PlayerShipsBoard shipsBoard;
        LinkedList<Point> CordsList;
        shipsBoard = (PlayerShipsBoard) GetPlayer(i_PlayerThatLostShip).GetPlayerShipsBoard();
        CordsList = shipsBoard.GetShipByPoint(i_pointToHit).GetShipCoords();
        for (Point itrPoint : CordsList) {
            GetPlayer(i_PlayerThatLostShip).GetPlayerShipsBoard().Draw(itrPoint, 'D');
            GetPlayer((i_PlayerThatLostShip + 1) % 2).GetPlayerHitsBoard().Draw(itrPoint, 'D');

//update the ShipsBoard;
        }
        return CordsList;
    }

    public int GetCurrentPlayerInd() {
        return m_CurrentPlayer;
    }

    public Player GetPlayer(int i_PlayerIndex) {
        return m_Players[i_PlayerIndex];//changed to i)PlayerIndex from i_PlayerIndex % k_NumberOfPlayers
    }

    public GameEngine(BattleShipGame i_BattleShipGame) throws XMLInvalidInputException {

        if (i_BattleShipGame.getBoardSize() > k_MaxNumberOfLines || i_BattleShipGame.getBoardSize() <= 0) {
            throw new XMLInvalidInputException("the board size is invalid. ");
        }
        initializeMembers(i_BattleShipGame);

    }

    private void initializeMembers(BattleShipGame i_BattleShipGame) throws XMLInvalidInputException {
        Dictionary<String, BattleShipGame.ShipTypes.ShipType> shipTypeMap;
        m_Players = new Player[k_NumberOfPlayers];

        if (i_BattleShipGame.getMine() != null) {
            m_MineGeneralAmount = i_BattleShipGame.getMine().getAmount();
        }

        m_LinesInBoard = i_BattleShipGame.getBoardSize();
        m_StartTime = System.currentTimeMillis();
        createShipTypesMap(i_BattleShipGame);
        m_Players[0] = new Player(i_BattleShipGame, 0, (HashMap<String, ShipTypes.ShipType>) m_ShipTypeMap);
        m_Players[1] = new Player(i_BattleShipGame, 1, (HashMap<String, ShipTypes.ShipType>) m_ShipTypeMap);
    }

    private void createShipTypesMap(BattleShipGame i_BattleShipGame) throws XMLInvalidInputException {
        m_ShipTypeMap = new HashMap<String, ShipTypes.ShipType>();
        int shipTypesTotalAmount = 0;

        for (ShipTypes.ShipType shipType : i_BattleShipGame.getShipTypes().getShipType()) {
            m_ShipTypeMap.put(shipType.getId(), shipType);
            shipTypesTotalAmount += shipType.getAmount();
        }
        if (shipTypesTotalAmount == 0) // The XML File contains no ships
        {
            throw new XMLInvalidInputException("The XML file contains no ships.");
        }

    }

    public long GetTimePlayed() {
        return (System.currentTimeMillis() - m_StartTime) / 1000;
    }

    public int GetLinesInBoard() {
        return m_LinesInBoard;
    }

    @Getter
    public Board GetPlayerShipBoard(int i_PlayerInd) {   //For printing purposes only
        return m_Players[i_PlayerInd % k_NumberOfPlayers].GetPlayerShipsBoard();
    }

    @Getter
    public Board GetPlayerHitsBoard(int i_PlayerInd) {   // For printing purposes only
        return m_Players[i_PlayerInd % k_NumberOfPlayers].GetPlayerHitsBoard();
    }

    protected boolean validateLogicInput(Point i_Point) {
        boolean isChordOk = true;
        if ((i_Point.GetX() > m_LinesInBoard || i_Point.GetY() > m_LinesInBoard) || (i_Point.GetX() < 0 || i_Point.GetY() < 0)) {
            isChordOk = !true;
        }
        return isChordOk;
    }

    public void SwitchPlayerTurn() {
        m_CurrentPlayer = (m_CurrentPlayer + 1) % k_NumberOfPlayers;
    }

    public void SwitchTurnToPlayer(int i_ChangeTo) {

        m_CurrentPlayer = i_ChangeTo;
    }

    //    public void IncreaseTurnCount() {
//        m_Players[m_CurrentPlayer].GetStatics().IncreaseTurnCount();
//    }
    public void IncreaseTurnCountToPlayer(int i_PlayerIndex) {
        m_Players[i_PlayerIndex].GetStatics().IncreaseTurnCount();
    }

    public void StartPlayerTurnTime() {
        m_Players[m_CurrentPlayer % k_NumberOfPlayers].StartTurnCount();
    }

    public int GetWinner() {
        int winningPlayer;
        if (m_Players[0].GetShipsSqauresCount() == 0) {
            winningPlayer = 1;
        } else if (m_Players[1].GetShipsSqauresCount() == 0) {
            winningPlayer = 0;
        } else {
            winningPlayer = m_CurrentPlayer;
        }
        return winningPlayer;       //0- player 1 won , 1- player 2 won. draw is not possible
    }

    protected HitStatus CheckWinner(HitStatus i_CurrentHitStatus) {
        HitStatus hitStatus = i_CurrentHitStatus;
        if (m_Players[0].GetShipsSqauresCount() == 0 || m_Players[1].GetShipsSqauresCount() == 0) {
            hitStatus = HitStatus.WINNER;
        }
        return hitStatus;
    }

    public void EndPlayerTurnTime() {
        m_Players[m_CurrentPlayer % k_NumberOfPlayers].EndTurnCount();
    }

    protected abstract HitStatus ShipHit(Point i_PointToChange);

    public static BattleShipGame IntializeBattleShipGameFromXmlFile(String i_XmlFilePath) throws JAXBException, FileNotFoundException {
        File xmlFile = new File(i_XmlFilePath);
        if (!xmlFile.isFile()) {
            throw new FileNotFoundException();
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(BattleShipGame.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        BattleShipGame battleShipGame = (BattleShipGame) jaxbUnmarshaller.unmarshal(xmlFile);
        String gameType = battleShipGame.getGameType();

        if (!(gameType.equals("BASIC") || (gameType.equals("ADVANCE")))) {
            throw new JAXBException("Invalid game type");
        }

        return battleShipGame;
    }

    public Map<String, ShipTypes.ShipType> GetShipTypeMap() throws InvalidPropertiesFormatException {
        return m_ShipTypeMap;
    }

    public static void ValidateXMLData(BattleShipGame i_BattleShipGame) throws XMLInvalidInputException {
        Map<String, Integer> playerOneShipTypesAmount = new HashMap<String, Integer>();
        Map<String, Integer> playerTwoShipTypesAmount = new HashMap<String, Integer>();

        // Insert amounts to ship types
        for (ShipTypes.ShipType shipType : i_BattleShipGame.getShipTypes().getShipType()) {
            playerOneShipTypesAmount.put(shipType.getId(), shipType.getAmount());
            playerTwoShipTypesAmount.put(shipType.getId(), shipType.getAmount());
        }

        adjustPlayerShipTypesMaps(i_BattleShipGame, playerOneShipTypesAmount, 0);
        adjustPlayerShipTypesMaps(i_BattleShipGame, playerTwoShipTypesAmount, 1);

    }

    private static void adjustPlayerShipTypesMaps(BattleShipGame i_BattleShipGame, Map<String, Integer> playerShipTypesAmount,
                                                  int i_PlayerInd) throws XMLInvalidInputException {
        // Player one ships types adjust
        for (Ship ship : i_BattleShipGame.getBoards().getBoard().get(i_PlayerInd).getShip()) {
            playerShipTypesAmount.put(ship.getShipTypeId(), playerShipTypesAmount.get(ship.getShipTypeId()) - 1);
        }
        //Player one checks
        for (Integer shipTypeValue : playerShipTypesAmount.values()) {
            if (shipTypeValue != 0) {
                throw new XMLInvalidInputException("The defined ship types does not match existing ship types in " +
                        "player " + (i_PlayerInd + 1) + " board");
            }
        }
    }

    public boolean PlantAMine(Point i_MineCoord) {
        boolean isCoordEmpty;
        PlayerShipsBoard currentPlayerShipsBoard = (PlayerShipsBoard) GetPlayer(m_CurrentPlayer).GetPlayerShipsBoard();
        isCoordEmpty = currentPlayerShipsBoard.CheckCoordAvailability(i_MineCoord);
        if (isCoordEmpty && GetPlayer(m_CurrentPlayer).GetCurrentMinesCount() > 0) {
            currentPlayerShipsBoard.Draw(i_MineCoord, 'M');
            GetPlayer(GetCurrentPlayerInd()).ReduceMineCount();

        }
        return isCoordEmpty;
    }

    public void SaveGameStateToFile(ObjectOutputStream objectSave) throws Exception {
        try {
            for (Player player : m_Players) {
                objectSave.writeObject(player);
                objectSave.writeObject(player.GetPlayerShipsBoard());
                objectSave.writeObject(player.GetPlayerHitsBoard());
                objectSave.writeObject(player.GetStatics());
            }
        } catch (Exception e) {

        }
    }

    public void LoadGameFromFile(ObjectInputStream objectSave) throws Exception {
        for (Player player : m_Players) {
            player = (Player) objectSave.readObject();
            player.SetPlayerShipBoard((PlayerShipsBoard) objectSave.readObject());
            player.SetPlayerHitBoard((PlayerHitsBoard) objectSave.readObject());
            player.SetPlayerStats((PlayerStatics) objectSave.readObject());
        }
    }

    protected abstract void handleMineRiposte(char i_RiposteSymbol, int i_AttackerInd, Point i_AttackedPoint);

    protected HitStatus MineHit(Point i_PointToChange) {
        int currentPlayerInd = GetCurrentPlayerInd();
        char symbolInMineCoord;
        symbolInMineCoord = GetPlayer((currentPlayerInd) % k_NumberOfPlayers).GetPointSymbolInShipBoard(i_PointToChange);
        handleMineRiposte(symbolInMineCoord, currentPlayerInd, i_PointToChange);
        return CheckWinner(HitStatus.MINE_HIT);
    }

    public HitStatus ShipMiss(Point i_PointToChange) {
        int currentPlayerInd = GetCurrentPlayerInd();
        Player currentPlayer = GetPlayer(currentPlayerInd);
        GetPlayer((currentPlayerInd + 1) % k_NumberOfPlayers).GetPlayerShipsBoard().Draw(i_PointToChange, 'O');
        currentPlayer.GetPlayerHitsBoard().Draw(i_PointToChange, 'O');
        currentPlayer.GetStatics().IncreaseMissCount();
        return HitStatus.MISS;
    }

    public void updateShipsRemaining(Point i_buttonPoint, int i_playerIndex) {
        PlayerShipsBoard ShipBoard = (PlayerShipsBoard) GetPlayerShipBoard((i_playerIndex + 1) % 2);
        String tempDir = ShipBoard.GetShipByPoint(i_buttonPoint).getDirection();
        if (tempDir.matches("ROW")) {
            ShipBoard.DecrementHorizontalShipsLeft();
        } else if (tempDir.matches("COLUMN")) {
            ShipBoard.DecrementVerticalShipsLeft();
        } else {
            ShipBoard.DecrementLShapeShipsLeft();

        }
    }

    public boolean CheckIfShipNeedsToBeDestroyed(Point i_LastCoord, int i_PlayerToCheck) {
        boolean isShipToDestroy = false;
        int destroyedCoords = 0;

        PlayerShipsBoard playerShipBoard;
        LinkedList<Point> cordsList;
        playerShipBoard = (PlayerShipsBoard) GetPlayer(i_PlayerToCheck).GetPlayerShipsBoard();
        cordsList = playerShipBoard.GetShipByPoint(i_LastCoord).GetShipCoords();

        for (Point point : cordsList) {
            if (playerShipBoard.GetPointInBoard(point) == 'X') {
                destroyedCoords++;
            }

            if (destroyedCoords == cordsList.size()) {
                isShipToDestroy = true;
            }
        }

        return isShipToDestroy;
    }
}
