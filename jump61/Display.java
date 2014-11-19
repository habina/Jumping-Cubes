package jump61;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

import java.io.PrintWriter;
import java.io.Writer;

import java.util.Observable;
import java.util.Observer;

import static jump61.Side.*;

/** The GUI controller for jump61.  To require minimal change to textual
 *  interface, we adopt the strategy of converting GUI input (mouse clicks)
 *  into textual commands that are sent to the Game object through a
 *  a Writer.  The Game object need never know where its input is coming from.
 *  A Display is an Observer of Games and Boards so that it is notified when
 *  either changes.
 *  @author Dasheng Chen
 */
class Display extends TopLevel implements Observer {

    /** A new window with given TITLE displaying GAME, and using COMMANDWRITER
     *  to send commands to the current game. */
    Display(String title, Game game, Writer commandWriter) {
        super(title, true);
        _game = game;
        _board = game.getBoard();
        _commandOut = new PrintWriter(commandWriter);
        _boardWidget = new BoardWidget(game, _commandOut);
        addLabel("", "Winner", new LayoutSpec("y", 1));
        addButton("New", "newGame", new LayoutSpec("y", 1));
        addButton("Quit", "quit", new LayoutSpec("y", 1));
        add(_boardWidget, new LayoutSpec("y", 0, "width", 3));
        addMenuButton("Game->New Game", "newGame");
        addMenuButton("Game->Auto Game", "auto");
        addMenuButton("Game->Quit", "quit");
        addMenuButton("Size->3 * 3", "three");
        addMenuButton("Size->4 * 4", "four");
        addMenuButton("Size->5 * 5", "five");
        addMenuButton("Size->6 * 6", "six");
        addMenuButton("Size->7 * 7", "seven");
        addMenuButton("Color->Red", "red");
        addMenuButton("Color->Blue", "blue");
        addMenuButton("Color->1 V 1", "pkMode");
        _board.addObserver(this);
        _game.addObserver(this);
        display(true);
    }

    /** Display what action AI takes.
     *  @param s winner to announce
     */
    public void setWinner(String s) {
        if (s.equals("")) {
            setLabel("Winner", String.format(""));
        } else {
            setLabel("Winner", String.format("%s wins.", s));
        }
    }

    /** Response to "pkMode" button click. */
    void pkMode(String dummy) {
        _commandOut.println("manual red");
        _commandOut.println("manual blue");
        newGame(dummy);
    }

    /** Response to "red" button click. */
    void red(String dummy) {
        _commandOut.println("manual red");
        _commandOut.println("auto blue");
        newGame(dummy);
    }

    /** Response to "blue" button click. */
    void blue(String dummy) {
        _commandOut.println("auto red");
        _commandOut.println("manual blue");
        newGame(dummy);
    }

    /** Response to "three" button click. */
    void three(String dummy) {
        _commandOut.println("size 3");
        newGame(dummy);
    }

    /** Response to "four" button click. */
    void four(String dummy) {
        _commandOut.println("size 4");
        newGame(dummy);
    }

    /** Response to "five" button click. */
    void five(String dummy) {
        _commandOut.println("size 5");
        newGame(dummy);
    }

    /** Response to "six" button click. */
    void six(String dummy) {
        _commandOut.println("size 6");
        newGame(dummy);
    }

    /** Response to "ten" button click. */
    void seven(String dummy) {
        _commandOut.println("size 7");
        newGame(dummy);
    }

    /** Response to "Auto" button click. */
    void newGame(String dummy) {
        if (autoMove) {
            autoMove = false;
            _commandOut.println("manual red");
            _commandOut.println("auto blue");
        }
        setWinner("");
        _commandOut.println("new");
    }

    /** Response to "Auto" button click. */
    void auto(String dummy) {
        autoMove = true;
        _commandOut.println("auto red");
        _commandOut.println("auto blue");
        _commandOut.println("start");
    }

    /** Response to "Quit" button click. */
    void quit(String dummy) {
        System.exit(0);
    }

    @Override
    public void update(Observable obs, Object obj) {
        _boardWidget.update();
        Side s = _board.getWinner();
        if (s != null) {
            setWinner(s.toCapitalizedString());
        }
        frame.pack();
        _boardWidget.repaint();
    }

    /** True if auto mode is on. */
    private boolean autoMove = false;
    /** The current game that I am controlling. */
    private Game _game;
    /** The board maintained by _game (readonly). */
    private Board _board;
    /** The widget that displays the actual playing board. */
    private BoardWidget _boardWidget;
    /** Writer that sends commands to our game. */
    private PrintWriter _commandOut;
}
