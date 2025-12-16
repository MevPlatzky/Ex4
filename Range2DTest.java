package assignments.ex2.ex2_sol;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class Range2DTest {

    /**
     * Test 1: Standard Order (Top-Left to Bottom-Right).
     * Tests the core nested loops in getCells().
     */
    @Test
    void testStandardRange() {
        // Range: (0,0) to (1,1) -> Should include (0,0), (0,1), (1,0), (1,1)
        CellEntry p1 = new CellEntry(0, 0);
        CellEntry p2 = new CellEntry(1, 1);
        Range2D range = new Range2D(p1, p2);

        ArrayList<Index2D> cells = range.getCells();
        System.out.println(range);
        assertEquals(4, cells.size(), "2x2 range should contain 4 cells");

        // Check if specific cells exist
        boolean has00 = false, has11 = false;
        for(Index2D c : cells) {
            if(c.getX() == 0 && c.getY() == 0) has00 = true;
            if(c.getX() == 0 && c.getY() == 1) has00 = true;
            if(c.getX() == 1 && c.getY() == 0) has00 = true;
            if(c.getX() == 1 && c.getY() == 1) has11 = true;
        }
        assertTrue(has00 && has11, "Range MUST contain the boundary cells");
    }

    /**
     * Test 2: Reverse Order (Bottom-Right to Top-Left).
     * CRITICAL: This tests the 'Math.min' and 'Math.max' lines in your code.
     */
    @Test
    void testReverseRange() {
        // Range: (1,1) to (0,0). User selected backwards.
        CellEntry p1 = new CellEntry(1, 1);
        CellEntry p2 = new CellEntry(0, 0);
        Range2D range = new Range2D(p1, p2);

        ArrayList<Index2D> cells = range.getCells();

        // Logic check: If Math.min/max were missing, this would be size 0.
        assertEquals(4, cells.size(), "Reverse selection should still yield 4 cells");
    }

    /**
     * Test 3: Single Cell Range.
     * Tests loop conditions (i <= maxX) and toString substring safety.
     */
    @Test
    void testSingleCell() {
        CellEntry p1 = new CellEntry(2, 2);
        Range2D range = new Range2D(p1, p1);

        ArrayList<Index2D> cells = range.getCells();

        // 1. Check Logic
        assertEquals(1, cells.size(), "Start==End should result in exactly 1 cell");
        assertEquals(2, cells.get(0).getX());

        // 2. Check toString formatting for single item
        // Expected: "(2,2)" (The substring logic removes the " | ")
        String str = range.toString();
        assertEquals("(2,2)", str, "toString failed to trim the separator correctly on single item");
    }

    /**
     * Test 4: 1D Range (Same Row or Same Column).
     * Tests that loops handle equal min/max on one axis.
     */
    @Test
    void testOneDimensionalRange() {
        // Same column: (0,0) to (0,2) -> 3 cells
        CellEntry p1 = new CellEntry(0, 0);
        CellEntry p2 = new CellEntry(0, 2);
        Range2D range = new Range2D(p1, p2);

        assertEquals(3, range.getCells().size(), "Range in same column failed");
    }

    /**
     * Test 5: toString Format Verification.
     * Verifies the exact string structure defined in your code.
     */
    @Test
    void testToStringFormat() {
        CellEntry p1 = new CellEntry(0, 0);
        CellEntry p2 = new CellEntry(0, 1);
        Range2D range = new Range2D(p1, p2); // Should have (0,0) and (0,1)

        String res = range.toString();

        // We expect: "(0,0) | (0,1)"
        // Your code adds " | " after each item and then removes the last 3 chars.

        assertTrue(res.contains("(0,0)"), "Missing first coordinate");
        assertTrue(res.contains("(0,1)"), "Missing second coordinate");
        assertTrue(res.contains("|"), "Missing separator");
        assertFalse(res.endsWith("| "), "Failed to remove trailing separator");
    }
}