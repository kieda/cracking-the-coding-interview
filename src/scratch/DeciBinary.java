package scratch;

import common.tuple.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeciBinary {
    public static long decibinaryNumbers(long x) {
        for(int digit = 0; digit <= x; digit++) {
//            if(digit == 3) {
//                System.out.println("start");
//            }
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


    public static void decibinaryArray(long count) {
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
//        order.stream().filter(x -> x.getFirst() <= 32).forEach(System.out::println);
        Map<Long, List<Integer>> counts = new HashMap<>();
        for(int i = 1; i < order.size() && order.get(i).getFirst() <= 32//200
                ; i++) {
            long decimal = order.get(i).getFirst();
            int log10 = log10(order.get(i).getSecond()) -1;
            counts.computeIfAbsent(decimal, k -> new ArrayList<>());
            add(counts.get(decimal), log10);
        }
        System.out.println();
        counts.forEach((dec, c) -> {
//            if(dec % 2 == 0 && c.size() >= 5)
                System.out.printf("%2d : %s\n", dec, c//c.get(4)
                )
                        ;
        }
                );
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

    // todo - memo the cumulative size. If we already memo'd the item we can binary search
    static long decibinaryIndex(long decibinaryIndex) {
        // find the decimal representation of our index
        List<Long> memo = new ArrayList<>();
        int decimal = 0;
        long currentSize = 0;
        long prevSize;
        while(true) {
            prevSize = currentSize;
            long count = decibinaryCount(decimal / 2, memo);
            currentSize += count;
            if(currentSize > decibinaryIndex) {
                break;
            }
            decimal++;
        }

        // our decibinary number has a decimal representation of decimal
        // all numbers equal to decimal are in the range [prevSize, currentSize)
//        int minK = decibinaryMinCombinationIdx(decimal);
//        int maxK = decibinaryMaxCombinationIdx(decimal);
//        int index = minK;
//        currentSize = prevSize;
//        while(index <= maxK) {
//            prevSize = currentSize;
//            long indexCount = decibinaryCountLengthCombinations(decimal, index, memo);
//            currentSize += indexCount;
//            if(currentSize > decibinaryIndex) {
//                break;
//            }
//            index++;
//        }


        // we have both the decimal representation and the length of the decibinary string

//        System.out.println(decibinaryIndex + " " + decimal + " " + prevSize + " " + currentSize + " " + index);
//        System.out.println(minK + " " + maxK);
//        System.out.println(currentSize - prevSize);

        return traverseDecibinary(decibinaryIndex, decimal, decibinaryMaxCombinationIdx(decimal), prevSize, memo);
    }
    static long traverseDecibinary(long targetIndex, long decimal, int k, long currentIndex, List<Long> memo) {
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
        int minK = decibinaryMinCombinationIdx((int)decimal);
        int maxK = Math.min(decibinaryMaxCombinationIdx((int)decimal), k); // k is our upper bound during our recursion
        int index = minK;
        while(index <= maxK) {
            long indexCount = decibinaryCountLengthCombinations(decimal, index, memo);
            if(indexCount + currentIndex > targetIndex) {
                break;
            }
            currentIndex += indexCount;
            index++;
        }
        k = index;
        // now, find the minimum and maximum first digit in decibinary.
        // Traverse through the digits: from a = minimum till a = maximum
        //    Remove that digit, subtracting a * 2^b from decimal where a is the base and b is the position of the first digit
        //    Find the number of length combinations at (b-1) for (decimal - a * 2^b)
        //       if the number of length combinations exceeds our current total, recurse.
        //       otherwise, continue traversing with a = a + 1 and add to our running total.

        long divisor = 1L << k;
//        long max = decimal / divisor;
//        if(max > 9)
//            max = 9;
//        // x - j * divisor = 9*(2^k - 1)
//        // j * divisor = x - 9*(2^k - 1)
//        // j = (x - 9*(2^k - 1))/divisor
//        long min = -Math.floorDiv(-(decimal - 9 * (divisor - 1)), divisor); // this magic takes the ceiling of ints
//        if(min <= 0)
//            min = 1;
        long newDecimal = decimal - divisor;
        long result =  traverseDecibinary(targetIndex, newDecimal, k, currentIndex, memo);
        return result + pow10(k);
        // min is in range 1 .. 9
//        for(int i = (int)min; min <= max; i++) {
//            long newDecimal = decimal - i * divisor;
//            // combinations @ k - 1 for newDecimal === combinations for elements that start with i @ k
//            long combinations = decibinaryCountLengthCombinations(newDecimal, k - 1, memo);
//            if(currentIndex + combinations >= targetIndex) {
//                // we found a match!
//                // todo - perform logic
//                // todo - accumulate result.
//                long result =  traverseDecibinary(targetIndex, newDecimal, k - 1, currentIndex, memo);
//                return result + 10 * i; // accumulate the result
//            } else {
//                currentIndex += combinations;
//            }
//        }
        //return -1;
    }

    /**
     * returns the index for the minimum combination size that we can have for a given decimal
     * for example, the decimal 22 can be represented by at least 2 digits
     *      78 decibinary = 22 decimal
     * since we are zero-indexing, we return 1.
     */
    static int decibinaryMinCombinationIdx(int decimal) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(((decimal-1) / 9) + 1) - 1;
    }

    /**
     * returns the index for the maximum combination size that we can have for a given decimal
     * for example, the decimal 22 can be represented by at most 5 digits
     *    10006 decibinary = 22 decimal
     * since we are zero-indexing, we return 4.
     */
    static int decibinaryMaxCombinationIdx(int decimal) {
        // 0 = 0*2^0
        if(decimal == 0)
            return 0;
        return Integer.SIZE - Integer.numberOfLeadingZeros(decimal) - 1;
    }

    static long pow10(int k) {
        long result = 1;
        while(k > 0) {
            result *= 10;
            k--;
        }
        return result;
    }
    static long pow5(int k) {
        long result = 1;
        while(k > 0) {
            result *= 5;
            k--;
        }
        return result;
    }

    static long decibinaryCountLengthCombinations(long decimal, int k, List<Long> memo) {
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
            return decibinaryCountLengthCombinations(ascendingPos - distance, k, memo);
        }
        // no combinations outside of this range
        return 0L;
    }


    static long decibinaryCount(int index, List<Long> memo) {
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
            long result = -decibinaryCount(Math.floorDiv(index - 5, 2), memo) +
                    decibinaryCount(index / 2, memo) +
                    decibinaryCount(index - 1, memo);
            memo.add(result);
            return result;
        }

    }

    public static void main(String[] args) {
        // 16
        //decibinaryArray(100000);
        //decibinaryArray(20000000);
        // 64
       // List<Long> memo = new ArrayList<>();
        //decibinaryCount(1000, memo);
        //System.out.println(memo);
//        decibinaryIndex(0);
//        decibinaryIndex(5);
//        decibinaryIndex(7);
//        decibinaryIndex(10);
//        decibinaryIndex(11);
        /*long count = decibinaryCount(1000000);
        System.out.println(count);
        System.out.println(count <= 10e16); */
//        System.out.println((int)((2L - 7L + 4L)/2));
//        decibinaryIndex(100000000000000000L);
        for(int i = 0; i <= 1000; i++)
            System.out.println("idx = " + i + " " + decibinaryIndex(i));

        //for(int i = 0; i <= 100;i++) {
            // 9 * (2^(index + 1) - 1) = decimal
            // (decimal / 9) + 1 = 2^(index + 1)
//            while(decimal <= 9 * ((1<<(index + 1)) - 1)) {

            /*int min = Integer.SIZE - Integer.numberOfLeadingZeros(((i-1) / 9) + 1) - 1;
            System.out.println(i + " " + min); */
        //}

    }

    // converts decimal to the number of decibinary numbers at that decimal
    static long decibinaryCount(int decimal) {
        List<Long> memo = new ArrayList<>();

        // avoid stack overflow errors
        int chunkSize = 1024;
        int current = 0;
        while(current + chunkSize < decimal) {
            current += chunkSize;
            decibinaryCount(current, memo);

        }
        // get the rest
        decibinaryCount(decimal, memo);

        long result = 0;
        for(int i = 0; i < memo.size(); i++) {
            result += memo.get(i);
        }
        return result;
    }
/*
110 : [0, 0, 0, 54, 561, 688, 230]
112 : [0, 0, 0, 46, 573, 735, 258]
114 : [0, 0, 0, 41, 580, 766, 277]
116 : [0, 0, 0, 34, 589, 809, 304]
118 : [0, 0, 0, 29, 595, 840, 324]
120 : [0, 0, 0, 22, 603, 888, 356]
122 : [0, 0, 0, 18, 607, 917, 376]
124 : [0, 0, 0, 13, 612, 958, 405]
126 : [0, 0, 0, 10, 615, 987, 426]
128 : [0, 0, 0, 6, 619, 1036, 464, 1]
 */

}
