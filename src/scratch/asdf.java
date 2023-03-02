package scratch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class asdf {
    public static int calc(List<Integer> result) {
        int total = 0;
        for(int i = 1; i < result.size(); i++) {
            total += Math.abs(result.get(i-1) - result.get(i));
        }
        return total;
    }
    public static int cost(List<Integer> B) {
        // Write your code here
        List<Integer> result1 = new ArrayList<>(B.size());
        List<Integer> result2 = new ArrayList<>(B.size());
        for(int i = 0; i < B.size(); i++) {
            if(i % 2 == 0) {
                result1.add(B.get(i));
                result2.add(1);
            } else {
                result1.add(1);
                result2.add(B.get(i));
            }
        }
        System.out.println(B);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(calc(result1));
        System.out.println(calc(result2));
        return Math.max(calc(result1), calc(result2));
//        int total = 0;
//        for(int i = 1; i < B.size(); i++) {
//            int zigSum = 0;
//            int zagSum = 0;
//            for(; i < B.size(); i++) {
//                if(i % 2 == 0) {
//                    zigSum += B.get(i) - 1;
//                    System.out.print(B.get(i) - 1 + " ");
//                    zagSum += B.get(i - 1) - 1;
//                } else {
//                    zigSum += B.get(i - 1) - 1;
//                    System.out.print(B.get(i - 1) - 1 + " ");
//                    zagSum += B.get(i) - 1;
//                }
//                if(B.get(i) == 1) {
//                    break;
//                }
//            }
//            System.out.println("\n" + zigSum + " " + zagSum);
//            int localMax = Math.max(zigSum, zagSum);
//            total += localMax;
//        }
//        System.out.println();

//        return total;
    }

//    [14, 30, 82, 49, 47, 96, 34, 66, 15, 11, 43, 45, 56, 77, 53, 13, 43, 92, 67, 37]
//    [14,  1, 82,  1, 47,  1, 34,  1, 15,  1, 43,  1, 56,  1, 53,  1, 43,  1, 67,  1]
//    [ 1, 30,  1, 49,  1, 96,  1, 66,  1, 11,  1, 45,  1, 77,  1, 13,  1, 92,  1, 37]
    public static void main(String[] args) {
        System.out.println(cost(
                Arrays.stream("14 30 82 49 47 96 34 66 15 11 43 45 56 77 53 13 43 92 67 37"
                        .split(" ")).map(x -> Integer.parseInt(x))
                    .collect(Collectors.toList())

        ));
    }
}
