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
    
    /** Best Move. */
    private int bestMove = -1;

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
        double cutoff = Integer.MAX_VALUE;
        ArrayList<Integer> moves = validMoves(player, b);
        findBestMove(player, b, depth, cutoff, moves);
        int r = b.row(bestMove);
        int c = b.col(bestMove);
        getGame().message("%s moves %d %d.\n", getSide().toCapitalizedString(), r, c);
//        getGame().message("%d\n", getBoard().numPieces());
        getGame().makeMove(r, c);
    }

    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of D
     *  (where D == 0 denotes statically evaluating just the next move).
     *  If MOVES is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call. */
    private double findBestMove(Side player, Board b, int d, double cutoff, ArrayList<Integer> moves) {
        boolean isMaximizer;
        if (player == getSide()) {
            isMaximizer = true;
        } else {
            isMaximizer = false;
        }
        if (moves == null) {
            if (isMaximizer) {
                return Integer.MIN_VALUE;
            } else {
                return Integer.MAX_VALUE;
            }
        }
        if (d == 0) {
            return guessBestMove(player, b, cutoff, moves, isMaximizer);
        }
        Board copyOfBoard = new MutableBoard(b);
        double currentBestValue;
        if (isMaximizer) {
            currentBestValue = Integer.MIN_VALUE;
        } else {
            currentBestValue = Integer.MAX_VALUE;
        }
        for (Integer m : moves) {
            copyOfBoard.addSpot(player, m);
            ArrayList<Integer> nextMoves = validMoves(player.opposite(), copyOfBoard);
            double newMoveValue = findBestMove(player.opposite(), copyOfBoard, d - 1, currentBestValue, nextMoves);
            copyOfBoard.undo();
            if (isMaximizer) {
                if (newMoveValue >= currentBestValue) {
                    currentBestValue = newMoveValue;
                    if (d == depth) {
                        bestMove = m;
                    }
                    if (currentBestValue >= cutoff) {
                        break;
                    }
                }
            } else {
                if (newMoveValue <= currentBestValue) {
                    currentBestValue = newMoveValue;
                    if (currentBestValue <= cutoff) {
                        break;
                    }
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
    private double guessBestMove(Side player, Board b, double cutoff, ArrayList<Integer> moves, boolean isMaximizer) {
        Board copyOfBoard = new MutableBoard(b);
        double currentBestValue;
        if (isMaximizer) {
            currentBestValue = Integer.MIN_VALUE;
        } else {
            currentBestValue = Integer.MAX_VALUE;
        }
        for (Integer m : moves) {
            copyOfBoard.addSpot(player, m);
            double newBestValue = staticEval(player, copyOfBoard);
            copyOfBoard.undo();
            
            
            if (isMaximizer) {
                if (newBestValue >= currentBestValue) {
                    currentBestValue = newBestValue;
                    bestMove = m;
                    if (currentBestValue >= cutoff) {
                        break;
                    }
                }
            } else {
                if (newBestValue <= currentBestValue) {
                    currentBestValue = newBestValue;
                    if (currentBestValue <= cutoff) {
                        break;
                    }
                }
            }
            
            
        }
        return currentBestValue;
    }

    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    private double staticEval(Side player, Board b) {
        // REPLACE WITH SOLUTIONs
        int numForPlayer = b.numOfSide(player);
        Side opponent = player.opposite();
//        int numForOpponent = b.numOfSide(opponent);
        double sum = 1.0 * b.numPieces();
        return numForPlayer / sum;
    }

    /** Find current available move for player p. */
    private ArrayList<Integer> validMoves(Side player, Board b) {
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
