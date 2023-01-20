package scratch;

import common.tuple.Tuple2;

import java.util.Arrays;

public class MatrixScratch {
    public static void main(String[] args) {
//        // length = 2, total = 1
//        // 0: 0..0  (1)
//        System.out.println(1);
//        // length = 3, total = 2
//        // 0: 0..1  (2)
//        System.out.println(2);
//        // length = 4, total = 4
//        // 0: 0..2  (3)
//        // 1: 1..1  (1)
//        System.out.println(3+1);
//        // length = 5, total = 6
//        // 0: 0..3  (4)
//        // 1: 1..2  (2)
//        System.out.println(4+2);
//        // length = 6, total = 9
//        // 0: 0..4  (5)
//        // 1: 1..3  (3)
//        // 2: 2..2  (1)
//        System.out.println(5+3+1);
//        // length = 7, total = 12
//        // 0: 0..5  (6)
//        // 1: 1..4  (4)
//        // 2: 2..3  (2)
//        System.out.println(6+4+2);
//        // length = 8, total = 16
//        // 0: 0..6  (7)
//        // 1: 1..5  (5)
//        // 2: 2..4  (3)
//        // 3: 3..3  (1)
//        System.out.println(7+5+3+1);
//        // length = 9, total = 20
//        // 0: 0..7  (8)
//        // 1: 1..6  (6)
//        // 2: 2..5  (4)
//        // 3: 3..4  (2)
//        System.out.println(8+6+4+2);
//        // length = 10, total = 25
//        // 0: 0..8  (9)
//        // 1: 1..7  (7)
//        // 2: 2..6  (5)
//        // 3: 3..5  (3)
//        // 4: 4..4  (1)
//        System.out.println(9+7+5+3+1);
//        // length = 11, total = 30
//        // 0: 0..9  (10)
//        // 1: 1..8  (8)
//        // 2: 2..7  (6)
//        // 3: 3..6  (4)
//        // 4: 4..5  (2)
//        System.out.println(10+8+6+4+2);


        int[] testCases = {
            0,
            0,
            1,
            2,
            4,
            6,
            9,
            12,
            16,
            20,
            25,
            30,
        };
        //for(int length = 0; length < testCases.length; length++) {
        //    check(length, testCases[length]);
        //}
        //check(5, testCases[5]);
        check(11, testCases[11]);
    }

    public static void check(int length, int expected) {
        int value = (length/2) * ((length - 1)/2 + 1);
        System.out.println(length + " -> " + value + "(" + expected + ")");

        if(length <= 1) return;
        int numberOfJobs = (length/2) * ((length - 1)/2 + 1);
        Tuple2<Integer, Integer>[][] jobs = new Tuple2[length/2][];
        Arrays.parallelSetAll(jobs, depth -> {
            Tuple2<Integer, Integer>[] row;
            if((length & 1) == 0) {
                row = new Tuple2[length - depth *2 - 1];
            } else {
                row = new Tuple2[2*(length/2 - depth)];
            }
            Arrays.parallelSetAll(row, idx -> Tuple2.make(depth, depth+idx));
            return row;
        });
        for(Tuple2<Integer, Integer>[] row : jobs) {
            for(Tuple2<Integer, Integer> item : row) {
                System.out.print(item + " ");
            }
            System.out.println();
        }

        // 1 2 3 4 5
        // 6 7 8 9 0
        // 0 9 8 7 6
        // 5 4 3 2 1
        // 1 2 3 4 5
        // X X X X
        //   X X


//        Arrays.parallelSetAll(jobs, idx -> {
//            if((length & 1) == 1) {
//
//            }
//            int previousJobsDone = (int)Math.floor((Math.sqrt(16*idx + 1) - 1 )/2.0);
//            previousJobsDone = (previousJobsDone/2) * ((previousJobsDone - 1)/2 + 1);
//            // length = 10, total = 25
//            // 0: 0..8  (9)
//            // 1: 1..7  (7)
//            // 2: 2..6  (5)
//            // 3: 3..5  (3)
//            // 4: 4..4  (1)
//
//            // length = 11, total = 30
//            // 0: 0..9  (10)
//            // 1: 1..8  (8)
//            // 2: 2..7  (6)
//            // 3: 3..6  (4)
//            // 4: 4..5  (2)
//            return new Tuple2<>(previousJobsDone,0);
//        });
//        for(Tuple2<Integer, Integer> item : jobs ){
//            System.out.println(item);
//        }
    }
}
