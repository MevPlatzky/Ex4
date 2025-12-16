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

        assertFalse(sheet.isIn(-1, 0));
        assertFalse(sheet.isIn(0, -1));
        assertFalse(sheet.isIn(27, 0));
        assertFalse(sheet.isIn(0, 101));
    }
    @Test
    void testCellAccessAndCoordinates() {
        sheet.set(0, 0, "TopLeft");
        sheet.set(Ex2Utils.WIDTH-1, Ex2Utils.HEIGHT-1, "BottomRight");

        // 1. בדיקת get לפי אינדקסים
        assertNotNull(sheet.get(0, 0));
        assertEquals("TopLeft", sheet.get(0, 0).getData());

        // 2. בדיקת get לפי מחרוזת (רגישות לאותיות)
        assertNotNull(sheet.get("A0")); // רגיל
        assertNotNull(sheet.get("a0")); // אות קטנה
        assertEquals("TopLeft", sheet.get("a0").getData());

        // 3. מקרים לא חוקיים של מחרוזות
        assertNull(sheet.get("XX99")); // אותיות כפולות (בהנחה ולא נתמך)
        assertNull(sheet.get("A-1"));  // מינוס
        assertNull(sheet.get(""));     // ריק

        // 4. בדיקת isIn
        assertTrue(sheet.isIn(0, 0));
        assertFalse(sheet.isIn(-1, 0));
        assertFalse(sheet.isIn(0, -1));
        assertFalse(sheet.isIn(26, 0)); // בדיוק בחוץ
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

    // ADVANCED ARITHMETICS:
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
        // הערה: בגלל ייצוג Double במחשב, זה יכול לצאת 0.09999999...
        // נבדוק שזה קרוב מספיק או נשתמש ב-Parsing הקיים
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
        //With  s p a c e s  test
        sheet.set(3, 0, "= 1 + 2 ");
        assertEquals("3.0", sheet.value(3, 0));
        sheet.set(3, 1, "= ( 1 + 2 ) * 2");
        assertEquals("6.0", sheet.value(3, 1));
    }

    @Test
    void testMathEdgeCases() {
        // חלוקה באפס
        sheet.set(0, 0, "=1/0");
        assertEquals("Infinity", sheet.value(0, 0));

        sheet.set(0, 1, "=-1/0");
        assertEquals("-Infinity", sheet.value(0, 1));

        // חישובים מורכבים יותר
        sheet.set(1, 0, "=(404+404)/8"); // 101
        assertEquals("101.0", sheet.value(1, 0));
    }

    @Test
    void testDeepDependencyChain() {
        int depth = 10;
        sheet.set(0, 0, "2"); // A0
        for (int i = 1; i < depth; i++) {
            String prevCell = "A" + (i - 1);
            sheet.set(0, i, "=" + prevCell + "+2");
        }

        // בדיקה: התא העשירי (A9) אמור להיות: 2 + (9 * 2) = 20
        assertEquals("20.0", sheet.value(0, depth - 1));

        //  משנים את ההתחלה (A0)
        sheet.set(0, 0, "1");
        // עכשיו: 1 + 18 = 19
        assertEquals("19.0", sheet.value(0, depth - 1));
    }

    @Test
    void testCrossReference() {
        // X תלוי ב-Y, Y תלוי ב-Z
        sheet.set(0, 0, "=10");    // A0

        // תיקון: שימוש בגבולות האמיתיים של הלוח במקום Z99
        int lastCol = Ex2Utils.WIDTH - 1;   // בדרך כלל 8 (I)
        int lastRow = Ex2Utils.HEIGHT - 1;  // בדרך כלל 16

        // נציב בתא האחרון בלוח (למשל I16)
        sheet.set(lastCol, lastRow, "=A0*2");

        // נציב בתא באמצע (למשל E5) הפניה לתא האחרון
        // בונים את השם של התא האחרון דינמית (למשל "I16")
        String lastCellName = Ex2Utils.ABC[lastCol] + lastRow;
        sheet.set(5, 5, "=" + lastCellName + "+5");

        // בדיקה: 20 + 5 = 25
        assertEquals("25.0", sheet.value(5, 5));
    }


    @Test
    void testFunctions() {
        // נתונים
        sheet.set(0, 0, "5");
        sheet.set(0, 1, "15");
        sheet.set(0, 2, "25");

        // טווח מלא
        sheet.set(1, 0, "=SUM(A0:A2)");
        assertEquals("45.0", sheet.value(1, 0));

        // טווח של תא בודד (חשוב!)
        sheet.set(1, 1, "=MIN(A0:A0)");
        assertEquals("5.0", sheet.value(1, 1));

        // טווח הפוך (A2:A0) - אם מימשת את ה-Min/Max ב-Range2D
        sheet.set(1, 2, "=MAX(A2:A0)");
        assertEquals("25.0", sheet.value(1, 2));
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
        // A0 = SUM(A0:A1) -> מעגל!
        sheet.set(0, 0, "=SUM(A0:A1)");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0));
    }

    @Test
    void testInfinityOverflow() {
        // Test huge numbers
        sheet.set(0, 0, String.valueOf(Double.MAX_VALUE));
        sheet.set(0, 1, String.valueOf(Double.MAX_VALUE));

        // זה אמור לצאת Infinity
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
        sheet.set(0, 0, "=IF(1 > -5, 3, 0)");
        assertEquals("3.0", sheet.value(0, 0));
        sheet.set(0, 0, "=IF(-15 <= -4, 100, 0)");
        assertEquals("100.0", sheet.value(0, 0));
        // WITH MATH EXPRESSIONS
        sheet.set(0, 1, "=IF(1+1==2, 5*2, 0)");
        assertEquals("10.0", sheet.value(0, 1));
        //WITH CELL REFERENCES
        sheet.set(1, 0, "100");
        sheet.set(1, 1, "=IF(A0==100, B0, 0)"); //A0 is 100 from last test...
        assertEquals("100.0", sheet.value(1, 1));
        //WITH CELL REFERENCES & MATH EXPRESSIONS
        sheet.set(2, 0, "100");
        sheet.set(2, 1, "=IF(A0==(2*B0-100), B0-7, 0)"); //A0 is 100 from last test...
        assertEquals("93.0", sheet.value(2, 1));

    }

    @Test
    void testErrorPropagation() {
        // שגיאה מתפשטת: A0 שגוי -> B0 תלוי בו -> גם B0 שגוי
        sheet.set(0, 0, "=1/0"); // Infinity (או ERR תלוי בלוגיקה)
        sheet.set(1, 0, "=A0+5");
        assertEquals("Infinity", sheet.value(1, 0)); // Infinity + 5 = Infinity

        sheet.set(0, 1, "=ERR"); // שגיאת פורמט
        sheet.set(1, 1, "=A1+5");
        // אם A1 הוא ERR_FORM, אז החישוב של B1 אמור להיכשל
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(1, 1));
    }

    @Test
    void testInvalidReferences() {
        // הפנייה לטווח לא חוקי
        sheet.set(0, 0, "=SUM(A0:Z9999)"); // חורג מהגבולות
        // בהתאם לתיקון שעשינו, זה צריך להיות ERR_FORM או ERR_FUNC
        String val = sheet.value(0, 0);
        assertNotEquals("0.0", val);
        assertTrue(val.startsWith("ERR"));
    }

    // SAVE & LOAD:
    @Test
    void testPersistence() throws IOException {
        String filename = "robust_test.csv";

        //fill with some stuff
        sheet.set(0, 0, "123");
        sheet.set(0, 1, "=A0*2");
        sheet.set(0, 2, "Some Text");
        sheet.set(5, 5, "=MIN(A0:A1)");

        sheet.save(filename);

        //create a new sheet and load the saved one
        Ex2Sheet s2 = new Ex2Sheet(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
        s2.load(filename);

        assertEquals("123.0", s2.value(0, 0));
        assertEquals("246.0", s2.value(0, 1));
        assertEquals("Some Text", s2.value(0, 2));
        assertEquals("123.0", s2.value(5, 5)); // Min(123, 246) = 123
    }
}