package chapter1;

import common.ArrayUtil;
import common.tests.TestException;
import common.tests.Testable;
import common.tuple.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Given an image represented by an NxN matrix, where each pixel in the image is 4 bytes, write a method to rotate
 * the image by 90 degrees. Can you do this in place?
 */
public class RotateMatrix implements Testable {

    public int[][] rotateMatrixInPlace(int[][] image) {
        int length = image.length;
        // copy for the purposes of testing, algo is done in place
        int[][] result = ArrayUtil.copyMatrix(image);

        for(int depth = 0; depth < length / 2; depth++) {
            int outerDepth = length - depth - 1;
            // journey from inner depth to outer depth
            for(int offset = depth; offset < outerDepth; offset++) {
                // go through through the row, rotating each element. Stop before the end of the row,
                // since it would be rotated
                int outerOffset = length - offset - 1;
                int temp = result[depth][offset];                              // store top left
                result[depth][offset] = result[outerOffset][depth];            // replace top left with bottom left
                result[outerOffset][depth] = result[outerDepth][outerOffset];  // replace bottom left with bottom right
                result[outerDepth][outerOffset] = result[offset][outerDepth];  // replace bottom right with top right
                result[offset][outerDepth] = temp;                             // replace top right with top left
            }
        }

        // assume that all rows are image.length in dimension
        return result;
    }
    public int[][] rotateMatrixParallel(int[][] image) {
        // note that we can parallelize the above to make it O(1) span
        // and we can do it in place, as long as we're not writing and reading to the same cell on multiple threads
        // 0 <= depth < length / 2
        // depth <= offset < length - depth - 1
        // 2 * sum_k=1 to length/2 k = (length/2)(length/2 + 1) total jobs, O(length^2) space requirement

        int length = image.length;
        int[][] result = ArrayUtil.copyMatrix(image);
        if(length <= 1)
            return result; // no sense rotating 0 or 1 pixel large images

        Tuple2<Integer, Integer>[][] jobs = new Tuple2[length/2][];
        Arrays.parallelSetAll(jobs, depth -> {
            Tuple2<Integer, Integer>[] row;
            if((length & 1) == 0) {
                row = new Tuple2[length - depth*2 - 1];
            } else {
                row = new Tuple2[2*(length/2 - depth)];
            }
            Arrays.parallelSetAll(row, idx -> Tuple2.make(depth, depth+idx));
            return row;
        });
        Arrays.stream(jobs).parallel()
                .forEach(depthRow -> {
                    Arrays.stream(depthRow).parallel().forEach( pixel -> {
                        int depth = pixel.getFirst();
                        int offset = pixel.getSecond();
                        int outerDepth = length - depth - 1;
                        int outerOffset = length - offset - 1;

                        int temp = result[depth][offset];                              // store top left
                        result[depth][offset] = result[outerOffset][depth];            // replace top left with bottom left
                        result[outerOffset][depth] = result[outerDepth][outerOffset];  // replace bottom left with bottom right
                        result[outerDepth][outerOffset] = result[offset][outerDepth];  // replace bottom right with top right
                        result[offset][outerDepth] = temp;                             // replace top right with top left
                    });
                });

        return result;
    }

    private final List<Tuple2<int[][], int[][]>> testCases = Tuple2.of2(
        new int[][] {{50}},
        new int[][] {{50}},
        new int[][] {
            {1, 2},
            {3, 4}
        },
        new int[][] {
            {3, 1},
            {4, 2}
        },
        new int[][] {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        },
        new int[][] {
            {7, 4, 1},
            {8, 5, 2},
            {9, 6, 3}
        },
        new int[][] {
            {0x1, 0x2, 0x3, 0x4},
            {0x5, 0x6, 0x7, 0x8},
            {0x9, 0xA, 0xB, 0xC},
            {0xD, 0xE, 0xF, 0x0},
        },
        new int[][] {
            {0xD, 0x9, 0x5, 0x1},
            {0xE, 0xA, 0x6, 0x2},
            {0xF, 0xB, 0x7, 0x3},
            {0x0, 0xC, 0x8, 0x4},
        },
        new int[][] {
            {10, 11, 12, 13, 14},
            {15, 16, 17, 18, 19},
            {20, 21, 22, 23, 24},
            {25, 26, 27, 28, 29},
            {30, 31, 32, 33, 34},
        },
        new int[][] {
            {30, 25, 20, 15, 10},
            {31, 26, 21, 16, 11},
            {32, 27, 22, 17, 12},
            {33, 28, 23, 18, 13},
            {34, 29, 24, 19, 14},
        }
    );

    private final List<Tuple2<String, Function<int[][], int[][]>>> testFunctions = List.of(
        Tuple2.make("rotateMatrixInPlace", this::rotateMatrixInPlace),
        Tuple2.make("rotateMatrixParallel", this::rotateMatrixParallel)
    );
    @Override
    public void test() throws TestException {
        Testable.check(testFunctions, testCases,  Arrays::deepEquals, RotateMatrix.class, "");
    }
}
