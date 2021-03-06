package ksat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MassSolver {
	private static final int NO_OF_TRIES = 100;
	private static final long MAX_TIME = 3000;
	public static final int GENERATION_VARIANT_1 = 1;
	public static final int GENERATION_VARIANT_2 = 2;
	
	public static Map<Integer,Integer> solve(int startingNoOfVars, int generationVariant){
		Map<Integer,Integer> nToMk = new HashMap<Integer,Integer>();
		int n = startingNoOfVars;
		int m = 1;
		int mk = 1;
		boolean outOfTime = false;
		while (!outOfTime){
			Tuple t = solveForSpecificN(n, m, generationVariant);
			mk = t.mk;
			outOfTime = (t.avgTime > MAX_TIME);
			nToMk.put(n, mk);
			System.out.println(n+": "+t.toString());
			if (!outOfTime){
				n++;
				// For n+1, mk MUST be larger than for n. Thus, start from the last discovered mk.
				m = mk;
			}
		}
		return nToMk;
	}
	public static void solveB(int n_max, int m, int generationVariant) {
		int m_from = m - m/10;
		int m_to = m + m/10;
		System.out.println("% for n_max, m from m - 10% to m + 10%");
		for(int i=m_from;i<=m_to;i++) {
			Tuple result = new Tuple();
			result = MassSolver.testForNM(n_max,i,1);
			System.out.println("m: "+i+" %: "+result.percentage);
		}
		
	}
	
	private static Tuple solveForSpecificN(int noOfVars, int startingNoOfClauses, int generationVariant){
		Tuple result = new Tuple();
		int n = noOfVars;
		int m = startingNoOfClauses;
		double lastPercentage = 100.0;
		double curPercentage = 100.0;
		while (curPercentage > 50.0){
			result = MassSolver.testForNM(n, m, generationVariant);
			lastPercentage = curPercentage;
			curPercentage = result.percentage;
			m++;
		}
		// At this point, lastPercentage is the last one still OVER 50 and curPercentage the first one UNDER 50.
		double diffToLast = lastPercentage - 50.0;
		double diffToNext = 50.0 - curPercentage;
		if (diffToNext < diffToLast){
			result.mk = m;
		} else {
			result.mk = m-1;
		}
		return result;
	}
	
	public static Tuple testForNM(int noOfVars, int noOfClauses, int generationVariant){
		int solved = 0;
		int[][] clauses;
		long curTime = 0;
		for (int i = 0; i < NO_OF_TRIES; i++){
			if (generationVariant == GENERATION_VARIANT_1){
				clauses = generateRandomClauses1(noOfVars,noOfClauses);
			} else if (generationVariant == GENERATION_VARIANT_2){
				clauses = generateRandomClauses2(noOfVars,noOfClauses);
			} else {
				throw new IllegalArgumentException("generationVariant must be 1 or 2.");
			}
			clauses = removeUselessClauses(clauses);
			Solver s = new Solver(clauses,noOfVars);
			long startTime = System.currentTimeMillis();
			if (s.solve(false)){
				solved++;
			}
			long endTime = System.currentTimeMillis();
			curTime += endTime-startTime;
		}
		double percentage = (double)solved / (double)NO_OF_TRIES * 100.0;
		double avgTime = (double)curTime / (double)NO_OF_TRIES;
		Tuple result =  new Tuple();
		result.avgTime = avgTime;
		result.percentage = percentage;
		return result;
	}
	
	/**
	 * Generates a random formula by variant 1.
	 * @param noOfVars The number of variables to use.
	 * @param noOfClauses The number of clauses to generate.
	 * @return A random formula in the form of an array of arrays. Each sub-array stands for one clause.
	 */
	private static int[][] generateRandomClauses1(int noOfVars, int noOfClauses){
		List<Integer> literals = new ArrayList<Integer>();
		for (int i = 1; i <= noOfVars; i++){
			literals.add(i);
			literals.add(-i);
		}
		int[][] clauses = new int[noOfClauses][3];
		Random rand = new Random();
		for (int i = 0; i < noOfClauses; i++){
			for (int j = 0; j < 3; j++){
				int index = rand.nextInt(literals.size());
				clauses[i][j] = literals.get(index);
			}
		}
		return clauses;
	}
	
	/**
	 * Generates a random formula by variant 2.
	 * @param noOfVars The number of variables to use.
	 * @param noOfClauses The number of clauses to generate.
	 * @return A random formula in the form of an array of arrays. Each sub-array stands for one clause.
	 */
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
	
	/**
	 * Removes unnecessary clauses from a formula.
	 * @param clauses A formula.
	 * @return The same formula with all duplicate clauses removed as well as all clauses that contain a formula (x OR not(x)) for 
	 * a variable x.
	 */
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
}
