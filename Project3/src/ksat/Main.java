package ksat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
//		Scanner sc = new Scanner(System.in);
//		int noOfVars = sc.nextInt();
//		int noOfClauses = sc.nextInt();
//		int[][] clauses = new int[noOfClauses][3];
//		for (int i = 0; i < noOfClauses; i++){
//			for (int j = 0; j < 3; j++){
//				int v = sc.nextInt();
//				if ((v > noOfVars) || (-v > noOfVars) || (v == 0)){
//					sc.close();
//					throw new IllegalArgumentException("Variable index too high or zero.");
//				}
//				clauses[i][j] = v;
//			}
//		}
//		sc.close();
		int noOfVars = 21;
		int noOfClauses = 100;
		int[][] clauses = generateRandomClauses1(noOfVars,noOfClauses);
//		clauses = removeUselessClauses(clauses);
		System.out.println(arrayArrayToString(clauses));
		Solver s = new Solver(clauses,noOfVars);
		long startTime = System.currentTimeMillis();
		s.solve();
		long endTime = System.currentTimeMillis();
		System.out.println((endTime-startTime)+" ms");
	}
	
	private static int[][] generateRandomClauses1(int noOfVars, int noOfClauses){
		List<Integer> literals = new ArrayList<Integer>();
		for (int i = 1; i <= noOfVars; i++){
			literals.add(i);
			literals.add(-i);
		}
		int[][] clauses = new int[noOfClauses][3];
		Random rand = new Random();
		for (int i = 0; i < noOfClauses; i++){
			List<Integer> literalsCopy = new ArrayList<Integer>(literals);
			for (int j = 0; j < 3; j++){
				int index = rand.nextInt(literalsCopy.size());
				clauses[i][j] = literalsCopy.get(index);
				literalsCopy.remove(index);
			}
		}
		return clauses;
	}
	
	private static int[][] generateRandomClauses2(int noOfVars, int noOfClauses){
		List<Integer> variables = new ArrayList<Integer>();
		for (int i = 1; i <= noOfVars; i++){
			variables.add(i);
		}
		int[][] clauses = new int[noOfClauses][3];
		Random rand = new Random();
		for (int i = 0; i < noOfClauses; i++){
			List<Integer> variablesCopy = new ArrayList<Integer>(variables);
			for (int j = 0; j < 3; j++){
				int index = rand.nextInt(variablesCopy.size());
				int var = variablesCopy.get(index);
				variablesCopy.remove(index);
				if (rand.nextBoolean()){
					clauses[i][j] = var;
				} else {
					clauses[i][j] = -var;
				}
			}
		}
		return clauses;
	}
	
	private static int[][] removeUselessClauses(int[][] clauses){
		Map<Integer,int[]> hashToArray = new HashMap<Integer,int[]>();
		for (int[] clause : clauses){
			Arrays.sort(clause);
			boolean dupl = false;
			for (int lit : clause){
				int index = Arrays.binarySearch(clause, -lit);
				if (index >= 0){
					dupl = true;
					break;
				}
			}
			if (!dupl){
				int hash = Arrays.hashCode(clause);
				if (!hashToArray.containsKey(hash)){
					hashToArray.put(Arrays.hashCode(clause), clause);
				}
			}
		}
		int[][] result = new int[hashToArray.size()][3];
		int i = 0;
		for (int[] clause : hashToArray.values()){
			result[i++] = clause;
		}
		return result;
	}
	
	private static String arrayArrayToString(int[][] arg0){
		String result = "[";
		for (int[] array : arg0){
			result += Arrays.toString(array)+", ";
		}
		result = result.substring(0,result.length()-2) + "]";
		return result;
	}
}
