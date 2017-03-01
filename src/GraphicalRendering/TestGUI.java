package GraphicalRendering;

import Player.PlayerState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by James Lawson on 12/21/2016.
 *
 * A simple GUI to test the networking and game code
 * eventually I want to replace this with graphics done in
 * Light Weight Java Game Library.
 */
public class TestGUI extends JFrame implements GameGUI{

    private int colClickBuffer = -1;
    private boolean allowClicks = false;
    private boolean hasClicked = false;

    private final int ROWS = 6;
    private final int COLS = 7;

    private Color[][] board = new Color[ROWS][COLS];

    private final Color EMPTY = Color.LIGHT_GRAY;
    private final Color RED = Color.RED;
    private final Color BLACK = Color.BLACK;

    private Dimension screen;

    private int WIDTH;
    private int HEIGHT;

    private int GRID_SIZE;
    private int CHIP_OFFSET;
    private int CHIP_SIZE;

    /**
     * creates the frame and panel and sets up all
     * of the listeners
     */
    public TestGUI()
    {
        //get the individual screen width and height
        screen = Toolkit.getDefaultToolkit().getScreenSize();

        WIDTH = (int) (screen.getWidth()/2);
        HEIGHT = ((WIDTH*6)/7);

        GRID_SIZE = WIDTH/7;
        CHIP_OFFSET = GRID_SIZE/10;
        CHIP_SIZE = GRID_SIZE-(2*CHIP_OFFSET);


        GUIpanel p = new GUIpanel();
        p.setPreferredSize(new Dimension(WIDTH,HEIGHT));

        //newGame();

        this.add(p);
        this.pack();

        this.addMouseListener(p);

        this.setTitle("Connect Four");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        this.repaint();
    }

    /**
     * lets the player click once, only when its their turn
     *
     * @return the collumn associated with the mouse click
     */
    @Override
    public int getClick()
    {
        try {
            while(!hasClicked) {
                Thread.sleep(50);
                //System.out.println(hasClicked);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        hasClicked = false;
        return colClickBuffer;
    }

    /**
     * creates a dialogue waiting for the player to start or quit
     * if they click OK, then the board is cleared and we start anew
     *
     * @return PlayerState, the appropriate state depending on the response
     */
    public PlayerState newGame()
    {

        int op = JOptionPane.showConfirmDialog(this, "Would you like to play?","start",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);

        if(op == JOptionPane.OK_OPTION) {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    board[i][j] = EMPTY;
                }
            }
            this.repaint();
            return PlayerState.STARTING;
        }
        else
        {
            return PlayerState.FORCED_GAME_OVER;
        }
    }

    /**
     * drops a piece into the array, see ConnectFourGame dropPiece
     *
     * @param col where to drop the piece
     * @param player true if itd "this" players turn
     */
    private void dropPiece(int col, boolean player)
    {
        for(int i = 0; i<ROWS; i++)
        {
            if(board[i][col].equals(EMPTY))
            {
                if(player)
                {
                    board[i][col] = RED;
                }
                else
                {
                    board[i][col] = BLACK;
                }
                break;
            }
        }
        this.repaint();
    }

    /**
     *  updates everything relating to the board
     *  including dropping the piece
     *
     * @param col where to drop the piece
     * @param player true if itd "this" players turn
     */
    @Override
    public void updateBoard(int col, boolean player) {
        dropPiece(col, player);
    }

    /**
     * if its the player turn, the client can allow clicks
     *
     * @param click the boolean to allow clicks or not
     */
    @Override
    public void allowClicks(boolean click)
    {
        allowClicks = click;
    }


    /**
     * show a win display
     */
    @Override
    public void doWin() {
        JOptionPane.showMessageDialog(this, "you won!","GAME OVER",JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * show a loss display
     */
    @Override
    public void doLoss() {
        JOptionPane.showMessageDialog(this, "you lost!","GAME OVER",JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * show a "draw" display
     */
    @Override
    public void doDraw() {
        JOptionPane.showMessageDialog(this, "the game was a draw!","GAME OVER",JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * clean up whatever needs to be cleaned up in
     * the display
     */
    @Override
    public void cleanUp()
    {
        this.dispose();
    }

    /**
     * extends the JPanel and updates the display and
     * listens for clicks of the mouse
     */
    class GUIpanel extends JPanel implements MouseListener
    {
        @Override
        public void paint(Graphics g) {
            g.setColor(Color.YELLOW);
            g.fillRect(0,0,this.getWidth(),this.getHeight());

            for(int i = 0; i< ROWS; i++)
            {
                for(int j = 0; j<COLS; j++)
                {
                    g.setColor(board[ROWS-i-1][j]);

                    g.fillOval(j*GRID_SIZE+CHIP_OFFSET,i*GRID_SIZE+CHIP_OFFSET,CHIP_SIZE,CHIP_SIZE);

                }
            }
        }

        /**
         * if clicks are allowed, update the mouse buffer for the most recent click
         *
         * @param e the Mouse Event
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if(allowClicks) {
                hasClicked = true;
                int col = e.getX() / GRID_SIZE;
                System.out.println(col);
                colClickBuffer = col;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /**
     * just to test some ish
     * @param args ignored
     */
    public static void main(String[] args)
    {
        TestGUI test = new TestGUI();
        test.dropPiece(0,true);
        test.dropPiece(1,true);
        test.dropPiece(2,true);
        test.dropPiece(3,true);
        test.dropPiece(0,false);
        test.dropPiece(1,false);
        test.dropPiece(2,false);
        test.dropPiece(3,false);


        System.out.println(test.getClick());

    }
}
