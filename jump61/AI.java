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
    
    /** Deepth for explore GameTree. */
    private static final int depth = 3;
    
    /** Best Move. */
    private int bestMove;

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Side color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        // FIXME
        Side player = this.getSide();
        Board b = this.getBoard();
        int cutoff = 10000;
        findBestMove(player, b, depth, cutoff);
//        int move = guessBestMove(player, b, cutoff);
        int r = b.row(bestMove);
        int c = b.col(bestMove);
        getGame().message("%s moves %d %d.\n", getSide().toCapitalizedString(), r, c);
        getGame().makeMove(r, c);
    }

    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of D
     *  (where D == 0 denotes statically evaluating just the next move).
     *  If MOVES is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call. */
    private int findBestMove(Side player, Board b, int d, int cutoff) {
        ArrayList<Integer> moves = validMoves(player, b, cutoff);
        if (moves == null) {
            return staticEval(player, b);
        }
        if (d == 0) {
            return guessBestMove(player, b, cutoff);
        }
        Board copyOfBoard = new MutableBoard(b);
        int currentBestValue = cutoff * -1;
        for (Integer m : moves) {
            copyOfBoard.addSpot(player, m);
            int negaBestValue = currentBestValue * -1;
            int newMoveValue = findBestMove(player.opposite(), copyOfBoard, d - 1, negaBestValue);
            copyOfBoard.undo();
            int negaNewValue = newMoveValue * -1;
            if (negaNewValue > currentBestValue) {
                currentBestValue = negaNewValue;
                if (d == depth){
                    bestMove = m;
                }
                if (currentBestValue >= cutoff) {
                    break;
                }
            }
        }
        return currentBestValue;
    }
    
    /** Guess best move for player p. 
     *  @param player player
     *  @param b current board
     *  @param cutoff cutoff value
     *  @return a best board static value. */
    private int guessBestMove(Side player, Board b, int cutoff) {
        Board copyOfBoard = new MutableBoard(b);
        ArrayList<Integer> moves = validMoves(player, b, cutoff);
        if (moves == null) {
            System.out.println("guessBestMove: no more moves");
            return -2;
        }
        int currentBestValue = -10001;
        for (Integer m : moves) {
            copyOfBoard.addSpot(player, m);
            int newBestValue = staticEval(player, copyOfBoard);
            copyOfBoard.undo();
            if (newBestValue > currentBestValue) {
                currentBestValue = newBestValue;
                if (getSide() == player) {
                    bestMove = m;
                }
                if (currentBestValue >= cutoff) {
                    break;
                }
            }
        }
        return currentBestValue;
    }

    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    private int staticEval(Side player, Board b) {
        // REPLACE WITH SOLUTIONs
        int numForPlayer = b.numOfSide(player);
        Side opponent = player.opposite();
        int numForOpponent = b.numOfSide(opponent);
        if (b.getWinner() == player) {
            return 10000;
        } else if (b.getWinner() == opponent) {
            return -10001;
        }
        return numForPlayer - numForOpponent;
    }

    /** Find current available move for player p. */
    private ArrayList<Integer> validMoves(Side player, Board b, int cutoff) {
        if (b.getWinner() != null) {
            return null;
        }
        ArrayList<Integer> moves = new ArrayList<Integer>();
        int boardSize = b.size() * b.size();
        Side opponent = player.opposite();
        for (int i = 0; i < boardSize; i += 1) {
            Square s = b.get(i);
            if (s.getSide() != opponent) {
                moves.add(i);
            }
        }
        return moves;
    }
}
