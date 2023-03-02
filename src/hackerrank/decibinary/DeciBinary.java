package hackerrank.decibinary;

import common.tuple.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wizard-level solution to convert an index to its DeciBinary representation.
 * Decibinary is a combination of decimal and binary numbers that follows the pattern
 *     [0-9]2^0 + [0-9]2^1 + ... + [0-9]2^n
 * This solution is in O(log n), which is better than any other solution I've found so far.
 * It can calculate the result for 10^7 in 1.25 milliseconds, and 10^18 in about 92 millis.
 * It also uses an astoundingly small amount of space in its memoization, 10^7 only uses 277 items.
 *
 * HackerRank Link:
 *     https://www.hackerrank.com/challenges/decibinary-numbers/problem
 * StackExchange Link (my writeup on this solution):
 *     https://math.stackexchange.com/questions/3540243/whats-the-number-of-decibinary-numbers-that-evaluate-to-given-decimal-number/4650820#4650820
 */
public class DeciBinary {
    private static List<Long> memo = new ArrayList<>();
    private static List<Long> cumulative = new ArrayList<>();
    private static Map<Integer, Long> pow5 = new HashMap<>();

    /**
     * Converts an index to its decibinary representation.
     * Super fast!
     */
    public static long getDecibinaryFromIndex(long decibinaryIndex) {
        // find the decimal representation of our index

        // binary search in cumulative to find the decimal and the currentSize
        int decimal = 0;
        long currentSize = 0;
        foundMatch : {
            int hi = cumulative.size() - 1;
            if(hi < 0 || decibinaryIndex > cumulative.get(hi)) {
                // we're out of bounds for our memo. Traverse from the end and add elements to the cumulative memo
                if(hi < 0) {
                    decimal = 0;
                    currentSize = 0;
                } else {
                    decimal = hi;
                    currentSize = cumulative.get(hi);
                }

                while(true) {
                    if(decimal == cumulative.size())
                        cumulative.add(currentSize);
                    long count = decibinaryCount(decimal / 2);
                    if(currentSize + count > decibinaryIndex) {
                        break;
                    }
                    currentSize += count;
                    decimal++;
                }

                break foundMatch;
            }
            // otherwise our result is in the bounds of cumulative. We binary search to find it.
            int lo = 0;
            int mid;
            while(lo <= hi) {
                mid = lo + (hi - lo) / 2;
                long currentDecibinary = cumulative.get(mid);
                if(currentDecibinary < decibinaryIndex) {
                    lo = mid + 1;
                } else if(currentDecibinary > decibinaryIndex) {
                    hi = mid - 1;
                } else {
                    decimal = mid;
                    currentSize = currentDecibinary;
                    break foundMatch;
                }
            }
            if(hi >= 0) {
                decimal = hi;
                currentSize = cumulative.get(hi);
            }
        }

        return traverseDecibinary(decibinaryIndex, decimal, decibinaryMaxCombinationIdx(decimal), currentSize);
    }

    /**
     * Recursion to calculate our decibinary representation
     * Moves from currentIndex to targetIndex, jumping forward for each digit we're adding on to the final representation
     * The indices help us pick what representation we will have at the end.
     * @param targetIndex target index we will end up at
     * @param decimal the decimal we're currently calculating
     * @param k the maximum decibinary size representation of decimal, gets smaller during recursion
     * @param currentIndex our current index
     * @return the decibinary representation
     */
    private static long traverseDecibinary(long targetIndex, long decimal, int k, long currentIndex) {
        if(k == 0) {
            long difference = Math.abs(targetIndex - currentIndex);
            if(difference >= 10)
                throw new IllegalStateException("Base case: k == 0 but difference > 9, targetIndex " + targetIndex + " != currentIndex = " + currentIndex + "; decimal = " + decimal);
            return decimal;
        }
        if(decimal == 0) {
            return 0;
        }

        // traverse to find the length of the decibinary combination we will be utilizing.
        // now, find the minimum and maximum first digit in decibinary.
        // Traverse through the digits: from a = minimum till a = maximum
        //    Remove that digit, subtracting 2^b from decimal where b is the position of the first digit
        //    Then recurse for (decimal - 2^b)
        int minK = decibinaryMinCombinationIdx((int)decimal);
        int maxK = Math.min(decibinaryMaxCombinationIdx((int)decimal), k); // k is our upper bound during our recursion
        int index = minK;
        while(index <= maxK) {
            long indexCount = decibinaryCountLengthCombinations(decimal, index);
            if(indexCount + currentIndex > targetIndex) {
                break;
            }
            currentIndex += indexCount;
            index++;
        }
        k = index;

        // we have both the decimal representation and the length of the decibinary string
        // recurse and add to total

        long divisor = 1L << k;
        long newDecimal = decimal - divisor;
        long result =  traverseDecibinary(targetIndex, newDecimal, k, currentIndex);
        return result + pow10(k);
    }

    /**
     * returns the index for the minimum combination size that we can have for a given decimal
     * for example, the decimal 22 can be represented by at least 2 digits
     *      78 decibinary = 22 decimal
     * since we are zero-indexing, we return 1.
     */
    private static int decibinaryMinCombinationIdx(int decimal) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(((decimal-1) / 9) + 1) - 1;
    }

    /**
     * returns the index for the maximum combination size that we can have for a given decimal
     * for example, the decimal 22 can be represented by at most 5 digits
     *    10006 decibinary = 22 decimal
     * since we are zero-indexing, we return 4.
     */
    private static int decibinaryMaxCombinationIdx(int decimal) {
        // 0 = 0*2^0
        if(decimal == 0)
            return 0;
        return Integer.SIZE - Integer.numberOfLeadingZeros(decimal) - 1;
    }

    static long pow10(int k) {
        // 10^k == 5^k*2^k
        return pow5(k) * (1L<<k);
    }
    static long pow5(int k) {
        return pow(k, 5, pow5);
    }

    /**
     * memoized O(log(k)) solution to produce an integer exponent
     */
    static long pow(int k, int exp, Map<Integer, Long> memo) {
        if(memo.containsKey(k))
            return memo.get(k);
        else if(k == 0) {
            memo.put(0, 1L);
            return 1L;
        } else {
            int recurse = k / 2;
            boolean odd = (k & 1) == 1;
            long sqrt = pow(recurse, exp, memo);
            long result = sqrt * sqrt;
            if(odd)
                result *= exp;
            memo.put(k, result);
            return result;
        }
    }

    /**
     * Counts the number of decibinary combinations there are for decimal with length (k+1)
     * This is O(1)
     */
    private static long decibinaryCountLengthCombinations(long decimal, int k) {
        // edge case : 1 combination for zero at index 0
        if(decimal == 0 && k == 0)
            return 1;

        // counts the number of decibinary combinations of length k + 1 can make decimal
        long pow2 = 1L << k;
        long pow2Next = pow2 << 1;
        long endCapLength = (8L << k) - 8L;
        long ascendingPos = pow2 + endCapLength;
        long descendingPos;
        if(pow2 <= decimal && decimal < ascendingPos) {
            // ascending digits
            long sub1Pos = 4 * (pow2 - 1);
            if(decimal < pow2 + pow2Next) {
                // from pow2 to pow2Next, should be the same as memo
                return memo.get((int)(decimal - pow2)>>>1);
            } else if(decimal < sub1Pos + pow2Next) {
                // subtract memo positions from memo
                long result = memo.get((int)(decimal - pow2)>>>1)
                        - memo.get((int)((decimal - pow2 - pow2Next)>>>1));
                // we may have to subtract twice. We never have to do more than this
                long sub2Pos = pow2 + 2*pow2Next;
                if(sub2Pos <= decimal) {
                    result -= memo.get((int)(decimal - sub2Pos)>>>1);
                }
                return result;
            } else {
                // subtract memo positions in reverse order from max value, 5^k
                long result = pow5(k) - memo.get((int)(ascendingPos - 1 - decimal)>>>1);

                long sub2Pos = ascendingPos - pow2Next;
                if(decimal < sub2Pos) {
                    result += memo.get((int)(sub2Pos - decimal - 1)>>>1);
                }
                return result;
            }
        } else if(decimal < (descendingPos = 10 * pow2) && ascendingPos <= decimal) {
            // max combinations at 5^k
            return pow5(k);
        } else if(descendingPos <= decimal && decimal <= 9 * (pow2Next - 1)) {
            // descending digits. this is mirrored from ascending, so we can just use that.
            long distance = decimal - descendingPos + 1;
            return decibinaryCountLengthCombinations(ascendingPos - distance, k);
        }
        // no combinations outside of this range
        return 0L;
    }

    /**
     * Counts the number of decibinary combinations there are for a given digit.
     * For any even digit d, decibinaryCount(d) == decibinaryCount(d + 1)
     * Thus, we only need to store even digits in our memo (and we do so.)
     */
    private static long decibinaryCount(int index) {
        // 0 ways to make negative numbers
        if(index < 0)
            return 0;
        // odds and evens are the same
        if(index < memo.size())
            return memo.get(index);
        else if(index == 0){
            // base case: 0th index is 1
            memo.add(1L);
            return 1;
        } else {
            //f(n) = f(n-1) + f(floor(n/2)) - f(floor((n-5)/2))
            //f(0) = 1
            //f(k < 0) = 0
            long result = -decibinaryCount(Math.floorDiv(index - 5, 2)) +
                    decibinaryCount(index / 2) +
                    decibinaryCount(index - 1);
            memo.add(result);
            return result;
        }
    }

    public static void main(String[] args) {
        // this is incredibly fast, and doesn't use a lot of space!
        long time = System.currentTimeMillis();
        System.out.println(getDecibinaryFromIndex(1000000000000000000L));
        System.out.println(System.currentTimeMillis() - time);
        System.out.println(memo.size());

        // randomized test against dumb method
        List<Long> table = decibinaryArray(10000000);
        for(int i = 0; i < 10000; i++) {
            table.add(getDecibinaryFromIndex(i));
        }
        Random r = new Random();
        for(int i = 0; i < 100000; i++) {
            int pos = r.nextInt(10000);
            long expected = table.get(pos);
            long actual = getDecibinaryFromIndex(pos);
            if(expected != actual) {
                throw new RuntimeException(i + " " + expected + " " + actual);
            }
        }
    }

    // converts decimal to the number of decibinary numbers at that decimal
    static long decibinaryTotal(int decimal) {
        List<Long> memo = new ArrayList<>();

        // avoid stack overflow errors
        int chunkSize = 1024;
        int current = 0;
        while(current + chunkSize < decimal) {
            current += chunkSize;
            decibinaryCount(current);

        }
        // get the rest
        decibinaryCount(decimal);

        long result = 0;
        for(int i = 0; i < memo.size(); i++) {
            result += memo.get(i);
        }
        return result;
    }


    /////
    // slow method, for testing purposes
    /////
    public static long decibinaryNumbers(long x) {
        for(int digit = 0; digit <= x; digit++) {
            if(digit == 0) {
                System.out.println(digit + " " + 0);
                continue;
            }
            long pow2 = 1;
            long pow10 = 1;
            while(pow2 <= digit) {
                // remove from modify and add to result
                long modify = digit;
                long result = 0;

                long powDigit10 = pow10;
                long powDigit2 = pow2;
                while(modify > 0) {
                    long diff = modify - powDigit2;
                    if(diff < 0) {
                        powDigit2 = powDigit2 >>> 1;
                        powDigit10 = powDigit10 / 10;
                        continue;
                    }
                    modify = diff;
                    result += powDigit10;
                }
                System.out.println(digit + " " + result);

                pow2 = pow2 << 1;
                pow10 = pow10 * 10;
            }
        }
        return 0;
    }

    static void add(List<Integer> l, int val) {
        while(l.size() <= val) {
            l.add(0);
        }
        l.set(val, l.get(val) + 1);
    }
    static int log10(long i) {
        int result = 0;
        while(i > 0) {
            i = i / 10;
            result++;
        }
        return result;
    }


    public static List<Long> decibinaryArray(long count) {
        List<Tuple2<Long, Long>> order = new ArrayList<>();
        for(long i = 0; i <= count; i++) {
            long decibinary = i;
            long decimal = 0;
            long exp = 1;
            while(decibinary > 0) {
                long digit = decibinary % 10;
                decimal += digit * exp;
                decibinary /= 10;
                exp *= 2;
            }

            order.add(Tuple2.make(decimal, i));
        }
        order.sort((l, r) -> {
            int decimal = Long.compare(l.getFirst(), r.getFirst());
            if(decimal == 0)
                return Long.compare(l.getSecond(), r.getSecond());
            return decimal;
        });
        return order.stream().filter(x -> x.getFirst() <= 64).map(x -> x.getSecond()).collect(Collectors.toList());
    }

}
