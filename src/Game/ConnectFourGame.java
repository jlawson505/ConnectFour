package Game;

/**
 * @author James Lawson
 * @version 1.0 - 12/20/16
 *
 *
 * contains the main method,
 * takes care of all the game logistic handling on the server side of the game
 */
public class ConnectFourGame implements GameModel{

    private final int ROWS = 6;
    private final int COLS = 7;
    private final int NUM_FOR_WIN = 4;

    //the 2D array that represents the connect four board
    private PieceType[][] board = new PieceType[ROWS][COLS];

    //p1 - red: p2 - black
    private PieceType player = PieceType.RED;

    /**
     * constructor that initializes the game
     */
    public ConnectFourGame()
    {
        newGame();
    }

    /**
     * resets all variables to default values to
     * start a new game
     */
    public void newGame()
    {
        player = PieceType.RED;

        for(int i = 0; i<ROWS; i++)
        {
            for(int j = 0; j<COLS; j++)
            {
                board[i][j] = PieceType.EMPTY;
            }
        }
    }

    /**
     *  PLAYER1_WIN if red wins
     *  PLAYER2_WIN if black wins
     *  DRAW if the board is full
     *  NO_WINNER_YET if valid move and the game is ingoing
     *  BAD_MOVE if the column is full
     *
     * @param col to perform the turn at
     * @return the appropriate game state
     */
    public GameState doTurn(int col)
    {
        if(isSafeDrop(col))
        {
            dropPiece(col);
            //System.out.println(getBoardString());

            if (isWinner())
            {
                if (player == PieceType.RED)
                {
                    return GameState.PLAYER1_WIN;
                }
                else
                {
                    return GameState.PLAYER2_WIN;
                }
            }
            else if(isDraw())
            {
                return GameState.DRAW;
            }

            changePlayer();
            return GameState.PLAYING;
        }
        return GameState.BAD_MOVE;
    }

    /**
     * drop the piece in the first available space
     *
     * @param col column to drop the piece in
     */
    private void dropPiece(int col)
    {
        for(int i = 0; i<ROWS; i++)
        {
            if(board[i][col] == PieceType.EMPTY)
            {
                board[i][col] = player;
                break;
            }
        }
    }

    /**
     * If there is a piece in the top row,
     * then all lower rows must be full.
     *
     * @param col the column to check
     * @return true if can safely play a piece
     */
    private boolean isSafeDrop(int col)
    {
        if(board[ROWS-1][col] == PieceType.EMPTY)
        {
            return true;
        }
        return false;
    }

    /**
     * checks for a winner from every position in the board,
     * there are more efficient ways of doing this, but the board is so small
     * that we aren't losing a lot and it seems much easier to implement
     *
     * @return true if a winner in any direction is found
     */
    private boolean isWinner()
    {
        for(int i = 0; i<ROWS; i++)
        {
            for(int j = 0; j<COLS; j++)
            {
                if(localWinner(i,j,1,0) || localWinner(i,j,0,1) || localWinner(i,j,1,1) || localWinner(i,j,1,-1))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * deltaRow and deltaCol essential provide the direction that i will
     * check for the winner (vertical, horizontal, diagonal left, diagonal right)
     *
     * @param col starting column
     * @param row starting row
     * @param deltaRow the change in the row
     * @param deltaCol the change in column
     * @return true if a winning condition is found
     */
    private boolean localWinner(int row, int col, int deltaRow, int deltaCol)
    {
        boolean winner = true;

        for(int i =0; i<NUM_FOR_WIN; i++)
        {
            if(row>=ROWS || row<0 || col>=COLS || col<0)
            {
                winner = false;
            }
            else if(board[row][col] != player)
            {
                winner = false;
            }

            row+=deltaRow;
            col+=deltaCol;
        }

        return winner;
    }

    /**
     * a draw is determined if the board is full and
     * there is no winner.
     *
     * @return true if there is a draw
     */
    private boolean isDraw()
    {
        for(int i = 0; i<ROWS; i++)
        {
            for(int j = 0; j<COLS; j++)
            {
                if(board[i][j] == PieceType.EMPTY)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * changes the player to the opposite color
     */
    private void changePlayer()
    {
        if(player == PieceType.RED)
        {
            player = PieceType.BLACK;
        }
        else
        {
            player = PieceType.RED;
        }
    }

    /**
     * @return number of rows
     */
    public int getRows()
    {
        return ROWS;
    }

    /**
     * @return number of columns
     */
    public int getColumns()
    {
        return COLS;
    }

    /**
     * @return the current player
     */
    public PieceType getPlayer()
    {
       return player;
    }

    public String getBoardString()
    {
        String s = "";

        for(int i = 0; i<ROWS; i++)
        {
            for(int j = 0; j<COLS; j++)
            {
                if (board[i][j] == PieceType.RED)
                {
                    s += "X";
                }
                else if(board[i][j] == PieceType.BLACK)
                {
                    s+="O";
                }
                else
                {
                    s+="-";
                }
            }
            s+="\n";
        }

        return s;
    }

    public static void main(String[] args)
    {
        ConnectFourGame c = new ConnectFourGame();

        System.out.println(c.getBoardString());
        System.out.println(c.isWinner());

        System.out.println(c.doTurn(1));
        System.out.println(c.getBoardString());

        System.out.println(c.doTurn(2));
        System.out.println(c.getBoardString());

        System.out.println(c.doTurn(1));
        System.out.println(c.getBoardString());

        System.out.println(c.doTurn(2));
        System.out.println(c.getBoardString());

        System.out.println(c.doTurn(1));
        System.out.println(c.getBoardString());

        System.out.println(c.doTurn(2));
        System.out.println(c.getBoardString());

        System.out.println(c.doTurn(1));
        System.out.println(c.getBoardString());

    }

}
