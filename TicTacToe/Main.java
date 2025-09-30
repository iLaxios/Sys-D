import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

enum Symbol {
    X, O;
}

interface MoveStrategy {
    int[] getMove(Board board);
}

// HumanStrategy & BotStrategy
class HumanStrategy implements MoveStrategy {

    Scanner sc;

    HumanStrategy(Scanner sc) {
        this.sc = sc;
    }

    public int[] getMove(Board board) {

        while(true) {
            System.out.println("Enter board position" + board.getSize());
            int r = sc.nextInt();
            int c = sc.nextInt();

            if(!board.isValid(r, c) || !board.isEmpty(r, c)) {
                System.out.println("invalid board position");
                continue;
            }

            return new int[]{r, c};
        }
    }
}

class BotStrategy implements MoveStrategy {


    public int[] getMove(Board board) {

        while(true) {
            Random rand = new Random();
            int r = rand.nextInt(board.getSize());
            int c = rand.nextInt(board.getSize());

            if(!board.isValid(r, c) || !board.isEmpty(r, c)) {
                // System.out.println("invalid board position");
                continue;
            }
            return new int[]{r, c};
        }

    }
}

class Player {
    String name;
    Symbol symbol;
    MoveStrategy moveStrategy;

    Player(String name, Symbol symbol, MoveStrategy strategy) {
        this.name = name;
        this.symbol = symbol;
        moveStrategy = strategy;
    }

    int[] play(Board board) {
        return moveStrategy.getMove(board);
    }
}

class PlayerFactory {

    public static Player createPlayer(String name, Symbol symbol, String type, Scanner sc) {

        if(type.equalsIgnoreCase("human")) return new Player(name, symbol, new HumanStrategy(sc));
        if(type.equalsIgnoreCase("bot")) return new Player(name, symbol, new BotStrategy());

        return null;
    }
}

class Board {
    Symbol grid[][];
    int n;

    Board(int n) {
        this.n = n;
        grid = new Symbol[n][n];
    }

    int getSize() {
        return n;
    }

    boolean isValid(int r, int c) {
        return (r >= 0 && r < n && c >= 0 && c < n);
    }

    boolean isEmpty(int r, int c) {
        return (grid[r][c] == null);
    }

    void place(int[] pos, Symbol s) {
        grid[pos[0]][pos[1]] = s;
    }

    void printBoard() {
        for(int i = 0; i < n; i++) {
            System.out.println(Arrays.toString(grid[i]));
        }
    }

    boolean checkWin(Symbol s) {

        System.out.println(s.name());
        // check all rows
        for(int i = 0; i < n; i++) {
            boolean rowWin = true;
            for(int j = 0; j < n; j++) {
                if(grid[i][j] != s) {
                    rowWin = false;
                    break;
                }
            }
            if(rowWin) return true;
        }

        for(int i = 0; i < n; i++) {
            boolean colWin = true;
            for(int j = 0; j < n; j++) {
                if(grid[j][i] != s) {
                    colWin = false;
                    break;
                }
            }
            if(colWin) return true;
        }

        boolean diagWin = true;
        for(int i = 0; i < n; i++) {
            if(grid[i][i] != s) {
                diagWin = false;
                break;
            }
        }
        if(diagWin) return true;

        boolean antiDiagWin = true;
        for(int i = 0; i < n; i++) {
            if(grid[i][n - i - 1] != s) {
                antiDiagWin = false;
                break;
            }
        }
        if(antiDiagWin) return true;

        return false;
    }

    boolean checkDraw() {

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if(grid[i][j] == null) return false;
            }
        }
        return true;
    }
}


class Game {

    Player p1;
    Player p2;
    Board board;

    Game(Player p1, Player p2, int n) {
        this.p1 = p1;
        this.p2 = p2;
        this.board = new Board(n);
    }

    void start() {
        Player current = p1;

        while(true) {
            board.printBoard();
            System.out.println(current.name + "'s turn...");
            int[] pos = current.play(board);
            board.place(pos, current.symbol);
            boolean endGame = board.checkWin(current.symbol);

            if(endGame) {
                declareWinner(p1);
                return;
            }

            if(board.checkDraw()) {
                System.out.println("Its a Draw!");
                return;
            }

            current = (current == p1) ? p2 : p1;
        }
    }

    void declareWinner(Player p1) {
        System.out.println(p1.name + " is the winner!");
    }
}

public class Main {

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        Player p1 = PlayerFactory.createPlayer("player1", Symbol.X,"Human",  sc);
        Player p2 = PlayerFactory.createPlayer("player2", Symbol.O, "Bot", sc);
        Game game = new Game(p1, p2 ,3 );
        game.start();
    }
}