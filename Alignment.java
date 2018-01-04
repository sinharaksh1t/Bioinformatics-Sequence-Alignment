

public class Alignment implements Comparable<Alignment> {
	int score;
	int start1;
	String id1;
	String s1;
	int start2;
	String id2;
	String s2;
	long runtime;

	Alignment(int score, int start1, String id1, String s1, int start2, String id2, String s2, long runtime) {
		this.score = score;
		this.start1 = start1;
		this.id1 = id1;
		this.s1 = s1;
		this.start2 = start2;
		this.id2 = id2;
		this.s2 = s2;
		this.runtime = runtime;
	}

	@Override
	public int compareTo(Alignment a1) {
		// TODO Auto-generated method stub
		if(a1.score > this.score)
			return 1;
		else if(a1.score < this.score)
			return -1;
		return 0;
	}
}
