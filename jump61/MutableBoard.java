// This file contains a SUGGESTION for the structure of your program.  You
// may change any of it, or add additional files to this directory (package),
// as long as you conform to the project specification.

// Comments that start with "//" are intended to be removed from your
// solutions.
package jump61;

import static jump61.Square.square;

import java.util.Iterator;
import java.util.Stack;

import com.sun.org.apache.bcel.internal.generic.CASTORE;

/** A Jump61 board state that may be modified.
 *  @author Dasheng Chen
 */
class MutableBoard extends Board {

    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        // FIXME
//        this._size = N;
//        this._boardStack = new Stack<MutableBoard>();
//        this._boardArray = new Square[_size * _size];
//        for (int i = 0; i < _boardArray.length; i += 1) {
//            _boardArray[i] = Square.square(Side.WHITE, 1);
//        }
//        this._boardStack.push(this);
        this.clear(N);
    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  undo history is clear. */
    MutableBoard(Board board0) {
        // FIXME
        this.copy(board0);
    }

    @Override
    void clear(int N) {
        // FIXME
        this._balanced = true;
        this._size = N;
        this._boardStack = new Stack<MutableBoard>();
        this._boardArray = new Square[_size * _size];
        for (int i = 0; i < _boardArray.length; i += 1) {
            _boardArray[i] = Square.square(Side.WHITE, 1);
        }
//        ConstantBoard constBoard = new ConstantBoard(this);
//        this._boardStack.push(constBoard);
        announce();
    }

    @Override
    void copy(Board board) {
        // FIXME
        this._boardStack = new Stack<MutableBoard>();
        this._size = board.size();
        this._boardArray = new Square[_size * _size];
        for (int i = 0; i < _boardArray.length; i += 1) {
            Square square = board._boardArray[i];
            _boardArray[i] = Square.square(square.getSide(), square.getSpots());
        }
//        ConstantBoard constBoard = new ConstantBoard(this);
//        this._boardStack.push(constBoard);
    }

    /** Copy the contents of BOARD into me, without modifying my undo
     *  history.  Assumes BOARD and I have the same size. */
    private void internalCopy(MutableBoard board) {
//        private void internalCopy(MutableBoard board) {
        // FIXME
        for (int i = 0; i < _boardArray.length; i += 1) {
            Square square = board._boardArray[i];
            _boardArray[i] = Square.square(square.getSide(), square.getSpots());
        }
    }

    @Override
    int size() {
        // REPLACE WITH SOLUTION
        return this._size;
    }

    @Override
    Square get(int n) {
        // REPLACE WITH SOLUTION
        return this._boardArray[n];
    }

    @Override
    int numOfSide(Side side) {
        // REPLACE WITH SOLUTION
        String sideName = side.name();
        int count = 0;
        for (int i = 0; i < _boardArray.length; i += 1) {
            String str = _boardArray[i].getSide().name();
            if (sideName.compareToIgnoreCase(str) == 0) {
                count += 1;
            }
        }
        return count;
    }

    @Override
    int numPieces() {
        // REPLACE WITH SOLUTION
        int count = 0;
        for (int i = 0; i < _boardArray.length; i += 1) {
            count += _boardArray[i].getSpots();
        }
        return count;
    }

    @Override
    void addSpot(Side player, int r, int c) {
        // FIXME
        this.addSpot(player, this.sqNum(r, c));
//        announce();
    }

    @Override
    void addSpot(Side player, int n) {
        // FIXME
        int spots = this._boardArray[n].getSpots() + 1;
        this.markUndo();
        set(n, spots, player);
        this.balanceBoard();
//        announce();
    }
    
    /** Make the board balance. */
    void balanceBoard() {
        if (_balanced || getWinner() != null) {
            return;
        }
        _balanced = true;
        for (int i = 0; i < _boardArray.length; i += 1) {
            Square square = _boardArray[i];
            int neighborNum = neighbors(i);
            if (neighborNum < square.getSpots()) {
                _balanced = false;
                Side side = square.getSide();
                _boardArray[i] = square(side, 1);
                int[] neighborList = getNeighborIndex(i, neighborNum);
                for (int nIndex = 0; nIndex < neighborNum; nIndex += 1) {
                    int indexForBalance = neighborList[nIndex];
                    Square squareTemp = _boardArray[indexForBalance];
                    int newSpots = squareTemp.getSpots() + 1;
                    _boardArray[indexForBalance] = square(side, newSpots);
                }
            }
        }
        balanceBoard();
    }

    /** Return index of currentIndex's neighbor.
     *  @param currentIndex current position index
     *  @param neighborNum number of neighbors
     *  @return a integer array
     */
    int[] getNeighborIndex(int currentIndex, int neighborNum) {
        int[] result = new int[neighborNum];
        int count = 0;
        int row = this.row(currentIndex);
        int col = this.col(currentIndex);
        if ((row - 1) < this._size && (row - 1) > 0) {
            result[count] = sqNum(row - 1, col);
            count += 1;
        }
        if ((row + 1) <= this._size) {
            result[count] = sqNum(row + 1, col);
            count += 1;
        }
        if ((col - 1) < this._size && (col - 1) > 0) {
            result[count] = sqNum(row, col - 1);
            count += 1;
        }
        if ((col + 1) <= this._size) {
            result[count] = sqNum(row, col + 1);
            count += 1;
        }
        return result;
    }

    @Override
    void set(int r, int c, int num, Side player) {
//        internalSet(sqNum(r, c), square(player, num));
        set(sqNum(r, c), num, player);
    }

    @Override
    void set(int n, int num, Side player) {
        if (this.isLegal(player, n)) {
            internalSet(n, square(player, num));
//            announce();
        }
    }

    @Override
    void undo() {
        // FIXME
        if (!this._boardStack.empty()) {
            MutableBoard oldBoard = this._boardStack.pop();
            this.internalCopy(oldBoard);
        }
    }

    /** Record the beginning of a move in the undo history. */
    private void markUndo() {
        // FIXME
        MutableBoard mBoard = new MutableBoard(this);
//        Board constBoard = new ConstantBoard(mBoard);
//        System.out.println("marking:");
//        System.out.println(mBoard.toString());
        this._boardStack.push(mBoard);
    }

    /** Set the contents of the square with index IND to SQ. Update counts
     *  of numbers of squares of each color.  */
    private void internalSet(int ind, Square sq) {
        // FIXME
        this._boardArray[ind] = sq;
        this._balanced = false;
        announce();
    }

    /** Notify all Observers of a change. */
    private void announce() {
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MutableBoard)) {
            return obj.equals(this);
        } else {
            // REPLACE WITH SOLUTION
            try {
                MutableBoard mtBoard = (MutableBoard) obj;
                if (mtBoard.size() != this._size
                    || mtBoard.hashCode() != this.hashCode()) {
                    return false;
                }
                for (int i = 0; i < this._size; i += 1) {
                    Square objSquare = mtBoard.get(i);
                    Square thisSquare = this._boardArray[i];
                    if (!thisSquare.equals(objSquare)) {
                        return false;
                    }
                }
            } catch (ClassCastException e) {
                System.out.println("Can't cast obj to MutableBoard");
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        // REPLACE WITH SOLUTION.  RETURN A NUMBER THAT IS THE SAME FOR BOARDS
        // WITH IDENTICAL CONTENTS (IT NEED NOT BE DIFFERENT IF THE BOARDS
        // DIFFER.)  THE CURRENT STATEMENT WORKS, BUT IS NOT PARTICULARLY
        // EFFICIENT.
        int result = 11;
        int code = this._size;
        final int prime = 31;
        for (int i = 0; i < _boardArray.length; i += 1) {
            code += _boardArray[i].hashCode();
        }
        // Need to consider history stack?
//        Iterator<MutableBoard> iter = _boardStack.iterator();
//        while (iter.hasNext()) {
//            code += iter.next().hashCode();
//        }
        result = result * prime + code;
        return result;
    }

    /** History stack. */
    private Stack<MutableBoard> _boardStack;
    /** True if board is balanced. */
    private boolean _balanced;
}
