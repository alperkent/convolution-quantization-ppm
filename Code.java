import java.util.*;
import java.io.*;

public class MAE2019742009 {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		int mode = Integer.parseInt(args[0]); // gets the mode of the program
		String fileName = args[1]; // gets the name of the input file
		Scanner input = new Scanner(new File(fileName)); // creates a scanner that will read the input file
		int[][][] array = readPPM(input); // creates a 3-D array from the input file
		
		// these if statements determine the output of the program according to the mode argument
		if (mode == 0) {
			PrintStream output = new PrintStream(new File("output.ppm")); // creates the output file and the printstream object to write on it
			writePPM(output, array);
		} else if (mode == 1) {
			PrintStream output = new PrintStream(new File("black-and-white.ppm")); // creates the output file and the printstream object to write on it
			writePPM(output, grayscale(array));
		} else if (mode == 2) {
			String filterName = args[2]; // gets the name of the filter file
			Scanner filterSc = new Scanner(new File(filterName)); // creates a scanner that will read the filter file
			PrintStream output = new PrintStream(new File("convolution.ppm")); // creates the output file and the printstream object to write on it
			writePPM(output, grayscale(convolution(array, filterSc)));
		} else if (mode == 3) {
			int range = Integer.parseInt(args[2]); // gets the range of quantization
			PrintStream output = new PrintStream(new File("quantized.ppm")); // creates the output file and the printstream object to write on it
			writePPM(output, quantization(array, range));
		}
		
	}
	
	// reads the input file and writes it into an array and returns the array
	public static int[][][] readPPM(Scanner input) {
		
		input.next(); // skips "P3"
		int cols = input.nextInt(); // gets the number of columns for the output array from the file
		int rows = input.nextInt(); // gets the number of rows for the output array from the file
		input.nextInt(); // skips "255"
		
		int[][][] arr = new int[rows][cols][3]; // creates the output array
		
		// these three nested for loops assign the value of every element of the array from the file
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				for (int k = 0; k < 3; k++) {
					arr[i][j][k] = input.nextInt();
				}
			}
		}
		
		return arr; 
		
	}
	
	// writes the given array into the output file 
	public static void writePPM(PrintStream output, int[][][] array) {
		
		int rows = array.length; // gets the number of rows in the array
		int cols = array[0].length; // gets the number of columns in the array
		
		output.println("P3");
		output.println(cols + " " + rows);
		output.println("255");
		
		// these three nested for loops write every value of the array into the output file
		for (int i = 0; i < rows; i++) { 
			for (int j = 0; j < cols; j++) {
				for (int k = 0; k < 3; k++) {
					output.print(array[i][j][k] + " ");
				}
				output.print("\t");
			}
			output.println();
		}
		
	}
	
	// averages out RGB values of every pixel in the given array and returns the grayscale version of the array
	public static int[][][] grayscale(int[][][] array) {
		
		int rows = array.length; // gets the number of rows in the input array
		int cols = array[0].length; // gets the number of columns in the input array
		int[][][] grayed = new int[rows][cols][3]; // creates the output array whose size is the same with the input array
		
		// these two nested for loops parse every pixel in the input array
		for (int i = 0; i < rows; i++) { 
			for (int j = 0; j < cols; j++) {
				int sum = 0; // creates a variable for storing the sum of all RGB values of the pixel
				// adds up the RGB values of the pixel
				for (int k = 0; k < 3; k++) { 
					sum += array[i][j][k];
				}
				// changes the RGB values of the pixel to their average
				for (int k = 0; k < 3; k++) {
					grayed[i][j][k] = sum/3;
				}
			}
		}
		
		return grayed;
		
	}
	
	// applies the given filter to every value of the given array and returns the convolved version of the array
	public static int[][][] convolution(int[][][] array, Scanner filterSc) {
		
		String size = filterSc.next(); // gets the line that contains number of rows and columns from the file
		int rowsF = Integer.parseInt(size.substring(0, 1)); // gets the number of rows in the filter
		int colsF = Integer.parseInt(size.substring(2, 3)); // gets the number of columns in the filter
		int[][] filter = new int[rowsF][colsF]; // creates the filter array
		
		// these two nested for loops assign the value of every element of the filter array from the file
		for (int i = 0; i < rowsF; i++) { 
			for (int j = 0; j < colsF; j++) {
				filter[i][j] = filterSc.nextInt();
			}
		}
		
		int rows = array.length; // gets the number of rows in the input array
		int cols = array[0].length; // gets the number of columns in the input array
		int rowsC = rows-(rowsF-1); // gets the number of rows for the output array
		int colsC = cols-(colsF-1); // gets the number of columns for the output array
		int[][][] convolved = new int[rowsC][colsC][3]; // creates the output array
		
		// these three nested for loops parse every element in the output array
		for (int i = rowsF/2; i < rows-rowsF/2; i++) { 
			for (int j = colsF/2; j < cols-colsF/2; j++) {
				for (int k = 0; k < 3; k++) {
					int sum = 0; // creates a variable for storing the sum of the multiplication of corresponding values in the array and the filter
					// these two nested for loops parse every element in the filter array
					for (int l = 0; l < rowsF; l++) { 
						for (int m = 0; m < colsF; m++) {
							sum += array[i-rowsF/2+l][j-colsF/2+m][k] * filter[l][m];
						}
					}
					// these if conditions set the sum equal to 255 if it exceeds 255 or to 0 if it is negative
					if (sum > 255) { 
						sum = 255;
					} else if (sum < 0) {
						sum = 0;
					}
					convolved[i-rowsF/2][j-colsF/2][k] = sum;
				}
			}
		}
		
		return convolved;
		
	}
	
	// runs the "quantizer" method with the given range for every value of the given array and returns the quantized version of the array
	public static int[][][] quantization(int[][][] array, int range) {
		
		int rows = array.length; // gets the number of rows in the input array
		int cols = array[0].length; // gets the number of columns in the input array
		int[][][] quantized = Arrays.copyOf(array, array.length); // creates the output array as a copy of the input array
		boolean[][][] check = new boolean[rows][cols][3]; // creates a boolean array whose size is the same with the input array
		
		// these three nested for loops run the "quantizer" method for every element in the output array in order
		for (int k = 0; k < 3; k++) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					int value = quantized[i][j][k]; // gets the value of the element
					quantizer(quantized, check, i, j, k, value, range);
				}
			}
		}
		
		return quantized;
		
	}
	
	// checks if the given indices are within the given array and returns a boolean value
	public static boolean isValid(int[][][] quantized, int i, int j, int k) {
		
		return i >= 0 && i < quantized.length && j >= 0 && j < quantized[0].length && k >= 0 && k < 3;
		
	}
	
	// checks if the value of the given indices in the given array are within the range of the given value
	public static boolean inRange(int[][][] quantized, int i, int j, int k, int value, int range) {
		
		return quantized[i][j][k] <= value+range && quantized[i][j][k] >= value-range;
		
	}
	
	// recursively checks if the values of the neighboring elements in the given array are within the range of the value in the given indices and sets them equal to the given value
	public static void quantizer(int[][][] quantized, boolean[][][] check, int i, int j, int k, int value, int range) {
		
		check[i][j][k] = true;
		
		// these if statements check every neighbor of the given indices in order and if neighbor indices are within the input array, not previously called and its value is within the given range, equate neighbor's value to given value and recursively call the method for that neighbor
		if (isValid(quantized, i+1, j, k) && !check[i+1][j][k] && inRange(quantized, i+1, j, k, value, range)) {
			quantized[i+1][j][k] = value;
			quantizer(quantized, check, i+1, j, k, value, range);
		}
		if (isValid(quantized, i-1, j, k) && !check[i-1][j][k] && inRange(quantized, i-1, j, k, value, range)) {
			quantized[i-1][j][k] = value;
			quantizer(quantized, check, i-1, j, k, value, range);
		}
		if (isValid(quantized, i, j+1, k) && !check[i][j+1][k] && inRange(quantized, i, j+1, k, value, range)) {
			quantized[i][j+1][k] = value;
			quantizer(quantized, check, i, j+1, k, value, range);
		}
		if (isValid(quantized, i, j-1, k) && !check[i][j-1][k] && inRange(quantized, i, j-1, k, value, range)) {
			quantized[i][j-1][k] = value;
			quantizer(quantized, check, i, j-1, k, value, range);
		}
		if (isValid(quantized, i, j, k+1) && !check[i][j][k+1] && inRange(quantized, i, j, k+1, value, range)) {
			quantized[i][j][k+1] = value;
			quantizer(quantized, check, i, j, k+1, value, range);
		}
		if (isValid(quantized, i, j, k-1) && !check[i][j][k-1] && inRange(quantized, i, j, k-1, value, range)) {
			quantized[i][j][k-1] = value;
			quantizer(quantized, check, i, j, k-1, value, range);
		}
		
	}
	
}
