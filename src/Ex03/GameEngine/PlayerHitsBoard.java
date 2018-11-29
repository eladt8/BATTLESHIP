package Ex03.GameEngine;



public class PlayerHitsBoard extends Board implements Cloneable {
    public PlayerHitsBoard(int i_sizeOfBoard) {
        super(i_sizeOfBoard);
    }

    @Override
    public PlayerHitsBoard clone(){
        PlayerHitsBoard newBoard=new PlayerHitsBoard(this.GetSizeOfBoard());

        for(int i=0;i<GetSizeOfBoard();i++){
            for(int j=0;j<GetSizeOfBoard();j++){
                newBoard.Draw(new Point(i,j),this.GetPointInBoard(new Point(i,j)));
            }
        }
        return newBoard;
    }
}
