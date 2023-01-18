package common;

public class Sort {
    private Sort() {

    }

    private static int binarySearchHelper(char[] in, char val, int low, int high) {
        // todo - is low > in.length - 1 necessary?
        // no - notice that high only becomes a lower value. Therefore, high will never be greater than or equal to in.length
        // Thus, if low > in.length - 1 then by definition low > high
        if(low > high) //|| low > in.length - 1)
            return -1;
        int mid = low + (high - low) / 2;
        char midVal = in[mid];
        if(val == midVal)
            return mid;
        if(val < midVal) {
            // recurse on left branch
            return binarySearchHelper(in, val, low, mid - 1);
        } else {
            // recurse on right branch
            return binarySearchHelper(in, val, mid + 1, high);
        }
    }
    public static int binarySearch(char[] in, char val) {
        return binarySearchHelper(in, val, 0, in.length - 1);
    }
    private static int binarySearchHelper(int[] in, int val, int low, int high) {
        if(low > high)
            return -1;
        int mid = low + (high - low) / 2;
        int midVal = in[mid];
        if(val == midVal)
            return mid;
        if(val < midVal) {
            // recurse on left branch
            return binarySearchHelper(in, val, low, mid - 1);
        } else {
            // recurse on right branch
            return binarySearchHelper(in, val, mid + 1, high);
        }
    }
    public static int binarySearch(int[] in, int val) {
        return binarySearchHelper(in, val, 0, in.length - 1);
    }
    public static void binarySort(char[] in) {

    }

    // null-safe comparator. null is least element
    public static <Y extends Comparable> int compare(Y o1, Y o2) {
        if(o1 == null && o2 == null) {
            return 0;
        } else if(o1 != null && o2 == null) {
            return 1;
        } else if(o1 == null && o2 != null) {
            return -1;
        } else {
            return o1.compareTo(o2);
        }
    }
}
