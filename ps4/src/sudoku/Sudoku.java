/**
 * Author: dnj, Hank Huang
 * Date: March 7, 2009
 * 6.005 Elements of Software Construction
 * (c) 2007-2009, MIT 6.005 Staff
 */
package sudoku;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import sat.env.Bool;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;

/**
 * Sudoku is an immutable abstract datatype representing instances of Sudoku.
 * Each object is a partially completed Sudoku puzzle.
 */
public class Sudoku {
	// dimension: standard puzzle has dim 3
	private final int dim;
	// number of rows and columns: standard puzzle has size 9
	private final int size;
	// known values: square[i][j] represents the square in the ith row and jth
	// column,
	// contains -1 if the digit is not present, else i>=0 to represent the digit
	// i+1
	// (digits are indexed from 0 and not 1 so that we can take the number k
	// from square[i][j] and
	// use it to index into occupies[i][j][k])
	private final int[][] square;
	// occupies [i,j,k] means that kth symbol occupies entry in row i, column j
	private final Variable[][][] occupies;

	// Rep invariant
	// TODO: write your rep invariant here
	private void checkRep() {
		// TODO: implement this.
		assert dim != 0;
		assert size == dim * dim;
		assert square != null;
		assert occupies != null;
	}

	/**
	 * create an empty Sudoku puzzle of dimension dim.
	 * 
	 * @param dim
	 *            size of one block of the puzzle. For example, new Sudoku(3)
	 *            makes a standard Sudoku puzzle with a 9x9 grid.
	 */
	public Sudoku(int dim) {
		// TODO: implement this.
		this.dim = dim;
		this.size = dim * dim;
		this.square = new int[size][size];
		this.occupies = new Variable[size][size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					Variable v = new Variable("occupies(" + i + "," + j + "," + k + ")");
					occupies[i][j][k] = v;
				}
			}
		}
		checkRep();
	}

	/**
	 * create Sudoku puzzle
	 * 
	 * @param square
	 *            digits or blanks of the Sudoku grid. square[i][j] represents
	 *            the square in the ith row and jth column, contains 0 for a
	 *            blank, else i to represent the digit i. So { { 0, 0, 0, 1 }, {
	 *            2, 3, 0, 4 }, { 0, 0, 0, 3 }, { 4, 1, 0, 2 } } represents the
	 *            dimension-2 Sudoku grid:
	 * 
	 *            ...1 23.4 ...3 41.2
	 * 
	 * @param dim
	 *            dimension of puzzle Requires that dim*dim == square.length ==
	 *            square[i].length for 0<=i<dim.
	 */
	public Sudoku(int dim, int[][] square) {
		// TODO: implement this.
		this.dim = dim;
		this.size = dim * dim;
		this.square = square;
		this.occupies = new Variable[size][size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					Variable v = new Variable("occupies(" + i + "," + j + "," + k + ")");
					occupies[i][j][k] = v;
				}
			}
		}
		checkRep();
	}

	/**
	 * Reads in a file containing a Sudoku puzzle.
	 * 
	 * @param dim
	 *            Dimension of puzzle. Requires: at most dim of 3, because
	 *            otherwise need different file format
	 * @param filename
	 *            of file containing puzzle. The file should contain one line
	 *            per row, with each square in the row represented by a digit,
	 *            if known, and a period otherwise. With dimension dim, the file
	 *            should contain dim*dim rows, and each row should contain
	 *            dim*dim characters.
	 * @return Sudoku object corresponding to file contents
	 * @throws IOException
	 *             if file reading encounters an error
	 * @throws ParseException
	 *             if file has error in its format
	 */
	public static Sudoku fromFile(int dim, String filename) throws IOException, ParseException {
		// TODO: implement this.
		assert dim > 0;
		assert !filename.equals("");

		int[][] result = new int[dim * dim][dim * dim];
		int row = 0;

		try (Scanner scanner = new Scanner(new File(filename))) {
			while (scanner.hasNext()) {
				result = change(result, scanner.nextLine(), row);
				row += 1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new Sudoku(dim, result);
	}

	private static int[][] change(int[][] x, String y, int z) {
		assert x != null;
		assert !y.equals("");

		for (int i = 0; i < x.length; i++) {
			String current = y.substring(i, i + 1);
			if (current.equals(".")) {
				x[z][i] = 0;
			} else {
				x[z][i] = Integer.parseInt(y.substring(i, i + 1));
			}
		}
		return x;
	}

	/**
	 * Exception used for signaling grammatical errors in Sudoku puzzle files
	 */
	@SuppressWarnings("serial")
	public static class ParseException extends Exception {
		public ParseException(String msg) {
			super(msg);
		}
	}

	/**
	 * Produce readable string representation of this Sukoku grid, e.g. for a 4
	 * x 4 sudoku problem: 12.4 3412 2.43 4321
	 * 
	 * @return a string corresponding to this grid
	 */
	public String toString() {
		// TODO: implement this.
		String result = "";
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (square[i][j] == 0) {
					result = result + " " + ".";
				} else {
					result = result + " " + square[i][j];
				}
			}
			result = result + "\n";
		}
		return result;
	}

	/**
	 * @return a SAT problem corresponding to the puzzle, using variables with
	 *         names of the form occupies(i,j,k) to indicate that the kth symbol
	 *         occupies the entry in row i, column j
	 */
	public Formula getProblem() {
		// TODO: implement this.
		Formula formula = new Formula();

		// rows
		for (int k = 0; k < size; k++) {
			for (int i = 0; i < size; i++) {
				Formula atMost = new Formula();
				Formula atLeast = new Formula(new Clause());
				for (int j = 0; j < size; j++) {
					atLeast = atLeast.or(new Formula(occupies[i][j][k]));
					for (int j2 = 0; j2 < size; j2++) {
						if (j != j2) {
							Formula temp = new Formula(occupies[i][j][k]);
							Formula temp2 = new Formula(occupies[i][j2][k]);
							temp = temp.not();
							temp2 = temp2.not();
							atMost = atMost.and(temp.or(temp2));
						}
					}
				}
				formula = formula.and(atMost).and(atLeast);
			}
		}

		// columns
		for (int k = 0; k < size; k++) {
			for (int j = 0; j < size; j++) {
				Formula atMost = new Formula();
				Formula atLeast = new Formula(new Clause());
				for (int i = 0; i < size; i++) {
					atLeast = atLeast.or(new Formula(occupies[i][j][k]));
					for (int i2 = 0; i2 < size; i2++) {
						if (i != i2) {
							Formula temp = new Formula(occupies[i][j][k]);
							Formula temp2 = new Formula(occupies[i2][j][k]);
							temp = temp.not();
							temp2 = temp2.not();
							atMost = atMost.and(temp.or(temp2));
						}
					}
				}
				formula = formula.and(atMost).and(atLeast);
			}
		}

		// blocks
		for (int iBlock = 0; iBlock < dim; iBlock++) {
			for (int jBlock = 0; jBlock < dim; jBlock++) {

				for (int k = 0; k < size; k++) {
					Formula atMost = new Formula();
					Formula atLeast = new Formula(new Clause());
					for (int i = 0; i < dim; i++) {
						for (int j = 0; j < dim; j++) {
							atLeast = atLeast.or(new Formula(occupies[iBlock * dim + i][jBlock * dim + j][k]));
							for (int k2 = 0; k2 < size; k2++) {
								if (k != k2) {
									Formula temp = new Formula(occupies[iBlock * dim + i][jBlock * dim + j][k]);
									Formula temp2 = new Formula(occupies[iBlock * dim + i][jBlock * dim + j][k2]);
									temp = temp.not();
									temp2 = temp2.not();
									atMost = atMost.and(temp.or(temp2));
								}
							}
						}
					}
					formula = formula.and(atMost).and(atLeast);
				}

			}
		}

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (square[i][j] > 0) {
					formula = formula.and(new Formula(occupies[i][j][square[i][j] - 1]));
				}
			}
		}

		return formula;
	}

	/**
	 * Interpret the solved SAT problem as a filled-in grid.
	 * 
	 * @param e
	 *            Assignment of variables to values that solves this puzzle.
	 *            Requires that e came from a solution to this.getProblem().
	 * @return a new Sudoku grid containing the solution to the puzzle, with no
	 *         blank entries.
	 */
	public Sudoku interpretSolution(Environment e) {
		// TODO: implement this.
		assert e != null;

		int[] result = new int[size * size];
		int count = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					if (e.get(occupies[i][j][k]) == Bool.TRUE) {
						result[count] = k + 1;
						count += 1;
					}
				}
			}
		}
		int[][] resultChange = new int[size][size];
		resultChange = change1D(result, size);
		return new Sudoku(dim, resultChange);
	}

	private int[][] change1D(int[] x, int size) {
		int count = 0;
		int[][] result = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				result[i][j] = x[count];
				count += 1;
			}
		}
		return result;
	}
}
