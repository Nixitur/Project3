package ksat;

import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

public class Main {
	/**
	 * Starts the main Mass Solver.
	 * @param args The first value is the random clause generation variant to use ("1" or "2") and the second one is the number of variables to
	 * start with.
	 */
	public static void main(String[] args){
		int variant = Integer.parseInt(args[0]);
		System.out.println("The following lines are in the format");
		System.out.println("n : [m_3^["+variant+"](n), avg time for the last (n,m) combination, percentage of solved formulas "
				+ "for the last (n,m) combination]");
		int n = Integer.parseInt(args[1]);
		Map<Integer,Integer> nToMk = MassSolver.solve(n, variant);
		n = Collections.max(nToMk.keySet());
		System.out.println("For variant "+variant+", n_max is "+n+".");
		System.out.println("m_3^["+variant+"](n) is as follows:");
		System.out.println(orderedMapString(nToMk,10,n));
		int m = nToMk.get(n);
		MassSolver.solveB(n, m, variant);
	}
	
	/**
	 * Gets a Map that maps Integers to other objects in the proper order.
	 * @param map A Map that contains mappings for all Integer keys from start to end.
	 * @param start The starting index.
	 * @param end The end index.
	 * @return A String representation of map in the proper order.
	 */
	private static <X> String orderedMapString(Map<Integer,X> map, Integer start, Integer end){
		String result = "[";
		for (int i = start; i <= end; i++){
			X value = map.get(i);
			result += "["+i+", "+value+"], ";
		}
		result = result.substring(0, result.length()-2)+"]";
		return result;
	}
	
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
	public int[][] readInClauses(){
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
		return clauses;
	}
}
