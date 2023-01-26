package scratch;

public class BitTest {
    public static void testPlusOne(int i) {
        int plusOne = i + 1;
        int xor = i ^ plusOne;
        boolean foundZero = false;
        int numTailBits = 0;
        for(int bit = 0; bit < 32; bit++) {
            if(((xor >>> bit) & 1) == 1) {
                if(foundZero)
                    throw new RuntimeException("Invalid plusone: " + i + " and " + plusOne);
                else
                    numTailBits++;
            } else {
                foundZero = true;
            }
        }
        //Integer.numberOfTrailingZeros()
        //System.out.println("One: " + Integer.toBinaryString(i) + " " + Integer.toBinaryString(plusOne) + " " + Integer.toBinaryString(xor) + " " + numTailBits);
    }
    public static void testPlusTwo(int i) {
        int plusTwo = i + 2;
        int xor = i ^ plusTwo;
        boolean foundZero = false;

        // should always be zero for plus two
        if((xor & 1) != 0) {
            throw new RuntimeException("Plustwo xor needs last digit 0: " + i + " and " + plusTwo);
        }
        int numTailBits = 0;
        for(int bit = 1; bit < 32; bit++) {
            if(((xor >>> bit) & 1) == 1) {
                if(foundZero)
                    throw new RuntimeException("Invalid plustwo: " + i + " and " + plusTwo);
                else
                    numTailBits++;
            } else {
                foundZero = true;
            }
        }
        //System.out.println("Two: " + Integer.toBinaryString(i) + " " + Integer.toBinaryString(plusTwo) + " " + Integer.toBinaryString(xor) + " " + numTailBits);
    }


    public static void main(String[] args) {
        for(int test = 1; test < Integer.MAX_VALUE - 2; test++) {
            testPlusOne(test);
            testPlusTwo(test);
        }
    }
}
