package scratch;

import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class PickingNumbers2 {

    private static boolean inBounds(int val1, int val2) {
        return val2 >= val1 - 1 && val2 <= val1 + 1;
    }
    /*
     * Complete the 'pickingNumbers' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER_ARRAY a as parameter.
     */
    public static int pickingNumbers(List<Integer> a) {
        // Write your code here
        int currentMax = 0;
        Integer pos1 = null, pos2 = null;
        int val1 = 0, val2 = 0;

        for(int i = 0; i < a.size(); i++) {
            int current = a.get(i);
            if(pos1 == null) {
                pos1 = i;
                val1 = current;
            } else if(pos2 == null) {
                if(!inBounds(val1, current)) {
                    int currentRun = i - pos1;
                    if(currentRun > currentMax) {
                        currentMax = currentRun;
                    }

                    pos1 = i;
                    val1 = current;
                } else if(current != val1) {
                    val2 = current;
                    pos2 = i;
                }
            } else if(val1 != current && val2 != current) {
                // we will reset and continue our traversal.
                // set our current max.
                int currentRun = i - pos1;
                if(currentRun > currentMax) {
                    currentMax = currentRun;
                }

                // current is not in our current bound
                if(inBounds(val2, current)) {
                    // but current is in val2's bound
                    pos1 = pos2;
                    pos2 = i;
                    val1 = a.get(pos1);
                    val2 = a.get(pos2);
                } else {
                    pos1 = i;
                    val1 = a.get(pos1);
                    pos2 = null;
                }
            }
        }

        if(pos1 != null) {
            int currentRun = a.size() - pos1;
            if(currentRun > currentMax)
                currentMax = currentRun;
        }
        return currentMax;
    }

}

public class PickingNumbers {
    public static void main(String[] args) throws IOException {

        List<Integer> tCase = List.of(4,6,5,3,3,1);
        int result = PickingNumbers2.pickingNumbers(tCase);

        System.out.println(result);
    }
}

