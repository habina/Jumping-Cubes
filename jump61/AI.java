// This file contains a SUGGESTION for the structure of your program.  You
// may change any of it, or add additional files to this directory (package),
// as long as you conform to the project specification.

// Comments that start with "//" are intended to be removed from your
// solutions.
package jump61;

import java.util.ArrayList;

/** An automated Player.
 *  @author Dasheng Chen
 */
class AI extends Player {

    /** Time allotted to all but final search depth (milliseconds). */
    private static final long TIME_LIMIT = 15000;

    /** Number of calls to minmax between checks of elapsed time. */
    private static final long TIME_CHECK_INTERVAL = 10000;

    /** Number of milliseconds in one second. */
    private static final double MILLIS = 1000.0;

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Side color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        // FIXME
        int[] move = new int[2];
        if (getGame().getMove(move)) {
            int r = move[0];
            int c = move[1];
            getGame().message("Blue moves %d %d.\n", r, c);
            getGame().makeMove(r, c);
        }
    }

    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of D
     *  (where D == 0 denotes statically evaluating just the next move).
     *  If MOVES is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call. */
    private int minmax(Side p, Board b, int d, int cutoff,
                       ArrayList<Integer> moves) {
        // REPLACE WITH SOLUTION
        
        return 0;
    }
    
    /** Guess best move for player p. 
     *  @param player player
     *  @param b current board
     *  @param cutoff cutoff value
     *  @return a best position for move. */
    private int guessBestMove(Side player, Board b, int cutoff) {
        Board copyOfBoard = new MutableBoard(b);
        ArrayList<Integer> moves = validMoves(player, b);
        int bestMove = 0;
        int bestMoveValue = Integer.MIN_VALUE;
        for (Integer n : moves) {
            copyOfBoard.addSpot(player, n);
            int newMoveValue = staticEval(player, copyOfBoard);
            if (newMoveValue > bestMoveValue) {
                bestMoveValue = newMoveValue;
                bestMove = n;
                if (bestMoveValue >= cutoff) {
                    break;
                }
            }
        }
        return bestMove;
    }

    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    private int staticEval(Side player, Board b) {
        // REPLACE WITH SOLUTIONs
        int numForPlayer = b.numOfSide(player);
        Side opponent = player.opposite();
        int numForOpponent = b.numOfSide(opponent);
        if (b.getWinner() == player) {
            return Integer.MAX_VALUE;
        } else if (b.getWinner() == opponent) {
            return Integer.MIN_VALUE;
        }
        return numForPlayer - numForOpponent;
    }

    /** Find current available move for player p. */
    private ArrayList<Integer> validMoves(Side p, Board b) {
        if (b.getWinner() != null) {
            return null;
        }
        ArrayList<Integer> moves = new ArrayList<Integer>();
        int boardSize = b.size();
        for (int i = 0; i < boardSize; i += 1) {
            Square s = b.get(i);
            if (s.getSide() == p) {
                moves.add(i);
            }
        }
        return moves;
    }
}
