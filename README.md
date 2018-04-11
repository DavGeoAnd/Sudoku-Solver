# Sudoku-Solver
This uses a basic SAT Solver for sudoku problems while timing how long it takes to complete the problems.

Screenshot(33) - shows results of running ps4/src/sudoku/Main

ps4/src/sudoku/Main - starts the program and timer

ps4/src/sudoku/Sudoku - class for setting up the sudoku problem before the SAT solver. I was given the skeleton of the program so I complete the methods: Sudoku (both), fromFile, getProblem, and interpretSolution

ps4/src/sat/SATSolver - class that takes the formula created from ps4/src/sudoku/Sudoku and solves the problem is possible