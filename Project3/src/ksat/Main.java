package ksat;

import java.util.Random;
import java.util.Scanner;

public class Main {
	
	/**
	 * Reads in a 3-KNF formula from the standard input. The input is the number of variables, then a new line, followed by the
	 * number of clauses, then a new line. After this, each 3-KNF clause is on a new line written as a space-delimited list while each
	 * literal is a number. Positive numbers indicate non-negated variables, negative ones indicate the negated variables. For example,
	 * the clause<br>
	 * x2 v not(x4) v x6<br>
	 * would be written as<br>
	 * 2 -4 6<br>
	 * Hence, variables are 1-indexed.
	 */
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		int noOfVars = sc.nextInt();
		int noOfClauses = sc.nextInt();
		int[][] clauses = new int[noOfClauses][3];
		for (int i = 0; i < noOfClauses; i++){
			for (int j = 0; j < 3; j++){
				int v = sc.nextInt();
				if ((v > noOfVars) || (-v > noOfVars) || (v == 0)){
					sc.close();
					throw new IllegalArgumentException("Variable index too high or zero.");
				}
				clauses[i][j] = v;
			}
		}
		sc.close();
		Solver s = new Solver(clauses,noOfVars);
		s.solve();
	}
	
	private static int[][] generateRandomClauses(int noOfVars, int noOfClauses){
		int[][] clauses = new int[noOfClauses][3];
		Random rand = new Random();
		for (int i = 0; i < noOfClauses; i++){
			for (int j = 0; j < 3; j++){
				int var = rand.nextInt(noOfVars)+1;
				if (rand.nextBoolean()){
					clauses[i][j] = var;
				} else {
					clauses[i][j] = -var;
				}
			}
		}
		return clauses;
	}
}
