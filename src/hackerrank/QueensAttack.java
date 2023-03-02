package hackerrank;


import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.joining;

class ResultQueensAttack {

    /*
     * Complete the 'queensAttack' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     *  1. INTEGER n
     *  2. INTEGER k
     *  3. INTEGER r_q
     *  4. INTEGER c_q
     *  5. 2D_INTEGER_ARRAY obstacles
     */

    public static int queensAttack(int n, int k, int r_q, int c_q, List<List<Integer>> obstacles) {
        // c_q > x and y > r_q
        /**
         5 3
         4 3

         5 5
         4 2
         2 3
         *
         *      column
         *  1 2 3 4 5
         *  _ o o o X 5
         *  _ X Q o o 4
         *  _ o o o _ 3  row
         *  o _ X _ o 2
         *  _ _ _ _ _ 1
         */

        int minDistanceLeft = c_q - 1;
        int minDistanceRight = n - c_q;
        int minDistanceBottom = r_q - 1;
        int minDistanceTop = n - r_q;

        int minDistanceTopLeft = Math.min(minDistanceTop, minDistanceLeft);
        int minDistanceTopRight = Math.min(minDistanceTop, minDistanceRight);
        int minDistanceBottomLeft = Math.min(minDistanceBottom, minDistanceLeft);
        int minDistanceBottomRight = Math.min(minDistanceBottom, minDistanceRight);

        System.out.println("INITIAL");
        System.out.println("left " + minDistanceLeft);
        System.out.println("right " + minDistanceRight);
        System.out.println("top " + minDistanceTop);
        System.out.println("bottom " + minDistanceBottom);

        System.out.println("bottomleft " + minDistanceBottomLeft);
        System.out.println("bottomright " + minDistanceBottomRight);
        System.out.println("topleft " + minDistanceTopLeft);
        System.out.println("topright " + minDistanceTopRight);
        for(List<Integer> obstacle : obstacles) {
            int y = obstacle.get(0);
            int x = obstacle.get(1);
            if(y == r_q && x == c_q)
                // if queen's position is occupied, we just count it as the queen herself.
                continue;
            if(y == r_q && x < c_q && c_q - x - 1 < minDistanceLeft)
                minDistanceLeft = c_q - x - 1;
            if(y == r_q && x > c_q && x - c_q - 1 < minDistanceRight)
                minDistanceRight = x - c_q - 1;
            if(x == c_q && y < r_q && r_q - y -1 < minDistanceBottom)
                minDistanceBottom = r_q - y - 1;
            if(x == c_q && y > r_q && y - r_q -1 < minDistanceTop)
                minDistanceTop = y - r_q - 1;
            if(x - c_q == y - r_q) {
                int i = x - c_q;
                if(i > 0 && --i < minDistanceTopRight)
                    minDistanceTopRight = i;
                if(i < 0 && -(++i) < minDistanceBottomLeft)
                    minDistanceBottomLeft = -i;
            }
            if(c_q - x == y - r_q) {
                int i = c_q - x;
                if(i > 0 && --i < minDistanceTopLeft)
                    minDistanceTopLeft = i;
                if(i < 0 && -(++i) < minDistanceBottomRight)
                    minDistanceBottomRight = -i;
            }
        }
        System.out.println("AFTER");
        System.out.println("left " + minDistanceLeft);
        System.out.println("right " + minDistanceRight);
        System.out.println("top " + minDistanceTop);
        System.out.println("bottom " + minDistanceBottom);

        System.out.println("bottomleft " + minDistanceBottomLeft);
        System.out.println("bottomright " + minDistanceBottomRight);
        System.out.println("topleft " + minDistanceTopLeft);
        System.out.println("topright " + minDistanceTopRight);



        return minDistanceLeft + minDistanceRight + minDistanceTop + minDistanceBottom
                + minDistanceBottomLeft + minDistanceBottomRight + minDistanceTopLeft + minDistanceTopRight;
    }
}

public class QueensAttack {
    public static void main(String[] args) throws IOException {

        int result = ResultQueensAttack.queensAttack(100, 100, 48,81,
                List.of(
                        List.of(54, 87),List.of(
                        64, 97),List.of(
                        42, 75),List.of(
                        32, 65),List.of(
                        42, 87),List.of(
                        32, 97),List.of(
                        54, 75),List.of(
                        64, 65),List.of(
                        48, 87),List.of(
                        48, 75),List.of(
                        54, 81),List.of(
                        42, 81),List.of(
                        45, 17),List.of(
                        14, 24),List.of(
                        35, 15),List.of(
                        95, 64),List.of(
                        63, 87),List.of(
                        25, 72),List.of(
                        71, 38),List.of(
                        96, 97),List.of(
                        16, 30),List.of(
                        60, 34),List.of(
                        31, 67),List.of(
                        26, 82),List.of(
                        20, 93),List.of(
                        81, 38),List.of(
                        51, 94),List.of(
                        75, 41),List.of(
                        79, 84),List.of(
                        79, 65),List.of(
                        76, 80),List.of(
                        52, 87),List.of(
                        81, 54),List.of(
                        89, 52),List.of(
                        20, 31),List.of(
                        10, 41),List.of(
                        32, 73),List.of(
                        83, 98),List.of(
                        87, 61),List.of(
                        82, 52),List.of(
                        80, 64),List.of(
                        82, 46),List.of(
                        49, 21),List.of(
                        73, 86),List.of(
                        37, 70),List.of(
                        43, 12),List.of(
                        94, 28),List.of(
                        10, 93),List.of(
                        52, 25),List.of(
                        50, 61),List.of(
                        52, 68),List.of(
                        52, 23),List.of(
                        60, 91),List.of(
                        79, 17),List.of(
                        93, 82),List.of(
                        12, 18),List.of(
                        75, 64),List.of(
                        69, 69),List.of(
                        94, 74),List.of(
                        61, 61),List.of(
                        46, 57),List.of(
                        67, 45),List.of(
                        96, 64),List.of(
                        83, 89),List.of(
                        58, 87),List.of(
                        76, 53),List.of(
                        79, 21),List.of(
                        94, 70),List.of(
                        16, 10),List.of(
                        50, 82),List.of(
                        92, 20),List.of(
                        40, 51),List.of(
                        49, 28),List.of(
                        51, 82),List.of(
                        35, 16),List.of(
                        15, 86),List.of(
                        78, 89),List.of(
                        41, 98),List.of(
                        70, 46),List.of(
                        79, 79),List.of(
                        24, 40),List.of(
                        91, 13),List.of(
                        59, 73),List.of(
                        35, 32),List.of(
                        40, 31),List.of(
                        14, 31),List.of(
                        71, 35),List.of(
                        96, 18),List.of(
                        27, 39),List.of(
                        28, 38),List.of(
                        41, 36),List.of(
                        31, 63),List.of(
                        52, 48),List.of(
                        81, 25),List.of(
                        49, 90),List.of(
                        32, 65),List.of(
                        25, 45),List.of(
                        63, 94),List.of(
                        89, 50),List.of(
                        43, 41)
                ));
        System.out.println(result);
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));
//
//        String[] firstMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
//
//        int n = Integer.parseInt(firstMultipleInput[0]);
//
//        int k = Integer.parseInt(firstMultipleInput[1]);
//
//        String[] secondMultipleInput = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
//
//        int r_q = Integer.parseInt(secondMultipleInput[0]);
//
//        int c_q = Integer.parseInt(secondMultipleInput[1]);
//
//        List<List<Integer>> obstacles = new ArrayList<>();
//
//        IntStream.range(0, k).forEach(i -> {
//            try {
//                obstacles.add(
//                        Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
//                                .map(Integer::parseInt)
//                                .collect(toList())
//                );
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        });
//
//        int result = Result.queensAttack(n, k, r_q, c_q, obstacles);
//
//        bufferedWriter.write(String.valueOf(result));
//        bufferedWriter.newLine();
//
//        bufferedReader.close();
//        bufferedWriter.close();
    }
}
