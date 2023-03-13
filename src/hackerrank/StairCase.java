package hackerrank;

import java.nio.CharBuffer;
import java.util.Objects;

public class StairCase {
    static class RepeatChar implements CharSequence {
        private char character;
        private int length;

        public void setCharacter(char character) {
            this.character = character;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public RepeatChar(char c, int length) {
            this.character = c;
            this.length = length;
        }

        public CharSequence subSequence(int start, int end) {
            Objects.checkFromToIndex(start, end, length);
            return new RepeatChar(character, end - start);
        }

        public int length() {
            return length;
        }

        public char charAt(int i) {
            if (i < 0 || i >= length)
                throw new IndexOutOfBoundsException("index " + i + " is out of bounds for length " + length);
            return character;
        }
    }

    /*
     * Complete the 'staircase' function below.
     *
     * The function accepts INTEGER n as parameter.
     */

    public static void staircase(int n) {
        RepeatChar spaces = new RepeatChar(' ', 0);
        RepeatChar pounds = new RepeatChar('#', 0);
        StringBuilder sb = new StringBuilder();

        // Write your code here
        for (int i = 1; i <= n; i++) {
            sb.setLength(0);
            int numSpaces = n - i;
            spaces.setLength(numSpaces);
            pounds.setLength(i);
            System.out.println(sb.append(spaces).append(pounds));
        }
    }

    public static void main(String[] args) {
        staircase(5);
    }
}
