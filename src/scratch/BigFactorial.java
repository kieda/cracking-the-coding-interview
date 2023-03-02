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

class Result {

    // primes up to 100
    // todo - we should expand for n! for n > 100 by using a seive
    private static final int[] ZERO = {0, 1};
    private static final int[] ONE = {1, 1};
    private static int[] primes = new int[]{
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97
    };
    // list starts at 2, s.t. factorizations[0] = [2, 1]
    private static List<int[]> factorizations = new ArrayList<>();
    public static int binarySearch(int val, int[] arr) {
        return binarySearch(val, arr, false);
    }
    public static int binarySearch(int val, int[] arr, boolean getClosest) {
        int hi = arr.length - 1;
        int lo = 0;
        while(lo <= hi) {
            int mid = lo + (hi - lo)/2;
            if(val < arr[mid]) {
                hi = mid - 1;
            } else if(val > arr[mid]) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        if(getClosest)
            return lo;
        else return -1;
    }
    public static int[] getFactorization(int n) {
        if(n <= 1) return ONE;
        if(n - 2 < factorizations.size())
            return factorizations.get(n - 2);
        findFactor:
        for(int i = factorizations.size() + 2; i <= n; i++) {
            int primeIdx = binarySearch(i, primes);
            int lowestPrime = -1;
            int divisor = -1;

            if(primeIdx >= 0) {
                lowestPrime = primes[primeIdx];
                divisor = 1;
            } else {
                lowestPrime = -1;
                divisor = -1;
                for(primeIdx = 0; primeIdx < primes.length; primeIdx++) {
                    if(i % primes[primeIdx] == 0) {
                        lowestPrime = primes[primeIdx];
                        divisor = i / lowestPrime;
                        break;
                    }
                }
            }
            int[] factorization;
            int divisorIdx;
            if(divisor == 1) {
                factorization = new int[]{primeIdx, 1};
            } else if(divisor <= 3) {
                // 2 -> 0
                // 3 -> 1
                factorization = new int[]{primeIdx, 1, divisor - 2, 1};
            } else if((divisorIdx = binarySearch(divisor, primes)) >= 0) {
                // if the divisor is also a prime, we know this factorization is a multiple of two primes
                factorization = new int[]{primeIdx, 1, divisorIdx, 1};
            } else {
                int[] divisorFactors = getFactorization(divisor);
                for(int j = 0; j < divisorFactors.length; j+=2) {
                    if(lowestPrime == primes[divisorFactors[j]]) {
                        factorization = Arrays.copyOf(divisorFactors, divisorFactors.length);
                        factorization[j+1]++;
                        factorizations.add(factorization);
                        continue findFactor;
                    }
                }
                factorization = Arrays.copyOf(divisorFactors, divisorFactors.length + 2);
                factorization[divisorFactors.length] = primeIdx;
                factorization[divisorFactors.length + 1] = 1;
            }
            factorizations.add( factorization);
        }
        return factorizations.get(n - 2);
    }


    /*
     * Complete the 'extraLongFactorials' function below.
     *
     * The function accepts INTEGER n as parameter.
     */
    public static void extraLongFactorials(int n) {
        // Write your code here
        getFactorization(n);

        int[] allFactors = new int[binarySearch(n, primes, true) + 1];
        for(int i = 2; i <= n; i++) {
            int[] factors = factorizations.get(i - 2);
            for(int j = 0; j < factors.length; j+=2) {
                int primeIdx = factors[j];
                int mult = factors[j+1];
                allFactors[primeIdx] += mult;
            }
        }

        System.out.println(Arrays.toString(allFactors));
        BigInteger factorial = BigInteger.ONE;
        for(int i = 0; i < allFactors.length; i++) {
            int exp = allFactors[i];
            if(exp > 0) {
                factorial = bigMult(factorial, BigInteger.valueOf(primes[i]), exp);
            }
        }

        System.out.println(factorial);
        BigInteger factorial2 = BigInteger.ONE;
        for(int i = 2; i <= n; i++) {
            factorial2 = factorial2.multiply(BigInteger.valueOf(i));
        }
        System.out.println(factorial2);
    }

    // returns initial * base^exp
    public static BigInteger bigMult(BigInteger initial, BigInteger base, int exp) {
        if(exp == 1)
            return initial.multiply(base);
        BigInteger result = initial;
        BigInteger carry = base;
        while(exp != 0) {
            if((exp & 1) == 1) {
                result = result.multiply(carry);
            }
            carry = carry.multiply(carry);
            exp = exp >>> 1;
        }
        return result;
    }

}

public class BigFactorial {
    public static void main(String[] args) throws IOException {
        /*BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(bufferedReader.readLine().trim());
*/
        Result.extraLongFactorials(25);

//        bufferedReader.close();
    }
}
