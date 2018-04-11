package sudoku;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import immutable.ImList;
import sat.env.Bool;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sudoku.Sudoku.ParseException;

public class SudokuTest {

	// make sure assertions are turned on!
	// we don't want to run test cases without assertions too.
	// see the handout to find out how to turn them on.
	@Test(expected = AssertionError.class)
	public void testAssertionsEnabled() {
		assert false;
	}

	// TODO: put your test cases here
	@Test
	public void testSudoku() throws IOException, ParseException {
		// test empty using toString()
		Sudoku test = new Sudoku(2);
		assertTrue(test.toString().equals("...." + "\n" + "...." + "\n" + "...." + "\n" + "...." + "\n"));

		// test a custom using toString()
		int[][] test1 = new int[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				test1[i][j] = i + j;
			}
		}
		Sudoku test2 = new Sudoku(2, test1);
		assertTrue(test2.toString().equals(".123" + "\n" + "1234" + "\n" + "2345" + "\n" + "3456" + "\n"));

		// test fromFile() using toString()
		Sudoku test3 = Sudoku.fromFile(2, "samples/sudoku_4x4.txt");
		assertTrue(test3.toString().equals(".234" + "\n" + "341." + "\n" + "214." + "\n" + ".321" + "\n"));
		Formula temp = test3.getProblem();
		temp.toString();
		
		// test an empty 1 dim with custom environment
		Sudoku test4 = new Sudoku(1);
		Formula test5 = test4.getProblem();
		ImList<Clause> test6 = test5.getClauses();
		Environment e = new Environment();
		e.put(test6.first().chooseLiteral().getVariable(), Bool.FALSE);
		Sudoku test7 = test4.interpretSolution(e);
		System.out.print(test7);

		// test a non-empty 1 dim with custom environment
		int[][] test8 = new int[1][1];
		test8[0][0] = 1;
		Sudoku test9 = new Sudoku(1, test8);
		Formula test10 = test9.getProblem();
		ImList<Clause> test11 = test10.getClauses();
		Environment e1 = new Environment();
		Variable v = test11.first().chooseLiteral().getVariable();
		e1 = e1.put(v, Bool.TRUE);
		Sudoku test12 = test9.interpretSolution(e1);
		System.out.print(test12);
	}

}