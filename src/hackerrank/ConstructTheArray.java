package hackerrank;

import java.math.BigInteger;

public class ConstructTheArray {

    public static long countArray2(int n, int k, int x) {
        BigInteger modulo = BigInteger.valueOf(1000000000 + 7);
        // Return the number of ways to fill in the array.
        n -= 2; // remove first and last elements, focus on the middle.
        //BigInteger total = BigInteger.ZERO;
        BigInteger multiplier = BigInteger.valueOf(k - 1);
        //BigInteger multiple = (x == 1) ? multiplier : BigInteger.ONE;

        BigInteger negOne = BigInteger.valueOf(-1);
        BigInteger parity = (n & 1) == 0 ? BigInteger.ZERO : negOne;
        // k * (-1)^n * ((-1)^n * k^n - 1)

        BigInteger bigExp = multiplier.pow(k - 1);
        if(x == 1)
            return (multiplier.multiply(parity).multiply(
                    parity.multiply(bigExp).subtract(BigInteger.ONE)
            )).divide(multiplier.add(BigInteger.ONE)).mod(modulo).longValue();
        else
            return (bigExp.multiply(multiplier)).subtract(
                    (multiplier.multiply(parity).multiply(
                            parity.multiply(bigExp).subtract(BigInteger.ONE)
                    )).divide(multiplier.multiply(multiplier.add(BigInteger.ONE)))

            ).mod(modulo).longValue();
    }
    public static long countArray(int n, int k, int x) {
        BigInteger modulo = BigInteger.valueOf(1000000000 + 7);
        // Return the number of ways to fill in the array.
        n -= 2; // remove first and last elements, focus on the middle.
        BigInteger total = BigInteger.ZERO;
        BigInteger multiplier = BigInteger.valueOf(k - 1);
        BigInteger multiple = (x == 1) ? multiplier : BigInteger.ONE;

        for(int i = 0; i < n; i++) {
            int parity = 1 & (n ^ i);
            if(parity == 0) {
                total = total.subtract(multiple);
            } else {
                total = total.add(multiple);
            }
            multiple = multiple.multiply(multiplier);
            total = total.remainder(modulo);
            multiple = multiple.remainder(modulo);
        }
        if(x == 1)
            return total.mod(modulo).longValue();
        else
            return multiple.subtract(total).mod(modulo).longValue();
    }

    public static BigInteger countArrayBigInt(int n, int k, int x) {
        // Return the number of ways to fill in the array.
        n -= 2; // remove first and last elements, focus on the middle.
        BigInteger total = BigInteger.ZERO;
        BigInteger multiplier = BigInteger.valueOf(k - 1);
        BigInteger multiple = (x == 1) ? multiplier : BigInteger.ONE;

        for(int i = 0; i < n; i++) {
            int parity = 1 & (n ^ i);
            if(parity == 0) {
                total = total.subtract(multiple);
            } else {
                total = total.add(multiple);
            }
            multiple = multiple.multiply(multiplier);

        }
        if(x == 1)
            return total;
        else
            return multiple.subtract(total);
    }

    public static void main(String[] args) {
        // 364168328
        // 236568308
        System.out.println(countArray2(942, 77, 13));
    }
}
