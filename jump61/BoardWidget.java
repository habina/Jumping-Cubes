package jump61;

import ucb.gui.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import java.io.PrintWriter;

import static jump61.Side.*;

/** A GUI component that displays a Jump61 board, and converts mouse clicks
 *  on that board to commands that are sent to the current Game.
 *  @author Dasheng Chen
 */
class BoardWidget extends Pad {

    /** Length of the side of one square in pixels. */
    private static final int SQUARE_SIZE = 50;
    /** Width and height of a spot. */
    private static final int SPOT_DIM = 8;
    /** Minimum separation of center of a spot from a side of a square. */
    private static final int SPOT_MARGIN = 10;
    /** Width of the bars separating squares in pixels. */
    private static final int SEPARATOR_SIZE = 3;
    /** Width of square plus one separator. */
    private static final int SQUARE_SEP = SQUARE_SIZE + SEPARATOR_SIZE;
    /** Unit distance for spots. */
    private static final int SPOTS_UNIT = 12;

    /** Colors of various parts of the displayed board. */
    private static final Color
        NEUTRAL = Color.WHITE,
        SEPARATOR_COLOR = Color.BLACK,
        SPOT_COLOR = Color.BLACK,
        RED_TINT = new Color(255, 200, 200),
        BLUE_TINT = new Color(200, 200, 255);

    /** A new BoardWidget that monitors and displays GAME and its Board, and
     *  converts mouse clicks to commands to COMMANDWRITER. */
    BoardWidget(Game game, PrintWriter commandWriter) {
        _game = game;
        _board = _bufferedBoard = game.getBoard();
        _side = _board.size() * SQUARE_SEP + SEPARATOR_SIZE;
        setPreferredSize(_side, _side);
        setMouseHandler("click", this, "doClick");
        _commandOut = commandWriter;
    }

    /* .update and .paintComponent are synchronized because they are called
     *  by three different threads (the main thread, the thread that
     *  responds to events, and the display thread.  We don't want the
     *  saved copy of our Board to change while it is being displayed. */

    /** Update my display depending on any changes to my Board.  Here, we
     *  save a copy of the current Board (so that we can deal with changes
     *  to it only when we are ready for them), and resize the Widget if the
     *  size of the Board should change. */
    synchronized void update() {
        _bufferedBoard = new MutableBoard(_board);
        int side0 = _side;
        _side = _board.size() * SQUARE_SEP + SEPARATOR_SIZE;
        if (side0 != _side) {
            setPreferredSize(_side, _side);
        }
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        g.setColor(NEUTRAL);
        g.fillRect(0, 0, _side, _side);
        g.setColor(SEPARATOR_COLOR);
        for (int k = 0; k < _side; k += SQUARE_SEP) {
            g.fillRect(0, k, _side, SEPARATOR_SIZE);
            g.fillRect(k, 0, SEPARATOR_SIZE, _side);
        }
        for (int i = 0; i < _board.size(); i += 1) {
            for (int j = 0; j < _board.size(); j += 1) {
                displaySpots(g, i, j);
            }
        }
    }

    /** Color and display the spots on the square at row R and column C
     *  on G.  (Used by paintComponent). */
    private void displaySpots(Graphics2D g, int r, int c) {
        int x = toCoord(r);
        int y = toCoord(c);
        int n = sqNum(r, c);
        if (_board.exists(n)) {
            int spotsNum = _board.get(n).getSpots();
            Side side = _board.get(n).getSide();
            if (side == WHITE) {
                drawSpots(g, spotsNum, x, y, NEUTRAL);
            } else if (side == RED) {
                drawSpots(g, spotsNum, x, y, RED_TINT);
            } else if (side == BLUE) {
                drawSpots(g, spotsNum, x, y, BLUE_TINT);
            }
        }

    }

    /** Return the square number of row R, column C. */
    int sqNum(int r, int c) {
        return c + r * _board.size();
    }

    /** Return the pixel distance corresponding to A rows or columns.
     *  @param a rows or columns number
     */
    static int toCoord(int a) {
        return SEPARATOR_SIZE + a * SQUARE_SEP;
    }

    /** Return the index corresponding the pixel distance.
     *  @param a pixel distance
     */
    static int toIndex(int a) {
        return (a - SEPARATOR_SIZE) / SQUARE_SEP;
    }

    /** Draw n spots at the square (X, Y) on G.
     *  @param g graph
     *  @param n index
     *  @param x index
     *  @param y index
     *  @param c color
     */
    private void drawSpots(Graphics2D g, int n, int x, int y, Color c) {
        g.setColor(c);
        g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
        switch (n) {
        case 1:
            spot(g, x + SPOTS_UNIT * 2, y + SPOTS_UNIT * 2);
            break;
        case 2:
            spot(g, x + SPOTS_UNIT, y + SPOTS_UNIT * 2);
            spot(g, x + SPOTS_UNIT * 3, y +  SPOTS_UNIT * 2);
            break;
        case 3:
            spot(g, x + SPOTS_UNIT, y + SPOTS_UNIT);
            spot(g, x + SPOTS_UNIT * 2, y + SPOTS_UNIT * 2);
            spot(g, x + SPOTS_UNIT * 3, y + SPOTS_UNIT * 3);
            break;
        case 4:
            spot(g, x + SPOTS_UNIT, y + SPOTS_UNIT);
            spot(g, x + SPOTS_UNIT, y + SPOTS_UNIT * 3);
            spot(g, x + SPOTS_UNIT * 3, y + SPOTS_UNIT);
            spot(g, x + SPOTS_UNIT * 3, y + SPOTS_UNIT * 3);
            break;
        default:
            break;
        }
    }

    /** Draw one spot centered at position (X, Y) on G. */
    private void spot(Graphics2D g, int x, int y) {
        g.setColor(SPOT_COLOR);
        g.fillOval(x - SPOT_DIM / 2, y - SPOT_DIM / 2, SPOT_DIM, SPOT_DIM);
    }

    /** Respond to the mouse click depicted by EVENT. */
    public void doClick(MouseEvent event) {
        int x = event.getX() - SEPARATOR_SIZE,
            y = event.getY() - SEPARATOR_SIZE;
        int r = toIndex(x) + 1;
        int c = toIndex(y) + 1;
        if (_board.exists(r, c)) {
            _game.makeMove(r, c);
            Side pColor = _board.whoseMove();
            Player player = _game.getPlayer(pColor);
            player.makeMove();
            _commandOut.printf("%d %d%n", r, c);
        }
    }

    /** The Game I am playing. */
    private Game _game;
    /** The Board I am displaying. */
    private Board _board;
    /** An internal snapshot of _board (to prevent race conditions). */
    private Board _bufferedBoard;
    /** Dimension in pixels of one side of the board. */
    private int _side;
    /** Destination for commands derived from mouse clicks. */
    private PrintWriter _commandOut;
}
