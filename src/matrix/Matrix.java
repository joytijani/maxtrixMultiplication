package matrix;

import java.io.*;

public class Matrix {

	private class Worker implements Runnable {

		private int[][] matrixA;
		private int[][] matrixB;
		private int[][] ans;
		private int rowStart, rowEnd;

		public Worker(int[][] matrixA, int[][] matrixB, int[][] ans, int rowStart, int rowEnd) {
			this.matrixA = matrixA;
			this.matrixB = matrixB;
			this.ans = ans;
			this.rowStart = rowStart;
			this.rowEnd = rowEnd;
		}

		@Override
		public void run() {
			for (int i = rowStart; i < rowEnd; i++) {
				for (int j = 0; j < matrixB[0].length; j++) {
					for (int k = 0; k < matrixB.length; k++) {
						ans[i][j] += matrixA[i][k] * matrixB[k][j];
					}
				}

			}
		}

	}

	public int[][] matrix;
	int[][] trans;
	public int x, y;
	private boolean transposed;

	public Matrix(int x, int y) {
		matrix = new int[x][y];
		this.x = x;
		this.y = y;
		this.transposed = false;
	}

	/*
	 * This method takes in a 2d matrix array and returns the transposed matrix
	 * https://en.wikipedia.org/wiki/Transpose
	 */
	public int[][] transpose(int[][] arr) {
		if (transposed) {
			transposed = false;
		} else {
			transposed = true;
		}
		int[][] ans = new int[arr[0].length][arr.length];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				ans[j][i] = arr[i][j];
			}
		}
		arr = ans;
		return arr;
	}

	public void set(int[][] in) {
		this.matrix = in;
	}

	// Do NOT modify this method
	public void load(String path) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			System.err.println("file not found: " + path);
		}
		int row = 0;
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			String arr[] = line.split(" ");
			for (int i = 0; i < arr.length; i++) {
				matrix[row][i] = Integer.parseInt(arr[i]);
			}
			row++;
		}
		trans = transpose(matrix);
		transposed = true;
	}

	// Do NOT modify this method
	public String toString() {
		String aString = "";
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				aString += " " + matrix[row][col];
			}
			aString += "\r\n";
		}
		return aString;
	}

	// This is a Single Threaded matrix multiply.
	// Takes in a matrix and multiplies itself by it so (this x b)
	public Matrix multiply(Matrix b) {
		if (this.x == 0 || b.x == 0 || this.y == 0 || b.y == 0 || this == null || b == null) {
			return null;
		}
		if (this.y != b.x) {
			return null;
		} else {
			int[][] temp = new int[this.x][b.y];
			Matrix temp2 = new Matrix(this.x, b.y);
			for (int i = 0; i < this.x; i++) {
				for (int j = 0; j < b.y; j++) {
					for (int k = 0; k < b.x; k++) {
						temp[i][j] += this.matrix[i][k] * b.matrix[k][j];
					}
				}
			}
			temp2.set(temp);
			return temp2;
		}
	}

	// This method takes in a Matrix, and a number of threads and uses that number
	// of threads to
	// multiply the two matrices together. It should be in the order (this x m)
	public Matrix multiply(Matrix m, int threads) {
		int counter = 0;
		if (this.x == 0 || m.x == 0 || this.y == 0 || m.y == 0 || threads <= 0 || this == null || m == null) {
			return null;
		}

		if (this.y != m.x) {
			return null;
		} else {
			int[][] temp = new int[this.x][m.y];
			Matrix tempM = new Matrix(this.x, m.y);
			int numOfTimes = this.x / threads;
			Thread[] thread = new Thread[this.x];
			for (int i = 0; i < numOfTimes; i++) {
				if (i == numOfTimes - 1) {
					thread[i] = new Thread(new Worker(this.matrix, m.matrix, temp, counter, this.x - 1));
					thread[i].start();
				} else {
					thread[i] = new Thread(new Worker(this.matrix, m.matrix, temp, counter, counter + numOfTimes));
					thread[i].start();
				}
				counter += numOfTimes;
			}

			for (int i = 0; i < numOfTimes; i++) {
				try {
					thread[i].join();
				} catch (InterruptedException e) {
				}
			}
			tempM.set(temp);
			return tempM;
		}

	}

	// a method that should take in a matrix and determine if it is equal to this
	// matrix
	@Override
	public boolean equals(Object in) {
		if (in == this) {
			return true;
		}
		if (in == null || getClass() != in.getClass()) {
			return false;
		}
		Matrix m = (Matrix) in;
		if ((this.x == m.x) && (this.y == m.y)) {
			return true;
		} else {
			return false;
		}
	}

	// this is given as potentially useful starting point for testing
	public static void main(String[] args) {
		Matrix a = new Matrix(3, 4);
		Matrix b = new Matrix(4, 4);
		Matrix c = new Matrix(2, 4);

		int[][] cin = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
		int[][] ain = { { 1, 2, 3, 4 }, { 1, 2, 3, 4 }, { 1, 2, 3, 4 }// ,
				// {1, 2, 3, 4}
		};
		int[][] bin = { { 1, 2, 3, 4 }, { 1, 2, 3, 4 }, { 1, 2, 3, 4 }, { 1, 2, 3, 4 } };
		a.set(ain);
		b.set(bin);

		cin = c.transpose(cin);
		c.set(cin);
		System.out.println(c);
		// Matrix rem = a.multiply(a, b, 3);

		/*
		 * System.out.println(a); System.out.println(b); System.out.println(rem);
		 */

	}

}
