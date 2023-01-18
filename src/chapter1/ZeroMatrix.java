package chapter1;

import common.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Write an algorithm such that if an element in an M x N matrix is 0, its entire row and column is set to zero
 */
public class ZeroMatrix implements Testable {
    public int[][] zeroMatrixLinear(int[][] matrix, int height, int width) {
        // O(M*N) work
        // O(1) space
        int[][] result = ArrayUtil.copyMatrix(matrix);
        if(width == 0 || height == 0) {
            return result;
        }

        // don't use up any extra space by storing zero values in the first row and column.
        // we use these two boolean flags to avoid the matrix from being overwritten

        boolean firstColumnZero = false;
        boolean firstRowZero = false;
        for(int x = 0; x < width; x++) {
            if(result[0][x] == 0) {
                firstRowZero = true;
                break;
            }
        }
        for(int y = 0; y < height; y++) {
            if(result[y][0] == 0) {
                firstColumnZero = true;
                break;
            }
        }

        // set zeroes in the first column
        for(int y = 1; y < height; y++) {
            for(int x = 1; x < width; x++) {
                int item = result[y][x];
                if(item == 0) {
                    result[0][x] = 0;
                    result[y][0] = 0;
                }
            }
        }
        // extend zeroes from first column and first row to the rest
        for(int x = 1; x < width; x++) {
            if(result[0][x] == 0) {
                for(int y = 1; y < height; y++) {
                    result[y][x] = 0;
                }
            }
        }
        for(int y = 1; y < height; y++) {
            if(result[y][0] == 0) {
                for(int x = 1; x < width; x++) {
                    result[y][x] = 0;
                }
            }
        }
        // set zeroes on first column and first row if necessary
        if(firstColumnZero) {
            for(int y = 0; y < height; y++)
                result[y][0] = 0;
        }
        if(firstRowZero) {
            for(int x = 0; x < width; x++)
                result[0][x] = 0;
        }
        return result;
    }
    public int[][] zeroMatrixParallel(int[][] matrix, int height, int width) {
        // Work: O(M * N)
        // Span: O(log M + log N)
        // Space: O(M * N) (we create a job for each element in matrix, but can be lower if there are less than M*N processors)

        // reduce matrix columns to a signifier if there's a 0 in the column
        // do the same with rows
        // Tabulate result to zero out rows and columns

        int[][] result = ArrayUtil.copyMatrix(matrix);

        boolean[] heightZero = new boolean[height];
        IntBinaryOperator hasZero = (left, right) -> left == 0 || right == 0 ? 0 : 1;
        IntStream.range(0, height).parallel().forEach(y -> {
            boolean zeroRow = IntStream.range(0, width).parallel()
                    .map(x -> result[y][x])
                    .reduce(hasZero).orElse(1) == 0;
            heightZero[y] = zeroRow;
        });
        boolean[] widthZero = new boolean[width];
        IntStream.range(0, width).parallel().forEach(x -> {
            boolean zeroColumn = IntStream.range(0, height).parallel()
                    .map(y -> result[y][x])
                    .reduce(hasZero).orElse(1) == 0;
            widthZero[x] = zeroColumn;
        });

        // zero out columns and rows in parallel for all elements of the matrix
        IntStream.range(0, width * height).parallel().forEach( idx -> {
            int y = idx / width;
            int x = idx % width;
            if(heightZero[y] || widthZero[x]) {
                result[y][x] = 0;
            }
        });
        return result;
    }

    private final List<Tuple2<Tuple3<int[][], Integer, Integer>, int[][]>> testCases = Tuple2.of2(
        Tuple3.make(new int[4][0], 4, 0), new int[4][0],
        Tuple3.make(new int[0][4], 4, 0), new int[0][4],
        Tuple3.make(new int[][]{
            {0, 1, 2, 3},
            {1, 2, 3, 4},
            {4, 3, 0, 2}
        }, 3, 4), new int[][]{
            {0, 0, 0, 0},
            {0, 2, 0, 4},
            {0, 0, 0, 0},
        }
    );
    private List<Tuple2<String, Function3<int[][], Integer, Integer, int[][]>>> testFunctions = List.of(
            Tuple2.make("zeroMatrixLinear", this::zeroMatrixLinear),
            Tuple2.make("zeroMatrixParallel", this::zeroMatrixParallel)
    );
    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases, Arrays::deepEquals, ZeroMatrix.class, "");
    }
}
