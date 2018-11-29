package Ex03.Servlets;

import java.io.*;
import java.util.Collection;
import java.util.Scanner;

import Ex03.Constants.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import Ex03.GameEngine.AdvancedEngine;
import Ex03.GameEngine.BasicEngine;
import Ex03.GameEngine.BattleShips.BattleShipGame;
import Ex03.GameEngine.GameEngine;
import Ex03.GameEngine.XMLInvalidInputException;
import Ex03.GameLobby.Room;
import Ex03.Manager.ErrorManager;
import Ex03.Utils.Error;
import Ex03.Utils.ServletUtils;

// Handles uploaded game file and its validation
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class GameUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(Constants.GAME_LOBBY_URL);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String errorString = null;
        Error.ErrorType errorType = Error.ErrorType.UNEXPECTED;

        try {
            Collection<Part> requestParts = request.getParts();
            String roomName = request.getParameter("roomName");
            String uploaderName = Ex03.Utils.ServletUtils.getUserManager(getServletContext()).GetUserNameBySession(request.getSession());

            if (ServletUtils.GetLobbyRoomManager(getServletContext()).IsRoomExist(roomName)) {
                errorString = "Room name is already used";
                errorType = Error.ErrorType.ROOM_NAME_USED;
                throw new Exception(errorString);
            } else if (roomName == null) {
                errorString = "Room name is invalid";
                throw new Exception(errorString);
            } else if (uploaderName == null) {
                errorString = "An unexpected error occured";
                throw new Exception(errorString);
            } else {
                String xmlFileContent = appendFileParts(requestParts);
                String xmlFilePath = makeXMLFile(xmlFileContent);
                BattleShipGame battleShipGame = GameEngine.IntializeBattleShipGameFromXmlFile(xmlFilePath);
                GameEngine uploadedFileGame = createEngine(battleShipGame);
                Room currentRoom = new Room(roomName, uploaderName, battleShipGame.getBoardSize(), uploadedFileGame, battleShipGame);
                ServletUtils.GetLobbyRoomManager(getServletContext()).AddRoom(currentRoom);
            }

            response.sendRedirect(".." + Constants.GAME_LOBBY_URL);
        } catch (Exception e) {
            if (errorString == null) {
                errorString = e.getMessage();
            }
            if (e instanceof XMLInvalidInputException) {
                errorType = Error.ErrorType.FILE_UPLOAD;
            }

            ErrorManager errorManager = Ex03.Utils.ServletUtils.GetErrorManager(getServletContext());
            errorManager.AddError(request.getSession(), new Error(errorString, errorType));
            response.sendRedirect(".." + Constants.ERROR_URL);
        }
    }

    private GameEngine createEngine(BattleShipGame i_BattleShipGame) throws XMLInvalidInputException {
        GameEngine createdGame = null;

        if (i_BattleShipGame.getGameType().equals(Constants.BASIC_TYPE)) {
            createdGame = new BasicEngine(i_BattleShipGame);

        } else if (i_BattleShipGame.getGameType().equals(Constants.ADVANCE_TYPE)) {
            createdGame = new AdvancedEngine(i_BattleShipGame);
        } else {
            throw new XMLInvalidInputException("Error in uploaded file");
        }

        return createdGame;
    }

    private String appendFileParts(Collection<Part> i_FileParts) throws IOException {
        StringBuilder fileContent = new StringBuilder();

        for (Part part : i_FileParts) {

            //to write the content of the file to a string
            fileContent.append(readFromInputStream(part.getInputStream()));
        }

        return fileContent.toString();
    }

    private static String makeXMLFile(String file) throws FileNotFoundException {
        String xmlData = file.replaceAll("[^\\x20-\\x7e]", ""); //Replaces invalid chars
        String xmlDataProcessed = xmlData.substring(0, xmlData.lastIndexOf('>') + 1);

        try {
            File outputFile = new File("gameFile.xml");
            String filePath = outputFile.getAbsolutePath();
            PrintStream outStream = new PrintStream(new FileOutputStream("gameFile.xml"));
            outStream.print(xmlDataProcessed);

            return filePath;
        } catch (FileNotFoundException ex) {
            throw ex;
        }

    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

}
