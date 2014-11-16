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
    private static final int depth = 4;

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Side color) {
        super(game, color);
    }
    
    /** A move class. */
    private class Move{
        int move;
        int value;
        public Move(int move, int value) {
            this.move = move;
            this.value = value;
        }
        public int getMove() {
            return move;
        }
        public void setMove(int move) {
            this.move = move;
        }
        public int getValue() {
            return value;
        }
        public void setValue(int value) {
            this.value = value;
        }
    }

    @Override
    void makeMove() {
        // FIXME
        Side player = this.getSide();
        Board b = this.getBoard();
        int cutoff = 10000;
        Move move = findBestMove(player, b, depth, cutoff);
        int r = b.row(move.getValue());
        int c = b.col(move.getValue());
        getGame().message("Blue moves %d %d.\n", r, c);
        getGame().makeMove(r, c);
    }

    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of D
     *  (where D == 0 denotes statically evaluating just the next move).
     *  If MOVES is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call. */
    private Move findBestMove(Side player, Board b, int d, int cutoff) {
        // REPLACE WITH SOLUTION
        if (d == 0) {
            return guessBestMove(player, b, cutoff);
        }
        ArrayList<Move> moves = validMoves(player, b, cutoff);
        if (moves == null) {
            return new Move(-1, staticEval(player, b));
        }
        Board copyOfBoard = new MutableBoard(b);
        Move bestMove = new Move(Integer.MIN_VALUE, cutoff);
        for (Move m : moves) {
            copyOfBoard.addSpot(player, m.getMove());
            int negaBestValue = bestMove.getValue() * -1;
            Move newMove = findBestMove(player.opposite(), copyOfBoard, d - 1, negaBestValue);
            copyOfBoard.undo();
            int negaNewValue = newMove.getValue() * -1;
            if (negaNewValue > bestMove.getValue()) {
                m.setValue(negaNewValue);
                bestMove = m;
                if (bestMove.getValue() >= cutoff) {
                    break;
                }
            }
        }
        return bestMove;
    }
    
    /** Guess best move for player p. 
     *  @param player player
     *  @param b current board
     *  @param cutoff cutoff value
     *  @return a best position for move. */
    private Move guessBestMove(Side player, Board b, int cutoff) {
        Board copyOfBoard = new MutableBoard(b);
        ArrayList<Move> moves = validMoves(player, b, cutoff);
        if (moves == null) {
            return new Move(-1, staticEval(player, b));
        }
        Move bestMove = new Move(Integer.MIN_VALUE, cutoff);
        for (Move m : moves) {
            copyOfBoard.addSpot(player, m.getMove());
            m.setValue(staticEval(player, copyOfBoard));
            copyOfBoard.undo();
            if (m.getValue() > bestMove.getValue()) {
                bestMove = m;
                if (bestMove.getValue() >= cutoff) {
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
            return 10000;
        } else if (b.getWinner() == opponent) {
            return -10000;
        }
        return numForPlayer - numForOpponent;
    }

    /** Find current available move for player p. */
    private ArrayList<Move> validMoves(Side player, Board b, int cutoff) {
        if (b.getWinner() != null) {
            return null;
        }
        ArrayList<Move> moves = new ArrayList<Move>();
        int boardSize = b.size() * b.size();
        Side opponent = player.opposite();
        for (int i = 0; i < boardSize; i += 1) {
            Square s = b.get(i);
            if (s.getSide() != opponent) {
                moves.add(new Move(i, cutoff));
            }
        }
        return moves;
    }
}
