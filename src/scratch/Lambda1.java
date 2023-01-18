package scratch;

import scratch2.Lambda2;

import java.util.function.Predicate;

public class Lambda1 {
    public boolean coffle(String str) {
        System.out.println(str);
        if(str.length() > 4)
            return true;
        return false;
    }
    public Predicate<String> myLambda = this::coffle;
    public Predicate<String> getLambda() {
        return myLambda;
    }

    public static void main(String[] args) throws Exception{
        Lambda1 l1 = new Lambda1();
        Lambda2.check((Object)l1.myLambda);
    }
}
