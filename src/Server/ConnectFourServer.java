package Server;

import Game.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * the server side code for the connect four game
 * creates the Game object and handles all communication between the clients
 *
 * @author James Lawson
 * @version 1.0 12/20/2016
 */
public class ConnectFourServer implements Runnable
{

    private final int port = 13000;
    //30 seconds
    private final int WAIT_TIME = 30000;


    private ServerSocket server;
    private Socket player1;
    private Socket player2;
    PrintWriter player1_out;
    BufferedReader player1_in;
    PrintWriter player2_out;
    BufferedReader player2_in;

    private ServerState sstate = ServerState.CREATED;
    private GameState gstate = GameState.WAITING_TO_START;

    private GameModel connectFourGame;

    /**
     * right now all the constructor does is create a new instance of the GameModel
     */
    public ConnectFourServer()
    {
        connectFourGame = new ConnectFourGame();

        try {
            server = new ServerSocket(port);
            //server.setSoTimeout(WAIT_TIME);

            System.out.println(System.currentTimeMillis() + " :waiting for player 1...");
            player1 = server.accept();
            System.out.println(System.currentTimeMillis() + " :player 1 connected");

            System.out.println(System.currentTimeMillis() + " :waiting for player 2...");
            player2 = server.accept();
            System.out.println("player 2 connected");

            player1_out = new PrintWriter(player1.getOutputStream(), true);
            player1_in = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            System.out.println(System.currentTimeMillis() + " :player 1 streams created\n");

            player2_out = new PrintWriter(player2.getOutputStream(), true);
            player2_in = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            System.out.println(System.currentTimeMillis() + " :player 2 streams created\n");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * run opens up a socket and listens for 2 connections.
     * once 2 players have conected, create the input and output
     * streams, then starts the game,
     *
     * --considering moving this to the constructor
     */
    @Override
    public void run()
    {
        try
        {
            serverLoop();

            cleanUp();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     *  runs while the server is still active
     * @throws IOException
     */
    private void serverLoop() throws IOException
    {
        sstate = ServerState.RUNNING;

        System.out.println(System.currentTimeMillis() + " :game starting...");

        while(sstate == ServerState.RUNNING)
        {
            System.out.println(System.currentTimeMillis() + " :start of loop");

            waitForStart();
            gameLoop();
        }
    }

    /**
     * does the main game logic
     * @throws IOException
     */
    private void gameLoop() throws IOException
    {
        String p1message = "";
        String p2message = "";

        int prevMove = -1;

        while(gstate == GameState.PLAYING) {

            if (sstate != ServerState.CLOSED) {
                System.out.println(System.currentTimeMillis() + " :server still running");
                //player 1 loop
                gstate = GameState.NO_MOVE;
                while (gstate == GameState.NO_MOVE) {
                    System.out.println(System.currentTimeMillis() + " :alert player 1 of turn");
                    player1_out.println("TURN:" + prevMove);

                    System.out.println(System.currentTimeMillis() + " :waiting for player 1 turn...");
                    p1message = player1_in.readLine();
                    System.out.println(System.currentTimeMillis() + "--" + p1message);

                    String[] splitMes = p1message.split(":");

                    switch (splitMes[0]) {
                        case "PLAY":
                            prevMove = Integer.parseInt(splitMes[1]);
                            gstate = attemptTurn(prevMove);
                            System.out.println(System.currentTimeMillis() + "state tested");
                            switch (gstate) {
                                case PLAYER1_WIN:
                                    System.out.println(System.currentTimeMillis() + " :PLAYER 1 WIN");
                                    player1_out.println("WON:" + prevMove);
                                    player2_out.println("LOSS:" + prevMove);
                                    gstate = GameState.PLAYER1_WIN;
                                    break;
                                case PLAYER2_WIN:
                                    System.out.println(System.currentTimeMillis() + " :PLAYER 2 WIN");
                                    player1_out.println("LOSS:" + prevMove);
                                    player2_out.println("WON:" + prevMove);
                                    gstate = GameState.PLAYER2_WIN;
                                    break;
                                case DRAW:
                                    player1_out.println("DRAW:" + prevMove);
                                    player2_out.println("DRAW:" + prevMove);
                                    gstate = GameState.DRAW;
                                    break;
                                case PLAYING:
                                    System.out.println(System.currentTimeMillis() + " :NO_WINNER_YET");
                                    gstate = GameState.PLAYING;
                                    System.out.println(System.currentTimeMillis() + "GOOD MOVE:" + prevMove);
                                    player1_out.println("GOOD MOVE:" + prevMove);
                                    break;
                                default:
                                    System.out.println(System.currentTimeMillis() + " :PLAYER1_ILLEGAL_MOVE");
                                    player1_out.println("BAD MOVE:");
                                    prevMove = -1;
                                    gstate = GameState.BAD_MOVE;
                            }
                            break;
                        default:
                            System.out.println(System.currentTimeMillis() + " :PLAYER1_CLOSED_CONNECTION");
                            gstate = GameState.FORCED_GAME_OVER;
                            sstate = ServerState.CLOSED;
                    }
                }
            }

            //==========================================
            if (gstate == GameState.PLAYING) {
                gstate = GameState.NO_MOVE;
            }
            if (sstate != ServerState.CLOSED) {
                //p2 turn
                while (gstate == GameState.NO_MOVE) {
                    player2_out.println("TURN:" + prevMove);

                    System.out.println(System.currentTimeMillis() + " :waiting for player 2 turn...");
                    p2message = player2_in.readLine();
                    System.out.println(System.currentTimeMillis() + " :" + p2message + "\n");

                    String[] splitMes = p2message.split(":");

                    switch (splitMes[0]) {
                        case "PLAY":
                            prevMove = Integer.parseInt(splitMes[1]);
                            gstate = attemptTurn(prevMove);
                            switch (gstate) {
                                case PLAYER1_WIN:
                                    player1_out.println("WON:"+prevMove);
                                    player2_out.println("LOSS:"+prevMove);
                                    gstate = GameState.PLAYER1_WIN;
                                    break;
                                case PLAYER2_WIN:
                                    player2_out.println("WON:"+prevMove);
                                    player1_out.println("LOSS:"+prevMove);
                                    gstate = GameState.PLAYER2_WIN;
                                    break;
                                case DRAW:
                                    player1_out.println("DRAW:"+prevMove);
                                    player2_out.println("DRAW:"+prevMove);
                                    gstate = GameState.DRAW;
                                    break;
                                case PLAYING:
                                    System.out.println(System.currentTimeMillis() + " :NO_WINNER_YET");
                                    gstate = GameState.PLAYING;
                                    System.out.println(System.currentTimeMillis() + "GOOD MOVE:" + prevMove);
                                    player2_out.println("GOOD MOVE:" + prevMove);
                                    break;
                                default:
                                    System.out.println(System.currentTimeMillis() + " :PLAYER 2_ILLEGAL_MOVE");
                                    player1_out.println("BAD MOVE:");
                                    prevMove = -1;
                                    gstate = GameState.BAD_MOVE;
                            }
                            break;
                        default:
                            System.out.println(System.currentTimeMillis() + " :PLAYER2_CLOSED_CONNECTION");
                            gstate = GameState.FORCED_GAME_OVER;
                            sstate = ServerState.CLOSED;
                    }
                }
            }
        }
    }

    /**
     * once both players are connected, let them know the server is ready to
     * start and wait for the response. If one or both of the players decline to start
     * close the server and shut everything down.
     * If both players confirm the start, begin a new game.
     * @throws IOException
     */
    private void waitForStart() throws IOException {
        String p1message = "";
        String p2message = "";

        //let the clients know the server is ready for start
        player1_out.println("READY FOR START?:");
        player2_out.println("READY FOR START?:");

        gstate = GameState.WAITING_TO_START;

        //here the while loop isn't necessary, but I thought it might be
        //more robust, we will see.
        while (gstate == GameState.WAITING_TO_START) {
            System.out.println(System.currentTimeMillis() + " :waiting for player 1 ready...");
            p1message = player1_in.readLine();
            p2message = player2_in.readLine();

            if (p1message.equals("START:") && p2message.equals("START:")) {
                //both players confirmed start
                System.out.println(System.currentTimeMillis() + " :players are ready to start");
                gstate = GameState.PLAYING;
                connectFourGame.newGame();
            } else {
                System.out.println(System.currentTimeMillis() + " :players have quit quit");
                //make sure the clients close properly
                player1_out.println("QUIT:");
                player2_out.println("QUIT:");
                gstate = GameState.FORCED_GAME_OVER;
                sstate = ServerState.CLOSED;
            }
        }
    }

    /**
     * This method might be superfluous, as I could just call doTurn in
     * ConnectFourGame, but this is more convenient if I ever decide that a turn
     * consists of more than doing the turn in the GameModel
     * @param col to attempt the game move at
     * @return the gamestate after updating the board and checking for winners
     */
    private GameState attemptTurn(int col)
    {
        return connectFourGame.doTurn(col);
    }


    /**
     *  Make sure all the connections are closed before we exit.
     * @throws IOException
     */
    private void cleanUp() throws IOException
    {
        System.out.println(System.currentTimeMillis()+ " :closing connections...");
        player1_in.close();
        player1_out.close();
        player1.close();

        player2_in.close();
        player2_out.close();
        player2.close();

        System.out.println(System.currentTimeMillis()+ " :connections closed");
    }

    /**
     * starts the sever side code as well as the game logistics
     *
     * @param args ignored
     */
    public static void main(String[] args)
    {
        Thread server = new Thread(new ConnectFourServer());
        server.start();
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis()+ " :Good Bye!");
    }


}
