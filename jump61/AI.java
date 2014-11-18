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
    private static final int DEPTH = 4;

    /** Best Move. */
    private int bestMove = -1;

    /** Best Value. */
    private static final int BESTVALUE = 1000;
    
    /** Time to start make move. */
    private long startTime;

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Side color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        Side player = this.getSide();
        Board b = this.getBoard();
        double cutoff = Double.MAX_VALUE;
        ArrayList<Integer> moves = validMoves(player, b);
        startTime = System.currentTimeMillis();
//        double response = findBestMove(player, b, DEPTH, cutoff, moves);
        try{
            findBestMove(player, b, DEPTH, cutoff, moves);
        } catch(GameException e) {
            int rand = getGame().randInt(moves.size());
            getGame().message("AI Time out ");
            bestMove = moves.get(rand);
        }
        int r = b.row(bestMove);
        int c = b.col(bestMove);
        getGame().message("%s moves %d %d.\n", getSide().toCapitalizedString(),
            r, c);
        getGame().makeMove(r, c);
    }

    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of D
     *  (where D == 0 denotes statically evaluating just the next move).
     *  If MOVES is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call.
     *  @param player a player
     *  @param b current board
     *  @param d depth
     *  @param cutoff cutoff value
     *  @param moves available moves
     *  @return return a static evaluation
     */
    private double findBestMove(Side player, Board b, int d, double cutoff,
        ArrayList<Integer> moves) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime > TIME_LIMIT) {
            throw GameException.timeOutAI();
        }
        boolean isMaximizer;
        if (player == getSide()) {
            isMaximizer = true;
        } else {
            isMaximizer = false;
        }
        if (moves == null) {
            if (isMaximizer) {
                return BESTVALUE * (-1);
            } else {
                return BESTVALUE;
            }
        }
        if (d == AI.DEPTH) {
            bestMove = moves.get(0);
        }
        if (d == 0) {
            return guessBestMove(player, b, cutoff, moves, isMaximizer);
        }
        Board copyOfBoard = new MutableBoard(b);
        double currentBestValue;
        if (isMaximizer) {
            currentBestValue = Double.MIN_VALUE;
        } else {
            currentBestValue = Double.MAX_VALUE;
        }
        for (Integer m : moves) {
            copyOfBoard.addSpot(player, m);
            ArrayList<Integer> nextMoves = validMoves(player.opposite(),
                copyOfBoard);
            double newMoveValue = findBestMove(player.opposite(), copyOfBoard,
                d - 1, currentBestValue, nextMoves);
            copyOfBoard.undo();
            if (isMaximizer) {
                if (newMoveValue > currentBestValue) {
                    currentBestValue = newMoveValue;
                    if (d == DEPTH) {
                        bestMove = m;
                    }
                    if (currentBestValue >= cutoff) {
                        break;
                    }
                }
            } else {
                if (newMoveValue < currentBestValue) {
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
     *  @param moves available moves
     *  @param isMaximizer check if current level is maximizer
     *  @return a best board static value. */
    private double guessBestMove(Side player, Board b, double cutoff,
        ArrayList<Integer> moves, boolean isMaximizer) {
        Board copyOfBoard = new MutableBoard(b);
        double currentBestValue;
        if (isMaximizer) {
            currentBestValue = Double.MIN_VALUE;
        } else {
            currentBestValue = Double.MAX_VALUE;
        }
        for (Integer m : moves) {
            copyOfBoard.addSpot(player, m);
            double newBestValue = staticEval(player, copyOfBoard);
            copyOfBoard.undo();
            if (isMaximizer) {
                if (newBestValue > currentBestValue) {
                    currentBestValue = newBestValue;
                    if (currentBestValue >= cutoff) {
                        break;
                    }
                }
            } else {
                if (newBestValue < currentBestValue) {
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
     *  Higher is better for P
     *  @param player current player
     *  @param b current board
     *  @return return evaluation of the board
     */
    private double staticEval(Side player, Board b) {
        int numForPlayer = b.numOfSide(player);
        double sum = 1.0 * b.numPieces();
        return numForPlayer / sum;
    }

    /** Find current available move for player p.
     *  @param player current player
     *  @param b current board
     *  @return return all available moves
     */
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
