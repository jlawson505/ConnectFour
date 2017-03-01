# ConnectFour
Online Multiplayer Game I created using java

to run the server code, the main class is located in 
ConnectFourServer.java

the player client code main class is located in
PlayerClient.java

# Behavior when running
the server can only handle one game at a time.
If either of the players disconnect, the server will exit and will
need to be restarted if you want to play again.
I am hoping to change the server code to be more robust and be able
to handle multiple games at the same time.

Once the players connect, and each new Game subsequentially, they will
be prompted to start a new Game. 

# Important
you will need to edit the "host" value in the player client to reflect 
the host you are running the server on.

In Windows, just type "hostname" in the command prompt to dee the name


