package Player;

import GraphicalRendering.GameGUI;
import GraphicalRendering.TestGUI;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * handles the client or player side of the
 * connect four game.
 *
 * @author  James Lawson
 * @version 1.0 - 12/21/2016
 */
public class PlayerClient implements Runnable
{
    private String HOST = "";
    private final int PORT = 13000;

    private PlayerState pstate = PlayerState.WAITING;
    private ClientState cstate = ClientState.CREATED;

    private Socket server;
    private PrintWriter out;
    private BufferedReader in;

    private GameGUI gameGUI;

    /**
     * constructor for the client side networking.
     * Sets up the Socket and attempts to connect to the server and
     * initializes whatever GUI you choose to use
     * -NOTE: the server needs to be running before you run the player
     */
    public PlayerClient()
    {
        gameGUI = new TestGUI();

        HOST = JOptionPane.showInputDialog(null,"Enter the host name.","",JOptionPane.QUESTION_MESSAGE);

        try
        {
            System.out.println(System.currentTimeMillis()+ " :connecting to server...");
            server = new Socket(HOST,PORT);
            System.out.println(System.currentTimeMillis()+ " :server found "+server.getLocalAddress().getHostName());

            out = new PrintWriter(server.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            System.out.println(System.currentTimeMillis()+ " :streams created");

        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
            gameGUI.cleanUp();
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * starts the Game Loop and cleans up once the Game Loop ends
     */
    @Override
    public void run()
    {
        cstate = ClientState.RUNNING;

        System.out.println(System.currentTimeMillis()+ " :thread running");

        try
        {
            clientLoop();
            cleanUp();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void waitForStart() throws IOException
    {
        String message = "";

        System.out.println(System.currentTimeMillis()+ " :waiting for start from server...");
        pstate = PlayerState.WAITING;
        while(pstate == PlayerState.WAITING)
        {
            message = in.readLine();

            if(message.equals("READY FOR START?:"))
            {
                pstate = gameGUI.newGame();
                if(pstate == PlayerState.STARTING) {
                    System.out.println(System.currentTimeMillis() + " :start confirmed");
                    out.println("START:");
                    cstate = ClientState.RUNNING;
                }
                else
                {
                    System.out.println(System.currentTimeMillis() + " :player quit");
                    out.println("QUIT:");
                    cstate = ClientState.CLOSED;
                }
            }
        }
    }

    private void clientLoop() throws IOException
    {
        String message = "";

        System.out.println(System.currentTimeMillis()+ " :game starting");

        while(cstate == ClientState.RUNNING)
        {

            waitForStart();

            while (pstate == PlayerState.STARTING ||
                    pstate == PlayerState.THIS_PLAYER_TURN || pstate == PlayerState.OTHER_PLAYER_TURN) {


                System.out.println("\n" + System.currentTimeMillis() + " :waiting for server...");
                message = in.readLine();
                System.out.println(System.currentTimeMillis() + " :server message :" + message);

                String[] spltmess = message.split(":");

                switch (spltmess[0]) {
                    //TURN comes packaged with the previous players turn play
                    case "TURN":
                        System.out.println(System.currentTimeMillis() + " :start player turn");
                        pstate = PlayerState.THIS_PLAYER_TURN;
                        doTurn(Integer.parseInt(spltmess[1]));
                        break;
                    case "BAD MOVE":
                        System.out.println(System.currentTimeMillis() + " :bad move, try turn again");
                        break;
                    case "WON":
                        System.out.println(System.currentTimeMillis() + " :you won!");
                        gameGUI.updateBoard(Integer.parseInt(spltmess[1]), true);
                        gameGUI.doWin();
                        pstate = PlayerState.WON;
                        break;
                    case "LOSS":
                        System.out.println(System.currentTimeMillis() + " :you lost!");
                        gameGUI.updateBoard(Integer.parseInt(spltmess[1]), false);
                        gameGUI.doLoss();
                        pstate = PlayerState.LOST;
                        break;
                    case "DRAW":
                        System.out.println(System.currentTimeMillis() + " :the game was a draw!");
                        gameGUI.updateBoard(Integer.parseInt(spltmess[1]), true);
                        gameGUI.doDraw();
                        pstate = PlayerState.DRAW;
                        break;
                    case "GOOD MOVE":
                        System.out.println(System.currentTimeMillis() + " :good move, updating GUI");
                        gameGUI.updateBoard(Integer.parseInt(spltmess[1]), true);
                        pstate = PlayerState.OTHER_PLAYER_TURN;
                        break;
                    default:
                        System.out.println(System.currentTimeMillis() + " :FORCED QUIT");
                        cstate = ClientState.CLOSED;
                        pstate = PlayerState.FORCED_GAME_OVER;

                }
            }
        }
    }

    private void doTurn(int col)
    {
        if(col != -1)
        {
            gameGUI.updateBoard(col, false);
        }
        gameGUI.allowClicks(true);
        int clk = gameGUI.getClick();
        gameGUI.allowClicks(false);

        System.out.println(System.currentTimeMillis() + "PLAY:"+clk);
        out.println("PLAY:"+clk);
    }


    private void cleanUp() throws IOException
    {
        in.close();
        out.close();

        server.close();
        gameGUI.cleanUp();

        System.out.println(System.currentTimeMillis()+ " :connections closed");
    }


    public static void main(String args[])
    {
        Thread player = new Thread(new PlayerClient());
        player.start();

        try {
            player.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(System.currentTimeMillis()+ " :Good Bye!");
    }


}
