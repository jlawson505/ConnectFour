package Player;

/**
 * enum type to keep track of the current player state
 *
 * Created by James Lawson on 12/21/2016.
 */
public enum PlayerState
{
    OTHER_PLAYER_TURN,
    THIS_PLAYER_TURN,
    WON,
    LOST,
    DRAW,
    WAITING,
    STARTING,
    FORCED_GAME_OVER
}
