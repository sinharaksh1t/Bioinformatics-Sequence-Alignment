
import java.io.*;
import java.util.*;

public class hw1 {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[])throws IOException {
		int algoType = Integer.parseInt(args[0]);
		String queryFile = args[1];
		String dataFile = args[2];
		String alphabetFile = args[3];
		String scoreMatrixFile = args[4];
		int k = Integer.parseInt(args[5]);
		//m is the gap penalty
		int m = Integer.parseInt(args[6]);
		HashMap<Character, Integer> alphabet = alphabetParser(alphabetFile);
		int[][] scorematrix = scorematrixParser(scoreMatrixFile);
		HashMap<String, String> database = sequenceParser(dataFile);
		HashMap<String, String> query = sequenceParser(queryFile);
		ArrayList<Alignment> arrList = new ArrayList<Alignment>();
		
		Set<?> querySet = query.entrySet();
		Iterator<?> i1 = querySet.iterator();
		while (i1.hasNext()) {
			Map.Entry<String, String> queryEntry = (Map.Entry<String, String>) i1.next();
			Set<?> databaseSet = database.entrySet();
			Iterator<?> i2 = databaseSet.iterator();
			String queryID = queryEntry.getKey();
			String querySequence = queryEntry.getValue();
			while (i2.hasNext()) {
				Map.Entry<String, String> databaseEntry = (Map.Entry<String, String>) i2.next();
				String databaseSeqID = databaseEntry.getKey();
				String databaseSequence = databaseEntry.getValue();
				switch (algoType) {
				case 1:
					Alignment GlobalAlignment = GlobalAlignment(querySequence, databaseSequence, scorematrix, alphabet, m, queryID, databaseSeqID);
					arrList.add(GlobalAlignment);
					break;
				case 2:
					Alignment LocalAlignment = LocalAlignment(querySequence, databaseSequence, scorematrix, alphabet, m, queryID, databaseSeqID);
					arrList.add(LocalAlignment);
					break;
				case 3:
					Alignment DovetailAlignment = DovetailAlignment(querySequence, databaseSequence, scorematrix, alphabet, m, queryID, databaseSeqID);
					arrList.add(DovetailAlignment);
					break;
				}
			}
		}
		Collections.sort(arrList);
		for (int i = 0; i < k; i++) {
			System.out.println("Score = "+arrList.get(i).score);
			System.out.println(arrList.get(i).id1 + "\t" + arrList.get(i).start1 + "\t" + arrList.get(i).s1);
			System.out.println(arrList.get(i).id2 + "\t" + arrList.get(i).start2 + "\t" + arrList.get(i).s2);
			System.out.println();
		}		
	}//end of main method

	public static Alignment DovetailAlignment(String querySequence, String databaseSequence, int[][] scorematrix,
			HashMap<Character, Integer> alphabet, int gap, String queryID, String databaseSeqID) {
		final long initruntime = System.nanoTime();
		int[][] outputMatrix = new int[querySequence.length() + 1][databaseSequence.length() + 1];
		querySequence = querySequence.toUpperCase();
		databaseSequence = databaseSequence.toUpperCase();
		
		int maxcv = Integer.MIN_VALUE, maxrv = Integer.MIN_VALUE, ci = 0, cj = 0, ri = 0, rj = 0;
		for (int i = 1; i <= querySequence.length(); i++) {
			for (int j = 1; j <= databaseSequence.length(); j++) {
				outputMatrix[i][j] = Math.max(outputMatrix[i][j - 1] + gap,
						outputMatrix[i - 1][j] + gap);
				outputMatrix[i][j] = Math.max(outputMatrix[i][j], outputMatrix[i - 1][j - 1] 
						+ scorematrix[alphabet.get(querySequence.charAt(i - 1))][alphabet.get(databaseSequence.charAt(j - 1))]);
				
				if (j == databaseSequence.length() && outputMatrix[i][j] >= maxcv) {
					maxcv = outputMatrix[i][j];
					ci = i;
					cj = j;
				}
				if (i == querySequence.length() && outputMatrix[i][j] >= maxrv) {
					maxrv = outputMatrix[i][j];
					rj = j;
					ri = i;
				}
			}
		}
		StringBuilder sb1 = new StringBuilder("");
		StringBuilder sb2 = new StringBuilder("");
		int maxvalue = (maxcv >= maxrv) ? maxcv : maxrv;
		int i = (maxcv >= maxrv) ? ci : ri;
		int j = (maxcv >= maxrv) ? cj : rj;
		while (i > 0 && j > 0) {
			if (i > 0 && j > 0 && outputMatrix[i][j] == (outputMatrix[i - 1][j - 1]
					+ scorematrix[alphabet.get(querySequence.charAt(i - 1))][alphabet.get(databaseSequence.charAt(j - 1))])) {
				String st1 = sb1.toString();
				sb1 = new StringBuilder();
				sb1.append(querySequence.charAt(i - 1));
				sb1.append(st1);
				String st2 = sb2.toString();
				sb2 = new StringBuilder();
				sb2.append(databaseSequence.charAt(j - 1));
				sb2.append(st2);
				i--;
				j--;
			} else if (i > 0 && outputMatrix[i][j] == outputMatrix[i - 1][j] + gap) {
				String st1 = sb1.toString();
				sb1 = new StringBuilder();
				sb1.append(querySequence.charAt(i - 1));
				sb1.append(st1);
				String st2 = sb2.toString();
				sb2 = new StringBuilder();
				sb2.append('.');
				sb2.append(st2);
				i--;
			} else {
				String st1 = sb1.toString();
				sb1 = new StringBuilder();
				sb1.append('.');
				sb1.append(st1);
				String st2 = sb2.toString();
				sb2 = new StringBuilder();
				sb2.append(databaseSequence.charAt(j - 1));
				sb2.append(st2);
				j--;
			}
		}
		final long runtime = System.nanoTime() - initruntime;
		Alignment a1 = new Alignment(maxvalue, (i+1), queryID, sb1.toString(), (j+1), databaseSeqID, sb2.toString(), runtime);
		return a1;
	}

	private static Alignment LocalAlignment(String querySequence, String databaseSequence, int[][] scorematrix,
			HashMap<Character, Integer> alphabet, int gap, String queryID, String databaseSeqID) {
		// TODO Auto-generated method stub
		final long initruntime = System.nanoTime();
		int[][] outputMatrix = new int[querySequence.length() + 1][databaseSequence.length() + 1];
		querySequence = querySequence.toUpperCase();
		databaseSequence = databaseSequence.toUpperCase();
		int maxValue = Integer.MIN_VALUE, maxi = 0, maxj = 0;
		for (int i = 1; i <= querySequence.length(); i++) {
			for (int j = 1; j <= databaseSequence.length(); j++) {
				outputMatrix[i][j] = Math.max(outputMatrix[i][j - 1] + gap,
						outputMatrix[i - 1][j] + gap);
				outputMatrix[i][j] = Math.max(outputMatrix[i][j], outputMatrix[i - 1][j - 1]
						+ scorematrix[alphabet.get(querySequence.charAt(i - 1))][alphabet.get(databaseSequence.charAt(j - 1))]);
				outputMatrix[i][j] = Math.max(outputMatrix[i][j], 0);
				if (outputMatrix[i][j] >= maxValue) {
					maxValue = outputMatrix[i][j];
					maxi = i;
					maxj = j;
				}
			}
		}
		
		StringBuilder sb1 = new StringBuilder("");
		StringBuilder sb2 = new StringBuilder("");
		int i = maxi;
		int j = maxj;
		while (i > 0 || j > 0) {
			if (i > 0 && j > 0 && outputMatrix[i][j] == (outputMatrix[i - 1][j - 1]
					+ scorematrix[alphabet.get(querySequence.charAt(i - 1))][alphabet.get(databaseSequence.charAt(j - 1))])) {
				String st1 = sb1.toString();
				sb1 = new StringBuilder();
				sb1.append(querySequence.charAt(i - 1));
				sb1.append(st1);
				String st2 = sb2.toString();
				sb2 = new StringBuilder();
				sb2.append(databaseSequence.charAt(j - 1));
				sb2.append(st2);
				i--;
				j--;
			} else if (i > 0 && outputMatrix[i][j] == outputMatrix[i - 1][j] + gap) {
				String st1 = sb1.toString();
				sb1 = new StringBuilder();
				sb1.append(querySequence.charAt(i - 1));
				sb1.append(st1);
				String st2 = sb2.toString();
				sb2 = new StringBuilder();
				sb2.append('.');
				sb2.append(st2);
				i--;
			} else if (j > 0 && outputMatrix[i][j] == outputMatrix[i][j - 1] + gap) {
				String st1 = sb1.toString();
				sb1 = new StringBuilder();
				sb1.append('.');
				sb1.append(st1);
				String st2 = sb2.toString();
				sb2 = new StringBuilder();
				sb2.append(databaseSequence.charAt(j - 1));
				sb2.append(st2);
				j--;
			} else {
				break;
			}
		}
		final long runtime = System.nanoTime() - initruntime;
		Alignment a1 = new Alignment(maxValue, (i+1), queryID, sb1.toString(), (j+1), databaseSeqID, sb2.toString(), runtime);
		return a1;
	}

	private static Alignment GlobalAlignment(String querySequence, String databaseSequence, int[][] scorematrix,
			HashMap<Character, Integer> alphabet, int gap, String queryID, String databaseSeqID) {
		// TODO Auto-generated method stub
		final long initruntime = System.nanoTime();
		int i, j, scoreDiag, scoreLeft, scoreUp;
		querySequence = querySequence.toUpperCase();
		databaseSequence = databaseSequence.toUpperCase();
		int[][] outputMatrix = new int[querySequence.length() + 1][databaseSequence.length() + 1];
		for (i = 1; i <= querySequence.length(); i++)
		    outputMatrix[i][0] = outputMatrix[i - 1][0] + gap;
		for (j = 1; j <= databaseSequence.length(); j++)
		    outputMatrix[0][j] = outputMatrix[0][j - 1] + gap;
		
		for (i = 1; i <= querySequence.length(); i++) {
		    for (j = 1; j <= databaseSequence.length(); j++) {
		        scoreDiag = outputMatrix[i - 1][j - 1]  
		        		+ scorematrix[alphabet.get(querySequence.charAt(i-1))][alphabet.get(databaseSequence.charAt(j-1))];
		        scoreLeft = outputMatrix[i][j - 1] + gap;
		        scoreUp = outputMatrix[i - 1][j] + gap;
		        outputMatrix[i][j] = Math.max(Math.max(scoreDiag, scoreLeft), scoreUp);
		    }
		}
		
		int score = outputMatrix[querySequence.length()][databaseSequence.length()];
		i = querySequence.length();
		j = databaseSequence.length();
		StringBuilder sbA = new StringBuilder();
		StringBuilder sbB = new StringBuilder();
		while (i > 0 || j > 0) {
			if (i > 0 && j > 0 && outputMatrix[i][j] == outputMatrix[i - 1][j - 1]
					+ scorematrix[alphabet.get(querySequence.charAt(i-1))][alphabet.get(databaseSequence.charAt(j-1))]) {
				sbA.insert(0, querySequence.charAt(i - 1));
				sbB.insert(0, databaseSequence.charAt(j - 1));
				i--;
				j--;
			} else if (i > 0 && outputMatrix[i][j] == outputMatrix[i - 1][j] + gap) {
				sbA.insert(0, querySequence.charAt(i - 1));
				sbB.insert(0, '.');
				i--;
			} else {
				sbA.insert(0, '.');
				sbB.insert(0, databaseSequence.charAt(j - 1));
				j--;
			}
		}
		final long runtime = System.nanoTime() - initruntime;
		Alignment a1 = new Alignment(score, 1, queryID, sbA.toString(), 1, databaseSeqID, sbB.toString(), runtime);
		return a1;
	}
	
	public static HashMap<Character, Integer> alphabetParser(String arg)throws IOException {
		HashMap<Character, Integer> hm = new HashMap<Character, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(arg));
		String s = br.readLine();
		char[] c = s.toCharArray();
		for (int i = 0; i < c.length; i++)
			hm.put(c[i], i);
		return hm;
	}

	private static HashMap<String, String> sequenceParser(String dataFile) throws IOException {
		// TODO Auto-generated method stub
		// Parsing database sequence in String(Builder?)/StringBuffer
		HashMap<String, String> hm = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(dataFile));
		StringBuilder s = new StringBuilder();
		String line = "";
		String[] temp, temp1;
		while ((line = br.readLine()) != null) {
			if (line.startsWith(">hsa")) {
				if (s != null)
					s.append(",");
				temp = line.split(" ");
				temp1 = temp[0].split(":");
				s.append(temp1[1] + ":");
			} else
				s.append(line);
		}
		temp = s.toString().split(",");
		for (int i = 1; i < temp.length; i++) {
			temp1 = temp[i].split(":");
			hm.put(temp1[0], temp1[1]);
		}
		br.close();
		return hm;
	}

	private static int[][] scorematrixParser(String scoreMatrixFile) {
		// TODO Auto-gene rated method stub
		File file = new File(scoreMatrixFile);
		Scanner in = null;
		int[][] arr = new int[4][4];
		int i = 0, j = 0;
		try {
			in = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(in.hasNextLine()) {
			arr[i][j] = in.nextInt();
			j++;
			if(j == 4) {
				i++;
				j=0;
			}
		}
		return arr;
	}
}
