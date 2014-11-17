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
//        ArrayList<Integer> moves = validMoves(player, b);
//        findBestMove(player, b, depth, cutoff, moves);
        alphaBetaMinimax(player, b, depth, Double.MIN_VALUE, Double.MAX_VALUE);
        int r = b.row(bestMove);
        int c = b.col(bestMove);
        getGame().message("%s moves %d %d.\n", getSide().toCapitalizedString(), r, c);
//        getGame().message("%d\n", getBoard().numPieces());
        getGame().makeMove(r, c);
    }

    
    
    private double alphaBetaMinimax(Side player, Board b, int d, double alpha, double beta) {
        ArrayList<Integer> moves = validMoves(player, b);
        boolean isMax = true;
        if (player != getSide()) {
            isMax = false;
        }
        if (moves == null) {
            if (isMax) {
                return Double.MIN_VALUE;
            }
            return Double.MAX_VALUE;
        }

        if (d == 0) {
            guessBestMove(player, b, moves, alpha, beta, isMax);
        }
        
        if (d == AI.depth) {
            bestMove = moves.get(0);
            if (moves.size() == 1) {
                //need to revise
                return -1000;
            }
        }
        
        Board copyOfBoard = new MutableBoard(b);
        
        for (Integer m : moves) {
            copyOfBoard.addSpot(player, m);
            double response = alphaBetaMinimax(player.opposite(), copyOfBoard, d - 1, alpha, beta);
            
            
            if (isMax) {
                if (response >= alpha) {
                    alpha = response;
                    if (d == AI.depth) {
                        bestMove = m;
                    }
                }
                if (alpha >= beta) {
                    return alpha;
                }
            } else {
                if (response <= beta) {
                    beta = response;
                    if (d == AI.depth) {
                        bestMove = m;
                    }
                }
                if (beta <= alpha) {
                    return beta;
                }
            }
        }
        
        if (isMax) {
            return alpha;
        } else {
            return beta;
        }
    }
    
    
    /** Guess best move for player p. 
     *  @param player player
     *  @param b current board
     *  @param cutoff cutoff value
     *  @return a best board static value. */
    private double guessBestMove(Side player, Board b,
        ArrayList<Integer> moves, double alpha, double beta, boolean isMaximizer) {
        Board copyOfBoard = new MutableBoard(b);

        for (Integer m : moves) {
            copyOfBoard.addSpot(player, m);
            double response = staticEval(player, copyOfBoard);
            copyOfBoard.undo();

            if (isMaximizer) {
                if (response >= alpha) {
                    alpha = response;
                    // bestMove = m;
                }
                if (alpha >= beta) {
                    return alpha;
                }
            } else {
                if (response <= beta) {
                    beta = response;
                }
                if (beta <= alpha) {
                    return beta;
                }
            }

        }

        if (isMaximizer) {
            return alpha;
        } else {
            return beta;
        }
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
                return Double.MIN_VALUE;
            } else {
                return Double.MAX_VALUE;
            }
        }
        if (d == AI.depth) {
            bestMove = moves.get(0);
            if (moves.size() == 1) {
                //need to revise
                return -1000;
            }
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
            currentBestValue = Double.MIN_VALUE;
        } else {
            currentBestValue = Double.MAX_VALUE;
        }
        for (Integer m : moves) {
            copyOfBoard.addSpot(player, m);
            double newBestValue = staticEval(player, copyOfBoard);
            copyOfBoard.undo();
            
            
            if (isMaximizer) {
                if (newBestValue >= currentBestValue) {
                    currentBestValue = newBestValue;
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
