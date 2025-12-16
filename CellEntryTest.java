package assignments.ex2.ex2_sol;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellEntryTest {


//    Basic Valid Input:
//    Verifies that a standard cell coordinate (like "A1") is considered valid
//    and is parsed correctly.
    @Test
    void testBasicValidCell() {
        String input = "A1";
        CellEntry c = new CellEntry(input);
        assertTrue(c.isValid(), "A1 should be a valid cell coordinate");
        assertEquals(0, c.getX(), "Column A should be index 0");
        assertEquals(1, c.getY(), "Row 1 should be index 1");
    }

    /**
     * Case Insensitivity:
     * Verifies that the program treats 'a1' and 'A1' exactly the same.
     */
    @Test
    void testCaseInsensitivity() {
        CellEntry lower = new CellEntry("g5");
        CellEntry upper = new CellEntry("G5");

        assertTrue(lower.isValid(), "Lower case 'g5' should be valid");
        assertEquals(lower.getX(), upper.getX(), "X coordinate should match regardless of case");
        assertEquals(lower.getY(), upper.getY(), "Y coordinate should match regardless of case");
    }

    /**
     * Invalid Inputs (Negative Testing):
     * Verifies that the class correctly identifies and rejects invalid formats.
     */
    @Test
    void testInvalidInputs() {
        // null input
        assertFalse(new CellEntry(null).isValid(), "Null input should be invalid");
        // empty string
        assertFalse(new CellEntry("").isValid(), "Empty string should be invalid");
        // wrong format (Number before Letter)
        assertFalse(new CellEntry("8e").isValid(), "Format '8e' is invalid (should be Letter-Number)");
        // missing components
        assertFalse(new CellEntry("D").isValid(), "Missing number should be invalid");
        assertFalse(new CellEntry("55").isValid(), "Missing letter should be invalid");
        assertFalse(new CellEntry("FF").isValid(), "Double letters should be invalid");

        // special characters
        assertFalse(new CellEntry("A 1").isValid(), "Spaces should generally be invalid inside coords");
        assertFalse(new CellEntry("!@#").isValid(), "Special characters should be invalid");
        // separation between x and y
        CellEntry special = new CellEntry("A-5");
        assertFalse(special.isValid(), "An '-' is invalid");
    }

    /**
     * toString() method:
     * Verifies that the object can represent itself back as a String correctly.
     */
    @Test
    void testToString() {
        String cord = "C10";
        CellEntry c = new CellEntry(cord);

        // We expect the toString to return "C10" (or at least contain it)
        assertNotNull(c.toString());
        assertEquals(cord, c.toString(), "toString should return the original coordinate");
    }

    /**
     * Boundaries (Edge Cases):
     * Verifies behavior at the edges of the spreadsheet (depending on Ex2Utils limits).
     */
    @Test
    void testBoundaries() {
        // Assuming the spreadsheet supports up to Z
        CellEntry validEdge = new CellEntry("Z10");
        assertTrue(validEdge.isValid(), "Z10 should be within valid range (if width allows)");
    }
}