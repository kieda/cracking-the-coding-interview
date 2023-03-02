package hackerrank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountCoinWays {
    private static long waysRecursive(long n, List<Long> c, long max, Map<Long, Long> memo) {
        if(n == 0)
            return 1;
        if(memo.containsKey(n)) {
            return memo.get(n);
        }
        long total = 0;
        for(int i = 0; i < c.size(); i++) {
            long change = c.get(i);
            if(change <= max) {
                long diff = n - change;
                if(diff > 0)
                    total += waysRecursive(diff, c, change, memo);
                else if(diff == 0)
                    total++;
            }
        }
        memo.put(n, total);
        return total;
    }
    public static long getWays(int n, List<Long> c) {
        long max = c.stream().max(Long::compare).orElse(0L);
        // Write your code here
        Map<Long, Long> memo = new HashMap<>();
        long ways =  waysRecursive(n, c, max, memo);
        System.out.println(memo);
        return ways;

    }
    public static void main(String[] args) {
        System.out.println(getWays(4, List.of(1L,2L,3L)));
    }
}
