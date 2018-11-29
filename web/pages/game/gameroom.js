var cellSize = 0;
var boardSize = 1;
var MineAmount = 0;
var thisPlayerIndex;
var currentTurnPlayerIndex = -1;
var updatePlayerRefreshRate = 1000;//mil seconds
var updateInterval;
var totalSeconds = 0;

function updateHitsBoardByPoints(i_HitBoardUpdates, i_newChar) {
    var table = document.getElementById("HitsBoard");
    var cellToUpdate;
    var rowOfCell;
    for (i = 0; i < i_HitBoardUpdates.length; i++) {
        var pointX = i_HitBoardUpdates[i].m_X;
        var pointy = i_HitBoardUpdates[i].m_Y;
        rowOfCell = table.getElementsByTagName('tr')[pointX];
        cellToUpdate = rowOfCell.getElementsByTagName('td')[pointy];
        updateCellImage(cellToUpdate, i_newChar);
        if (cellToUpdate != undefined) {
            cellToUpdate.disable = true;
        }
    }
}

function updateShipBoardByPoints(i_shipBoardUpdates, i_newChar) {
    var table = document.getElementById("ShipsBoard");
    var cellToUpdate;
    var rowOfCell;
    for (i = 0; i < i_shipBoardUpdates.length; i++) {
        var pointX = i_shipBoardUpdates[i].m_X;
        var pointy = i_shipBoardUpdates[i].m_Y;
        rowOfCell = table.getElementsByTagName('tr')[pointX];
        cellToUpdate = rowOfCell.getElementsByTagName('td')[pointy];
        updateCellImage(cellToUpdate, i_newChar);
    }
}

function updatePlayerStatics(i_statics) {
    if (i_statics != undefined) {
        document.getElementById("fillScore").innerText = i_statics.m_Score.toString();
        document.getElementById("fillHits").innerText = i_statics.m_Hits.toString();
        document.getElementById("fillMiss").innerText = i_statics.m_Misses.toString();
        var avarageTurnTime = i_statics.m_TotalTimePlayed;

        if (i_statics.m_TurnsTaken != 0) {
            avarageTurnTime = Math.round((i_statics.m_TotalTimePlayed / i_statics.m_TurnsTaken));
        }

        document.getElementById("fillAvTurnTime").innerText = avarageTurnTime.toString() + " seconds";
        document.getElementById("fillTurnsPlayed").innerText = i_statics.m_TurnsTaken.toString();
    }
}

function updateShipsLeft(i_PlayerShipLeft, i_EnemyShipsLeft) {
    if (i_PlayerShipLeft != undefined && i_EnemyShipsLeft != undefined) {
        document.getElementById("fillHorizontal").innerText = i_PlayerShipLeft.m_HorizontalShips.toString();
        document.getElementById("fillVertical").innerText = i_PlayerShipLeft.m_VerticalShips.toString();
        document.getElementById("fillLShape").innerText = i_PlayerShipLeft.m_LShapeShpis.toString();
        document.getElementById("fillHorizontalEnemy").innerText = i_EnemyShipsLeft.m_HorizontalShips.toString();
        document.getElementById("fillVerticalEnemy").innerText = i_EnemyShipsLeft.m_VerticalShips.toString();
        document.getElementById("fillLShapeEnemy").innerText = i_EnemyShipsLeft.m_LShapeShpis.toString();
    }
}

function updatePlayerTurnMessage() {
    var playerTurn = document.getElementById("PlayerTurn");
    var indexToNumber = (currentTurnPlayerIndex + 1);
    playerTurn.innerText = "this is player " + indexToNumber.toString() + " turn";
    // var header = document.getElementById("HeadDiv")
    if (currentTurnPlayerIndex == "-1") {
        playerTurn.innerText = "waiting to second player"
    }
    else if (currentTurnPlayerIndex == thisPlayerIndex) {
        playerTurn.style.color = "#28ff2b"
    }
    else {
        playerTurn.style.color = "#ff1e08"
    }
}

// Sends an ajax call to perform end of game engine actions
function ajaxEndOfGame() {
    $.ajax({
        url: "../../gameactions/endofgame",
        type: "POST",
        success: function (enemystatics) {
            showEnemyStatics(enemystatics);
            setTimeout(function () {
                window.location.href = "../lobby/gamelobby.html";
            }, 7000);
        }
    });
}

function endOfGameMessage(hitStatusString, i_LastPlayer) {
    var PlayerNumber = (i_LastPlayer + 1);
    var playerNumberString = PlayerNumber.toString();
    var stringToShow;
    var playerTurnHtML = document.getElementById("PlayerTurn");
    var colorString;
    switch (hitStatusString) {
        case("WINNER"):
            if (currentTurnPlayerIndex == thisPlayerIndex) {
                stringToShow = "Congratulations! you are the Winner";
                colorString = "#383bff"
            }
            else {
                stringToShow = "Player " + playerNumberString + " won the Game.";
                colorString = "#ff1e08"
            }
            break;
        case("QUIT"):
            if (currentTurnPlayerIndex == thisPlayerIndex) {
                stringToShow = "You quit the game! you lost the game";
                colorString = "#ff1e08"
            }
            else {
                stringToShow = "Player " + playerNumberString + " quit he game, you are the Winner!";
                colorString = "#383bff"
            }
            break;
    }
    playerTurnHtML.innerHTML = stringToShow;
    playerTurnHtML.style.color = colorString;
}

function callAjaxEndOfGame() {
    return ajaxEndOfGame(event);
}

function endOfMoveActions(m_ActionsResult, i_LastPlayer) {
    var hitStatusString = m_ActionsResult.toString();
    if (hitStatusString.localeCompare("WINNER") == 0 || hitStatusString.localeCompare("QUIT") == 0) {
        clearInterval(updateInterval);
        endOfGameMessage(hitStatusString, i_LastPlayer);
        callAjaxEndOfGame();

    }
}

function switchTurnActions(i_SwitchTurnInfo) {
    currentTurnPlayerIndex = i_SwitchTurnInfo.m_NextTurnPlayerIndex;
    prevTurnIndex = i_SwitchTurnInfo.m_PreviousTurnPlayerIndex;
    if (i_SwitchTurnInfo.m_ActionsResult.toString().localeCompare("QUIT") != 0) {
        if (i_SwitchTurnInfo.m_ActionsResult.toString().localeCompare("MINE_PLANTED") == 0 && prevTurnIndex == thisPlayerIndex) {
            reduceMineAmount();
            var mineDiv = document.getElementById("fillMineAmount");
            mineDiv.textContent = "Mines Left: " + MineAmount;
        }

        updatePlayerStatics(i_SwitchTurnInfo.m_Statics);
        updateShipsLeft(i_SwitchTurnInfo.m_PlayerTypesLeft, i_SwitchTurnInfo.m_EnemyTypesLeft);
        updateShipBoardByPoints(i_SwitchTurnInfo.m_ShipPointsToChange, i_SwitchTurnInfo.m_ChangeShipPointsTo);
        updateHitsBoardByPoints(i_SwitchTurnInfo.m_HitPointsToChange, i_SwitchTurnInfo.m_ChangeHitPointsTo);
        updatePlayerTurnMessage();
    }

    endOfMoveActions(i_SwitchTurnInfo.m_ActionsResult, i_SwitchTurnInfo.m_NextTurnPlayerIndex);
}

function reduceMineAmount() {
    if (MineAmount > 0) {
        MineAmount--;
    }
}

function updatePlayerTurn() {
    if (currentTurnPlayerIndex != thisPlayerIndex) {
        $.ajax({
            url: "../../gameactions/gamemove",
            type: "GET",
            success: function (SwitchTurnInfo) {
                var TurnInfoJson = JSON.parse(SwitchTurnInfo);

                if (TurnInfoJson.m_NextTurnPlayerIndex != -1) {//m_NextTurnPlayerIndex is -1 when the game is not active
                    currentTurnPlayerIndex = TurnInfoJson.m_NextTurnPlayerIndex;

                    if (TurnInfoJson.m_ActionsResult.toString() == "FIRST_MOVE" && totalSeconds == 0) {//option: this is the way to deal with first turn
                        updatePlayerTurnMessage();
                        setInterval(setTime, 1000);
                    }
                    else if (thisPlayerIndex != TurnInfoJson.m_PreviousTurnPlayerIndex) {
                        switchTurnActions(TurnInfoJson);
                    }
                }
            }
        });
    }

}

$(function ajaxInitializeRoom() {
    $.ajax({
        url: "../../openGameRoom",
        success: function (roomInfo) {
            InitializeRoom(roomInfo);
        }
    });

});

function setTime() {

    var minutesLabel = document.getElementById("fillMinutes");
    var secondsLabel = document.getElementById("fillSeconds");
    ++totalSeconds;
    secondsLabel.innerText = pad(totalSeconds % 60);
    minutesLabel.innerText = pad(parseInt(totalSeconds / 60));
}

function pad(val) {
    var valString = val + "";
    if (valString.length < 2) {
        return "0" + valString;
    }
    else {
        return valString;
    }
}

function updatePlayerNumber(i_playerIndx, i_playerName, i_GameType, i_RoomName) {

    var StringOfPlayer;
    if (i_playerIndx === 0) {
        thisPlayerIndex = 0;
        StringOfPlayer = i_playerName.toString() + ", You are player 1 ,";
    }
    else {
        thisPlayerIndex = 1;
        StringOfPlayer = i_playerName.toString() + ", You are player 2 ,"

    }

    StringOfPlayer += " Room: " + i_RoomName;
    StringOfPlayer += " , Game Type: " + i_GameType;
    document.getElementById("header").innerHTML = StringOfPlayer;
}

function logoutAction() {
    debugger;
    var typeStr = "QUIT";
    $.ajax({
        url: "../../gameactions/gamemove",
        data: {"TYPE": typeStr, isMine: "FALSE"},//send the type "point" when send col and row
        async: false,
        type: "POST",
        success: function (QuitActionInfo) {
            switchTurnActions(QuitActionInfo);
        }

    });
}

function InitializeRoom(roomInfo) {
    document.getElementById("enemyStaticsTable").style.display = "none";
    updateBoardSize(roomInfo.boardSize);
    updatePlayerNumber(roomInfo.playerIndx, roomInfo.playerName, roomInfo.gameType, roomInfo.roomName);
    updateCellSize();
    updateMineAmount(roomInfo.mineAmount);
    initializeHitBoard();
    initializeShipBoard(roomInfo.shipsBoard);

    if (roomInfo.gameType.localeCompare("ADVANCE") == 0) {
        initializeMineSection();
    }

    updateInterval = setInterval(updatePlayerTurn, updatePlayerRefreshRate);
    updatePlayerTurnMessage();
}

function initializeMineSection() {
    debugger;
    var mineDiv = document.getElementById("fillMineImg");
    var mineImg = new Image();
    var minesLeft = "Mines Left: ";
    var mineText = document.getElementById("fillMineAmount");
    // var mineText = document.createTextNode("");
    mineText.textContent = minesLeft + MineAmount;
    // mineText.setIdAttribute = "mineText";
    mineImg.src = '../../resources/Mine.jpg';
    mineImg.height = cellSize;
    mineImg.width = cellSize;
    mineImg.draggable = true;
    mineImg.addEventListener('dragstart', function (event) {
        onDrag(event)
    }, false);
    mineDiv.appendChild(mineImg);
}

function updateCellSize() {

    cellSize = 400 / (boardSize);
}

function updateBoardSize(i_boardSize) {

    boardSize = i_boardSize;
}

function updateMineAmount(i_mineAmount) {
    MineAmount = i_mineAmount;
}

function initializeShipBoard(i_shipsBoard) {
    var table = document.getElementById("ShipsBoard");
    clearTable(table);
    for (i = 0; i < boardSize; i++) {
        var row = table.insertRow(i);
        for (j = 0; j < boardSize; j++) {
            var cell = row.insertCell(j);
            updateCellImage(cell, i_shipsBoard[i][j]);
            cell.style.borderWidth = "0px";
            cell.ondragover = function (event) {
                if (thisPlayerIndex == currentTurnPlayerIndex && MineAmount > 0) {
                    allowMineDrop(event);
                }
            };
            cell.ondrop = hitCellOnClick(i, j, "TRUE");
        }
    }
}

function clearTable(table) {
    if (table.rows != undefined) {
        for (var i = table.rows.length - 1; i >= 0; i--) {
            table.deleteRow(i);
        }
    }
}

function updateCellImage(cell, char) {
    var img = document.createElement('img');
    var ImageName;

    var url = "../../resources/";

    switch (char) {
        case ('S'):
            ImageName = "Ship.jpg";
            break;
        case (' '):
            ImageName = "wave.jpg";
            break;
        case ('M'):
            ImageName = "Mine.jpg";
            break;
        case ('X'):
            ImageName = "X.png";
            break;
        case ('O'):
            ImageName = "waterSplash.jpg";
            break;
        case ('D'):
            ImageName = "shipDestroyed.gif";
            break;
        case ('E'):
            ImageName = "MineDestroy.jpg";
            break;
    }
    if (cell.childElementCount == 0) {
        img.width = cellSize;
        img.height = cellSize;

        img.src = url + ImageName;
        cell.appendChild(img);
    }
    else {//when update, just change the url
        cell.querySelector('img').src = url + ImageName;

    }


}

function initializeHitBoard() {

    var table = document.getElementById("HitsBoard");
    clearTable(table);
    for (i = 0; i < boardSize; i++) {
        var row = table.insertRow(i);
        for (j = 0; j < boardSize; j++) {
            var cell = row.insertCell(j);
            cell.getElementsByTagName('td').width = cellSize;
            cell.getElementsByTagName('td').height = cellSize;
            updateCellImage(cell, ' ');
            cell.style.borderWidth = "0px";
            cell.addEventListener('click', hitCellOnClick(i, j, "FALSE"));
        }
    }

}

function hitCellOnClick(row, col, isMine) {
    var typeString = "POINT";
    return function (event) {
        $.ajax({
            url: "../../gameactions/gamemove",
            data: {"ROW": row, "COL": col, "TYPE": typeString, "isMine": isMine},//send the type "point" when send col and row
            type: "POST",
            success: function (moveActionInfo) {
                debugger;
                switchTurnActions(moveActionInfo);
            }
        });
    }
}

function onDrag(ev) {
    if (MineAmount > 0) {
        ev.dataTransfer.setData("text", ev.target.id);
    }
}

function allowMineDrop(ev) {
    ev.preventDefault();
}

// Only when game ends
function showEnemyStatics(i_enemystatics) {
    if (i_enemystatics != undefined) {
        var parsedStatics = JSON.parse(i_enemystatics);
        document.getElementById("EnemyfillScore").innerText = parsedStatics.m_Score.toString();
        document.getElementById("EnemyfillHits").innerText = parsedStatics.m_Hits.toString();
        document.getElementById("EnemyfillMiss").innerText = parsedStatics.m_Misses.toString();
        var avarageTurnTime = parsedStatics.m_TotalTimePlayed;
        if (i_enemystatics.m_TurnsTaken != 0) {
            avarageTurnTime = Math.round((parsedStatics.m_TotalTimePlayed / parsedStatics.m_TurnsTaken));
        }
        document.getElementById("EnemyfillAvTurnTime").innerText = avarageTurnTime.toString() + " seconds";
        document.getElementById("EnemyfillTurnsPlayed").innerText = parsedStatics.m_TurnsTaken.toString();
    }

    document.getElementById("enemyStaticsTable").style.display = "";
}

// To show number of selected files on the label
var inputs = document.querySelectorAll('.inputFile');
Array.prototype.forEach.call(inputs, function (input) {
    var label = input.nextElementSibling,
        labelVal = label.innerHTML;

    input.addEventListener('change', function (e) {
        var fileName = '';
        if (this.files && this.files.length > 1)
            fileName = ( this.getAttribute('data-multiple-caption') || '' ).replace('{count}', this.files.length);
        else
            fileName = e.target.value.split('\\').pop();

        if (fileName)
            label.querySelector('span').innerHTML = fileName;
        else
            label.innerHTML = labelVal;
    });
});