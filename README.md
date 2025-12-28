# Ex4
<img width="540" height="500" alt="Gemini_Generated_Image_99qeoo99qeoo99qe" src="https://github.com/user-attachments/assets/d55253e1-fe42-4579-aca4-b19cb49f0ff7" />

# 📊 Ex4: Advanced Spreadsheet Engine

> **A robust, object-oriented spreadsheet application featuring formula parsing, dependency resolution, cycle detection, and advanced logical functions.**

## 📖 Overview

This project is an advanced implementation of a Spreadsheet application (Ex4), extending the capabilities of a basic table engine. It is designed to handle complex mathematical formulas, resolve cell dependencies dynamically, and support advanced spreadsheet features such as **Ranges**, **Statistical Functions** (`SUM`, `AVG`, `MIN`, `MAX`), and **Conditional Logic** (`IF`).

The core of the application is a custom recursive descent parser that evaluates formulas while respecting mathematical order of operations and cell references.

---

## 🚀 Key Features

* **Cell Management:** Supports text, numbers, and formulas.
* **Formula Parsing:** Evaluates mathematical expressions (e.g., `=A1+5*(B2-3)`).
* **Dependency Resolution:** Automatically calculates the depth of dependency for each cell to determine the calculation order.
* **Cycle Detection:** Identifies circular references (e.g., A1 depends on B1, B1 depends on A1) and flags them as errors.
* **2D Ranges:** Support for cell ranges (e.g., `A1:C5`) used in aggregation functions.
* **Statistical Functions:** Implementation of `SUM`, `AVG`, `MIN`, and `MAX` over ranges.
* **Conditional Logic:** Full support for `=IF(condition, true_val, false_val)` statements with comparison operators.
* **Persistence:** Save and Load functionality (I/O) to CSV format.

---

## 🏗 Design & Architecture

The solution is built upon the `Sheet` interface, implemented primarily by the `Ex2Sheet` class.

### 1. `Ex2Sheet` Class

This is the main engine of the spreadsheet. It manages a 2D array of `Cell` objects and a 2D array of computed `Double` values.

* **Storage:** Uses `Cell[][] table` for raw data and `Double[][] data` for cached calculated values.
* **Evaluation Engine:** The `eval()` method orchestrates the calculation process. It first calculates the dependency depth (`depth()`) to ensure cells are computed in the correct topological order.

### 2. `computeFormP` (The Recursive Parser)

The heart of the logic resides in the private method `computeFormP(String form)`. This method employs a recursive approach to evaluate expressions:

* **Base Cases:** Checks if the form is a number or a reference to another cell.
* **Functions & IFs:** Detects keywords (`SUM`, `IF`) and delegates to specific handlers.
* **Arithmetic:** Uses `findLastOp` to identify the operator with the lowest precedence (last to be calculated), splits the expression, and recursively solves the left and right sides.

### 3. Error Handling

The system uses a strict error propagation model:

* `ERR_FORM`: Malformed formulas (e.g., missing parentheses).
* `ERR_CYCLE`: Circular dependencies detected by the `depth()` algorithm.
* `ERR_FUNC`: Invalid function usage.
* `ERR_IF`: Malformed IF statements.

---

## 🧠 Implementation Details & Algorithms

### ⚖️ Handling the `IF` Function

The `IF` function logic handles conditions of the format: `Formula1 operator Formula2`.

* **Parsing Logic:** The parser identifies the `IF` keyword and extracts the content within parentheses.
* **Assumptions & Limitations:** The implementation assumes a strict comma-separated structure: `condition, true_expression, false_expression`.
* The code uses `split(",")` to separate arguments. **Crucial Assumption:** This assumes that the nested expressions do not contain top-level commas that are not protected by internal logic (though the basic split might be sensitive to this). If the structure is invalid (e.g., missing an argument), the system returns `null`, which converts to `ERR_IF_FORMAT`.
* The system evaluates the *condition* first. It only recursively computes the "True" or "False" branch based on the result, optimizing performance.



### 📊 Range & Statistical Functions

* **Range Parsing:** The `getRange` method parses strings like `A1:B3` into two `Index2D` objects representing the start and end of the rectangle.
* **Computation:** Functions like `SUM` or `AVG` iterate over every cell in the defined `Range2D`. They retrieve the calculated value of each cell using `eval(x, y)`. If any cell in the range is invalid or non-numeric, the entire function propagates an error or returns null.

### 🔄 Cycle Detection (`depth`)

Before evaluation, the `depth()` method runs an iterative algorithm:

1. Initialize all depths to -1 (undefined).
2. Iteratively attempt to compute depth based on dependencies.
3. If a cell depends only on numbers/text, depth is 0.
4. If a cell depends on neighbors, its depth is `max(neighbors) + 1`.
5. Cells remaining at -1 after convergence are declared as **Cycles** (`ERR_CYCLE`).

---

## 🧪 Testing

The project includes a comprehensive test suite (JUnit 5) (`Ex2SheetTest`) ensuring high reliability and robustness. The tests cover:

1. **Boundaries & Infrastructure:**
* Verifies grid initialization (default 9x17).
* Tests `isIn` and `get` methods for out-of-bound access prevention.


2. **Advanced Arithmetic:**
* Tests order of operations (e.g., `1+2*3`).
* Validates parenthesis handling including nested and redundant braces `(((x)))`.
* Checks precision with floating-point calculations.


3. **Logical & Functional Tests:**
* **IF Logic:** Validates comparison operators (`<`, `>`, `==`, `!=`, etc.) and nesting.
* **Functions:** Tests `SUM`, `AVG`, `MIN`, `MAX` on 1D and 2D ranges, including reverse ranges (e.g., `A2:A0`).
* **Scientific Notation:** Specific tests for handling large numbers (e.g., `1.0E47`) and infinity overflow.


4. **Robustness & Error Handling:**
* **"The Masquerading Text":** Tests inputs that look like formulas but aren't (e.g., `1.2.3`, `hello@world`).
* **Cycle Detection:** Verifies that self-references and deep circular chains result in `ERR_CYCLE`.
* **Input Sanitization:** Tests formulas with erratic spacing (`= 1 + 2`).


5. **Persistence:**
* Verifies that the state of the spreadsheet can be saved to a CSV file and loaded back accurately.



---


### 📝 Notes on Assumptions

* **String Formatting:** The engine automatically removes spaces and converts formulas to uppercase for consistency.
  
---

**Author:** Mevaser Tziyon Platzkty
**Date:** 2025
