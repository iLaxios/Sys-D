import java.util.Scanner;

class Player {

    String name;
    boolean isWhite;

    Player(String name, boolean isWhite) {
        this.name = name;
        this.isWhite = isWhite;
    }

    Move getMove(Scanner sc) {
        int r1 = sc.nextInt(), c1 = sc.nextInt();
        int r2 = sc.nextInt(), c2 = sc.nextInt();
        
        return new Move(new Cell(r1, c1), new Cell(r2, c2));
    }

}

interface MoveStrategy {
    boolean validateMove(Move move);
}

class Move {

    Cell startCell;
    Cell endCell;

    public Move(Cell startCell, Cell endCell) {
        this.startCell = startCell;
        this.endCell = endCell;
    }

    boolean isValidMove() {
        Piece piece = startCell.getPiece();
        return piece.canMove(this);
    }

    void makeMove() {
        Piece piece = startCell.getPiece();
        piece.makeMove(this);
    }

}


abstract class Piece {

    boolean isWhite;
    String label;
    MoveStrategy moveStrategy;

    
    public Piece(boolean isWhite, String label, MoveStrategy moveStrategy) {
        this.isWhite = isWhite;
        this.label = label;
        this.moveStrategy = moveStrategy;
    }
    
    boolean canMove(Move move) {

        if(move.endCell.row < 0 || move.endCell.row >= 8 || move.endCell.col < 0 || move.endCell.col >= 8 ) return false;

        return moveStrategy.validateMove(move);
    }

    void makeMove(Move move) {
        move.endCell.piece = move.startCell.piece;
        move.startCell.piece = null;
    }
}

class PieceFactory {

    boolean isWhite;
    String label;
    MoveStrategy moveStrategy;
    String type;

    public static Piece createPiece(String type, boolean isWhite, String label, MoveStrategy moveStrategy) {
    
        switch (type.toLowerCase()) {
            case "Rook":
                return new RookPiece(isWhite, label, moveStrategy);
        
            default:
                throw new IllegalArgumentException("Unkown Piece!");
        }
    }
}

class RookPiece extends Piece {

    RookPiece(boolean isWhite, String label, MoveStrategy moveStrategy) {
        super(isWhite, label, moveStrategy);
    }

}

class RookStrategy implements MoveStrategy {

    @Override
    public boolean validateMove(Move move) {        
        return move.startCell.row == move.endCell.row || move.startCell.col == move.endCell.col;
    }
}


class Cell {

    int row, col;
    Piece piece;

    Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }

}

class Board {

    Cell[][] cell = new Cell[8][8];
    private static Board instance;


    private Board() {
        initBoard();
    }

    public static Board getInstance() {
        if(instance == null) {
            instance = new Board();
        }
        
        return instance;
    }
    
    void initBoard() {

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                cell[i][j] = new Cell(i, j);
            }
        }

        cell[0][0].setPiece(PieceFactory.createPiece("Rook", true, "WR", new RookStrategy()));
    }

}


class Game {
    
    Player p1 = new Player("Player1", true);
    Player p2 = new Player("Player2", false);
    Board board = Board.getInstance();
    Scanner sc = new Scanner(System.in);

    void start() {
        Player current = p1;
        
        while(true) {
            Move move = current.getMove(sc);

            if(!move.isValidMove()) {
                System.out.println("illegal move");
                continue;
            } 

            move.makeMove();

            current = (current == p1) ? p2 : p1;
        }
    }
}

public class Main {

    public static void main(String args[]) {
        Game game = new Game();
        game.start();
    }
}
