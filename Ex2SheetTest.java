package assignments.ex2.ex2_sol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive Test Class for Ex2Sheet.
 * Based on extensive coverage requirements including:
 * - Advanced Arithmetic & Precedence
 * - Deep Dependency Chains
 * - Complex Function usage (Ranges, Errors, Edge cases)
 * - Condition Logic (IF)
 * - Serialization (Save/Load)
 * - Error Handling & Stability
 */
class Ex2SheetTest {
    private Ex2Sheet sheet;

    @BeforeEach
    void setUp() {
        sheet = new Ex2Sheet(26, 100);
    }

    @Test
    void testConstructors() {
        // NegativeArraySizeException
        assertThrows(Throwable.class, () -> new Ex2Sheet(-2, 3));
        assertThrows(Throwable.class, () -> new Ex2Sheet(7, -1));
        assertThrows(Throwable.class, () -> new Ex2Sheet(-7, -1));
        // Default sheet
        Ex2Sheet defaultSheet = new Ex2Sheet();
        assertEquals(Ex2Utils.WIDTH, defaultSheet.width());
        assertEquals(Ex2Utils.HEIGHT, defaultSheet.height());
    }

    // BASIC CELL MANIPULATION & BOUNDARIES
    @Test
    void testBoundariesAndGet() {
        // (Ex2Utils.WIDTH/HEIGHT)
        assertNotNull(sheet.get(0, 0));
        assertNotNull(sheet.get(Ex2Utils.WIDTH - 1, Ex2Utils.HEIGHT - 1));

        assertNull(sheet.get(-1, 0));
        assertNull(sheet.get(0, -1));
        assertNull(sheet.get(30, 0));
        assertNull(sheet.get(0, 300));

        assertTrue(sheet.isIn(0, 0));
        assertTrue(sheet.isIn(Ex2Utils.WIDTH - 1, Ex2Utils.HEIGHT - 1));

        assertFalse(sheet.isIn(-1, 0));
        assertFalse(sheet.isIn(0, -1));
        assertFalse(sheet.isIn(27, 0));
        assertFalse(sheet.isIn(0, 101));
    }
    @Test
    void testGets() {
        sheet.set(0, 0, "TopLeft");
        sheet.set(Ex2Utils.WIDTH-1, Ex2Utils.HEIGHT-1, "BottomRight");
        // Get with index
        assertNotNull(sheet.get(0, 0));
        assertEquals("TopLeft", sheet.get(0, 0).getData());
        // Get with Strong
        assertNotNull(sheet.get("A0")); // Uppercase
        assertNotNull(sheet.get("a0")); // Lowercase
        assertEquals("TopLeft", sheet.get("a0").getData());
        // Illegal Strings inputs
        assertNull(sheet.get("XX99"));
        assertNull(sheet.get("A-1"));
        assertNull(sheet.get(""));
        assertNull(sheet.get(null));
    }

    @Test
    void testSetAndClear() {
        sheet.set(0, 0, "123"); //Set something
        assertEquals("123.0", sheet.value(0, 0));
        sheet.set(0, 0, ""); //Clean a cell
        assertEquals("", sheet.value(0, 0));
        sheet.set(1, 1, "Text");
        sheet.set(1, 1, "NewText"); //Override
        assertEquals("NewText", sheet.value(1, 1));
    }

    // ADVANCED ARITHMETICS for simple formulas:
    @Test
    void testComplexArithmetic() {
        // Braces & Nested braces
        sheet.set(0, 0, "=(1)");
        assertEquals("1.0", sheet.value(0, 0));
        sheet.set(0, 0, "=(((1+2)*3)+6)"); // ((3)*3)+6 = 15
        assertEquals("15.0", sheet.value(0, 0));
        sheet.set(0, 1, "=(1+2)*((3))-1"); // 3 * 3 - 1 = 8
        assertEquals("8.0", sheet.value(0, 1));
        sheet.set(0, 1, "=(((1+2)))");
        assertEquals("3.0", sheet.value(0, 1));
        // Precision test
        sheet.set(1, 0, "=1.2-1.15");
        double val = Double.parseDouble(sheet.value(1, 0));
        assertEquals(0.05, val, 0.000001);
        // Long expression test
        sheet.set(2, 0, "=11+10-3*2+1"); // 21 - 6 + 1 = 16
        assertEquals("16.0", sheet.value(2, 0));
        sheet.set(2, 1, "=5/2"); // 2.5
        assertEquals("2.5", sheet.value(2, 1));
        // Starts with minus
        sheet.set(2, 0, "=-(1+4)");
        assertEquals("-5.0", sheet.value(2, 0));
        sheet.set(2, 1, "=-1+5");
        assertEquals("4.0", sheet.value(2, 1));
        sheet.set(2, 0, "=-2*4");
        assertEquals("-8.0", sheet.value(2, 0));
        //With  s p a c e s  test
        sheet.set(3, 0, "= 1 + 2 ");
        assertEquals("3.0", sheet.value(3, 0));
        sheet.set(3, 1, "=     ( 1 + 2 ) *   2");
        assertEquals("6.0", sheet.value(3, 1));
        // Zero division
        sheet.set(0, 0, "=1/0");
        assertEquals("Infinity", sheet.value(0, 0));
        sheet.set(0, 1, "=-1/0");
        assertEquals("-Infinity", sheet.value(0, 1));
        // More complex division
        sheet.set(1, 0, "=(404+404)/8"); // 101
        assertEquals("101.0", sheet.value(1, 0));
    }

    @Test
    void testErrorSpread() {
        // The Error spreads to the dependant cells
        sheet.set(0, 0, "=1/0"); // Infinity
        sheet.set(1, 0, "=A0+5");
        assertEquals("Infinity", sheet.value(1, 0)); // Infinity + 5 = Infinity

        sheet.set(0, 1, "=ERR"); // Form Err
        sheet.set(1, 1, "=A1+5");
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(1, 1));
    }

    @Test
    void testErrForms() {  //a, AB, @2, 2+), (3+1*2)-, =(), =5**,
        // Missing parenthesis etc
        sheet.set(0, 0, "=3+");
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
        sheet.set(0, 0, "=(7)+");
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
        sheet.set(0, 0, "=a");
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
        sheet.set(0, 0, "=AB");
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
        sheet.set(0, 0, "=@2");
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
        sheet.set(0, 0, "=(3+1*2)-");
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
        sheet.set(0, 0, "=()");
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
        sheet.set(0, 0, "=5*+");
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
        // Missing cell
        sheet.set(0, 1, "=Z99");
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, sheet.get(0, 1).getType());
        // Func does not exists
        sheet.set(0, 2, "=HELLO(1,1)");
        assertTrue(sheet.value(0, 2).startsWith("ERR"));
        //
    }

    @Test
    void testDeepDependencyChain() {
        int depth = 10;
        sheet.set(0, 0, "2"); // A0
        for (int i = 1; i < depth; i++) {
            String prevCell = "A" + (i - 1);
            sheet.set(0, i, "=" + prevCell + "+2");
        }
        // Check the last cell
        assertEquals("20.0", sheet.value(0, depth - 1));
        // Change the first cell
        sheet.set(0, 0, "1");
        // now 1+2*9 = 19
        assertEquals("19.0", sheet.value(0, depth - 1));
    }

    @Test
    void testDepthAndCycles() {
        // A self cycle:
        sheet.set(2, 0, "=C0");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(2, 0));
        // A two cell cycleL
        sheet.set(0, 0, "=B0");
        sheet.set(1, 0, "=A0");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0));
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(1, 0));
        // Check directly the depth:
        int[][] d = sheet.depth();
        assertEquals(-1, d[0][0]);
        assertEquals(-1, d[1][0]);
        // A bigger cycle:
        sheet.set(0, 0, "=A4");
        sheet.set(0, 1, "=A0");
        sheet.set(0, 2, "=A1");
        sheet.set(0, 3, "=A2");
        sheet.set(0, 4, "=A3");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0));
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 1));
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 2));
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 3));
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 4));
    }

    @Test
    void testCrossReference() {
        sheet.set(0, 0, "=10"); //A0
        int lastCol = Ex2Utils.WIDTH - 1;
        int lastRow = Ex2Utils.HEIGHT - 1;
        // set the last cell
        sheet.set(lastCol, lastRow, "=A0*2");
        //Dynamically build the last cell
        String lastCellName = Ex2Utils.ABC[lastCol] + lastRow;
        sheet.set(5, 5, "=" + lastCellName + "+5");
        assertEquals("25.0", sheet.value(5, 5));
    }

    @Test
    void testFunctions() {
        sheet.set(0, 0, "5");
        sheet.set(1, 0, "10");
        sheet.set(0, 1, "10");
        sheet.set(1, 1, "25");
        sheet.set(0, 2, "15");

        // A one dimensional range
        sheet.set(0, 3, "=SUM(A0:A2)");
        assertEquals("30.0", sheet.value(0, 3));
        sheet.set(0,3,"=AVG(A0:A2)");
        assertEquals("10.0", sheet.value(0,3));
        sheet.set(0,3,"=MIN(A0:A2)");
        assertEquals("5.0",sheet.value(0,3));
        sheet.set(0,3,"=MAX(A0:A2)");
        assertEquals("15.0",sheet.value(0,3));

        // A two-dimensional range
        sheet.set(0, 3, "=SUM(A0:B1)");
        assertEquals("50.0", sheet.value(0, 3));
        sheet.set(0,3,"=AVG(A0:B1)");
        assertEquals("12.5", sheet.value(0,3));
        sheet.set(0,3,"=MIN(A0:B1)");
        assertEquals("5.0",sheet.value(0,3));
        sheet.set(0,3,"=MAX(A0:B1)");
        assertEquals("25.0",sheet.value(0,3));

        // A single cell range
        sheet.set(0, 3, "=MIN(A0:A0)");
        assertEquals("5.0", sheet.value(0, 3));
        sheet.set(0,3,"=AVG(A2:A2)");
        assertEquals("15.0", sheet.value(0,3));

        // A reverse range order
        sheet.set(0,3,"=AVG(A1:B0)");
        assertEquals("12.5", sheet.value(0,3));
        sheet.set(0,3,"=MAX(A1:B0)");
        assertEquals("25.0",sheet.value(0,3));
    }

    @Test
    void testHugeNumbersSum() {
        // This test is problematic with the GUI but logics works well.
        // Huge numbers in scientific form
        sheet.set(0, 0, "1.0E47"); // A0
        sheet.set(1, 0, "1.0E19"); // B0
        sheet.set(2, 0, "1.0E34"); // C0
        sheet.set(3, 0, "1.0E70"); // D0
        sheet.set(4, 0, "1.0E85"); // E0

        assertEquals(1.0E47, Double.parseDouble(sheet.value(0, 0)), 0.001);
        sheet.set(0, 1, "=SUM(A0:E0)"); // A1
        double result = Double.parseDouble(sheet.value(0, 1));
        assertTrue(result > 1.0E80, "The sum should be huge!");
        assertEquals(1.0E85, result, 1.0E80); // בדיקה גסה
    }

    @Test
    void testFunctionsWithErrors() {
        // Cell with text
        sheet.set(0, 0, "5");
        sheet.set(0, 1, "Text");
        sheet.set(1, 0, "=SUM(A0:A1)");
        assertEquals(Ex2Utils.ERR_FUNC, sheet.value(1, 0));
        // Empty cell
        sheet.set(3, 0, "5");
        sheet.set(3, 1, ""); // ריק
        sheet.set(3, 2, "=SUM(A3:B3)");
        assertEquals(Ex2Utils.ERR_FUNC, sheet.value(3, 2));
        // Wrong func name
        sheet.set(4, 0, "=SUUMM(A0:A1)");
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(4, 0));
        sheet.set(4, 1, "=SUMmM(A0:A1)");
        assertEquals(Ex2Utils.ERR_FUNC, sheet.value(4, 1));


    }

    @Test
    void testFunctionCircular() {
        // Function contains itself in range:
        sheet.set(0, 0, "=AVG(A0:A1)");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0));
    }

    @Test
    void testInfinityOverflow() {
        // Test huge numbers
        sheet.set(0, 0, String.valueOf(Double.MAX_VALUE));
        sheet.set(0, 1, String.valueOf(Double.MAX_VALUE));
        // Should be infinity
        sheet.set(1, 0, "=SUM(A0:A1)");
        assertEquals("Infinity", sheet.value(1, 0));
    }

    @Test
    void testIF() {
        // BASIC
        sheet.set(0, 0, "=IF(10>5, 1, 0)"); // > true
        assertEquals("1.0", sheet.value(0, 0));
        sheet.set(0, 0, "=IF(1>=3, 1, 0)"); // >= false
        assertEquals("0.0", sheet.value(0, 0));
        sheet.set(0, 0, "=IF(900<901, 1, 0)"); // < true
        assertEquals("1.0", sheet.value(0, 0));
        sheet.set(0, 0, "=IF(10<=5, 1, 0)"); // <= false
        assertEquals("0.0", sheet.value(0, 0));
        sheet.set(0, 0, "=IF(7==77, 1, 0)"); // == false
        assertEquals("0.0", sheet.value(0, 0));
        sheet.set(0, 0, "=IF(10!=5, 1, 0)"); // != true
        assertEquals("1.0", sheet.value(0, 0));
        // WITH NEGATIVE VALUES
        sheet.set(0, 0, "=IF(-10 > 5, 1, 0)");
        assertEquals("0.0", sheet.value(0, 0));
        sheet.set(0, 1, "=IF(1 > -5, 3, 0)");
        assertEquals("3.0", sheet.value(0, 1));
        sheet.set(0, 0, "=IF(-15 <= -4, 100, 0)");
        assertEquals("100.0", sheet.value(0, 0));
        // WITH MATH EXPRESSIONS
        //
        sheet.set(0, 3, "=IF((1+1*9-8)==2, 5*2, 0)");
        assertEquals("10.0", sheet.value(0, 3));
        //WITH CELL REFERENCES
        sheet.set(1, 0, "100");
        sheet.set(1, 1, "=IF(A0==100, B0, 0)"); //A0 is 100 from last test...
        assertEquals("100.0", sheet.value(1, 1));
        //WITH CELL REFERENCES & MATH EXPRESSIONS
        sheet.set(2, 0, "100");
        sheet.set(0, 2, "100");
        sheet.set(2, 1, "=IF(A0==(2*B0-100), B0-7, 0)"); //A0 is 100 from last test...
        assertEquals("93.0", sheet.value(2, 1));
        sheet.set(4, 1, "=if(b0*b1 != a3/(2-a1), a2+2, a1+1)");
        assertEquals("102.0", sheet.value(4, 1));
    }

    @Test
    void testErrIf() {
        sheet.set(0,0,"1");
        sheet.set(0,1,"2");
        sheet.set(0,2,"3");
        // Wrong format IF
        sheet.set(3, 3, "=if(2 < 1 , 1, 0 ");   // missing braces
        assertEquals(sheet.value(3, 3), Ex2Utils.ERR_IF);
        sheet.set(3, 3, "=if(2 == 1 , 99)");    // missing second option
        assertEquals(sheet.value(3, 3), Ex2Utils.ERR_IF);
        sheet.set(3, 3, "=if(2 && 1 , 1, 3)");   // invalid op
        assertEquals(sheet.value(3, 3), Ex2Utils.ERR_IF);
        sheet.set(3, 3, "=if(2 < 1 , =1, =0)"); // shouldn't have a "=" at the results
        assertEquals(sheet.value(3, 3), Ex2Utils.ERR_IF);
        sheet.set(3, 3, "=if(2 < 1 , 1, G1");   // missing content cell
        assertEquals(sheet.value(3, 3), Ex2Utils.ERR_IF);
        sheet.set(3, 3, "=if(A1,2,3)");          // not an op
        assertEquals(sheet.value(3, 3), Ex2Utils.ERR_IF);
        sheet.set(3, 3, "=if(11,2,3)");          // also not an op
        assertEquals(sheet.value(3, 3), Ex2Utils.ERR_IF);
        sheet.set(3, 3, "=if(a1<a2,=(A1, 12)"); // invalid first formula
        assertEquals(sheet.value(3, 3), Ex2Utils.ERR_IF);
        sheet.set(3, 3, "=if(a1<a2,(A1, 12)"); // invalid first formula
        assertEquals(sheet.value(3, 3), Ex2Utils.ERR_IF);
        sheet.set(3, 3, "=if(d3<1 , 1, 0 )");   // A self referring if
        assertEquals(sheet.value(3, 3), Ex2Utils.ERR_CYCLE);
    }

    @Test
    void testInvalidReferences() {
        // An illegal reference cell
        sheet.set(1, 0, "=SUM(B1:Z9999)"); // out of boundaries
        sheet.set(0, 1, "=SUM(A1:Z9999)"); // cycle reference
        sheet.set(0, 0, "=SUM(A:11)"); // illegal reference
        String val = sheet.value(0, 0);
        assertNotEquals("0.0", val);
        assertEquals(Ex2Utils.ERR_FUNC,sheet.value(1,0));
        assertEquals(Ex2Utils.ERR_CYCLE,sheet.value(0,1));
        assertEquals(Ex2Utils.ERR_FUNC,sheet.value(0,0));
    }

    // SAVE & LOAD:
    @Test
    void testSetAndLoad() throws IOException {
        String filename = "robust_test.csv";

        //fill with some stuff and save
        sheet.set(0, 0, "123");
        sheet.set(0, 1, "=A0*2");
        sheet.set(0, 2, "Some Text");
        sheet.set(5, 5, "=MIN(A0:A1)");
        sheet.save(filename);
        //create a new sheet and load the saved one
        Ex2Sheet s2 = new Ex2Sheet(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
        s2.load(filename);
        //Check the values
        assertEquals("123.0", s2.value(0, 0));
        assertEquals("246.0", s2.value(0, 1));
        assertEquals("Some Text", s2.value(0, 2));
        assertEquals("123.0", s2.value(5, 5)); // Min(123, 246) = 123

        new java.io.File(filename).delete(); //delete the temporary file
    }

    @Test
    void testFindLastOp() {
        assertEquals(1, Ex2Sheet.findLastOp("1+2"));    // +
        assertEquals(1, Ex2Sheet.findLastOp("1-2"));    // -
        assertEquals(1, Ex2Sheet.findLastOp("1*2"));    // *
        assertEquals(1, Ex2Sheet.findLastOp("1/2"));    // /
        assertEquals(1, Ex2Sheet.findLastOp("1+2*3"));  // +
        assertEquals(3, Ex2Sheet.findLastOp("1*2+3"));  // +
        assertEquals(5, Ex2Sheet.findLastOp("(1+2)*3"));    // *
    }
}