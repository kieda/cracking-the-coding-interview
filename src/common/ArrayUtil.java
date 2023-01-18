package common;

import java.util.Arrays;

public class ArrayUtil {
    private ArrayUtil(){}

    public static int[][] copyMatrix(int[][] image) {
        int[][] result = new int[image.length][];
        for(int i = 0; i < image.length; i++) {
            result[i] = Arrays.copyOf(image[i], image[i].length);
        }
        return result;
    }

    public static Character[] toObject(char[] chars) {
        if(chars == null)
            return null;
        Character[] result = new Character[chars.length];
        for(int i = 0; i < chars.length; i++) {
            result[i] = chars[i];
        }
        return result;
    }
}
