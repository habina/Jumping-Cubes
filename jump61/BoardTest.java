package jump61;

import static jump61.Side.*;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of Boards.
 *  @author Dasheng Chen
 */
public class BoardTest {

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testToString() {
        Board B = new MutableBoard(6);
        String output = "===\n";
        output += "    1- 1- 1- 1- 1- 1-\n";
        output += "    1- 1- 1- 1- 1- 1-\n";
        output += "    1- 1- 1- 1- 1- 1-\n";
        output += "    1- 1- 1- 1- 1- 1-\n";
        output += "    1- 1- 1- 1- 1- 1-\n";
        output += "    1- 1- 1- 1- 1- 1-\n";
        output += "===";
        assertEquals(output, B.toString());
    }

    @Test
    public void testClear() {
        Board B = new MutableBoard(6);
        String output = B.toString();
        B.set(1, 3, RED);
        assertEquals(false, B.toString().equals(output));
        B.clear(B._size);
        assertEquals(true, B.toString().equals(output));
    }

    @Test
    public void testCopy() {
        Board A = new MutableBoard(6);
        Board B = new MutableBoard(6);
        String output = A.toString();
        B.set(1, 3, RED);
        assertEquals(false, B.toString().equals(output));
        A.copy(B);
        output = A.toString();
        assertEquals(true, B.toString().equals(output));
        B.set(2, 3, RED);
        B.set(4, 3, RED);
        A = new MutableBoard(B);
        output = A.toString();
        assertEquals(true, B.toString().equals(output));

    }

    @Test
    public void testNumPieces() {
        Board A = new MutableBoard(6);
        assertEquals(6 * 6, A.numPieces());
        A.set(1, 3, RED);
        assertEquals(6 * 6 + 2, A.numPieces());
        A.set(1, 5, RED);
        assertEquals(6 * 6 + 4, A.numPieces());
    }

    @Test
    public void testHashCode() {
        Board A = new MutableBoard(6);
        Board B = new MutableBoard(6);
        assertEquals(A.hashCode(), B.hashCode());
        A.set(1, 3, RED);
        assertNotEquals(A.hashCode(), B.hashCode());
        B.set(1, 3, RED);
        assertEquals(A.hashCode(), B.hashCode());
    }

    @Test
    public void testEquals() {
        Board A = new MutableBoard(6);
        Board B = new MutableBoard(6);
        assertEquals(true, A.equals(B));
        A.set(1, 3, RED);
        assertEquals(false, A.equals(B));
        B.set(1, 3, RED);
        assertEquals(true, A.equals(B));
    }

    @Test
    public void testSize() {
        Board B = new MutableBoard(5);
        assertEquals("bad length", 5, B.size());
        ConstantBoard C = new ConstantBoard(B);
        assertEquals("bad length", 5, C.size());
        Board D = new MutableBoard(C);
        assertEquals("bad length", 5, C.size());
    }

    @Test
    public void testSet() {
        Board B = new MutableBoard(5);
        B.set(2, 2, 1, RED);
        assertEquals("wrong number of spots", 1, B.get(2, 2).getSpots());
        assertEquals("wrong color", RED, B.get(2, 2).getSide());
        assertEquals("wrong count", 1, B.numOfSide(RED));
        assertEquals("wrong count", 0, B.numOfSide(BLUE));
        assertEquals("wrong count", 24, B.numOfSide(WHITE));
    }

    @Test
    public void testMove() {
        Board B = new MutableBoard(6);
        checkBoard("#0", B);
        B.addSpot(RED, 1, 1);
        checkBoard("#1", B, 1, 1, 2, RED);
        B.addSpot(BLUE, 2, 1);
        checkBoard("#2", B, 1, 1, 2, RED, 2, 1, 2, BLUE);
        B.addSpot(RED, 1, 1);
        checkBoard("#3", B, 1, 1, 1, RED, 2, 1, 3, RED, 1, 2, 2, RED);
        B.undo();
        checkBoard("#2U", B, 1, 1, 2, RED, 2, 1, 2, BLUE);
        B.undo();
        checkBoard("#1U", B, 1, 1, 2, RED);
        B.undo();
        checkBoard("#0U", B);
    }

    /** Checks that B conforms to the description given by CONTENTS.
     *  CONTENTS should be a sequence of groups of 4 items:
     *  r, c, n, s, where r and c are row and column number of a square of B,
     *  n is the number of spots that are supposed to be there and s is the
     *  color (RED or BLUE) of the square.  All squares not listed must
     *  be WHITE with one spot.  Raises an exception signaling a unit-test
     *  failure if B does not conform. */
    private void checkBoard(String msg, Board B, Object... contents) {
        for (int k = 0; k < contents.length; k += 4) {
            String M = String.format("%s at %d %d", msg, contents[k],
                                     contents[k + 1]);
            assertEquals(M, (int) contents[k + 2],
                         B.get((int) contents[k],
                               (int) contents[k + 1]).getSpots());
            assertEquals(M, contents[k + 3],
                         B.get((int) contents[k],
                               (int) contents[k + 1]).getSide());
        }
        int c;
        c = 0;
        for (int i = B.size() * B.size() - 1; i >= 0; i -= 1) {
            assertTrue("bad white square #" + i,
                       (B.get(i).getSide() != WHITE)
                       || (B.get(i).getSpots() == 1));
            if (B.get(i).getSide() != WHITE) {
                c += 1;
            }
        }
        assertEquals("extra squares filled", contents.length / 4, c);
    }
}
