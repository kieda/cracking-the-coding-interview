package common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public interface Tuple {
    public Object[] getItems();

    /**
     * Applies the data contained in this tuple as arguments to function.
     * If the method returns void, this method returns Void.TYPE
     * @param object
     * @param function
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public default Object apply(Object object, Method function) throws InvocationTargetException, IllegalAccessException {
        Object result = function.invoke(object, getItems());
        if(function.getReturnType().equals(void.class)) {
            return void.class;
        }
        return result;
    }
}
