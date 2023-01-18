package scratch2;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Lambda2 {
    public static void main(String[] args) throws Exception{

    }

    public static void check(Object o) throws Exception {
        //Object o = (Object) new Lambda1().getLambda();
        List<Method> target = Arrays.stream(o.getClass().getMethods())
                .filter(m -> m.getName().equals("test"))
                .collect(Collectors.toList());
        target.get(0).setAccessible(true);
        System.out.println(target.get(0).invoke(o, "hello"));
    }
}
