package scratch;

import java.util.Arrays;
import java.util.List;

public class Varargs {
    public static boolean varargs(Object first, Object... items) {
        if(items == null) {
            System.out.println(first + ": items = null");
        } else {
            System.out.println(first + ": items = " + Arrays.asList(items));
        }
        return first != null && items != null && items.length % 2 == 0;
    }
    public static void main(String[] args) {
        varargs("asdf1", null);
        varargs("asdf2", (Object)null);
        Object[] myObjects = new Object[]{"crap", "crepe", "moon"};
        varargs("asdf3", myObjects);
        Object[] myObjects2 = new Object[]{};
        varargs("asdf4", myObjects2);
        varargs("asdf5", new Object[]{myObjects, myObjects2});
        varargs("asdf6", (Object) myObjects);
    }
}
