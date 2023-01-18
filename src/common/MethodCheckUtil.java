package common;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.IntPredicate;

public final class MethodCheckUtil {
    private final static Collection<Class> objectsAndPrimitives = Set.of(
            Object.class,
            boolean.class,
            char.class,
            double.class,
            float.class,
            int.class,
            long.class
    );
    public enum ArgumentSpace{
        ZERO(i -> i == 0), ONE_OR_MORE(i -> i > 0),
        ONE(i -> i == 1), ZERO_OR_MORE(i -> i >= 0);
        private IntPredicate condition;
        ArgumentSpace(IntPredicate condition) {
            this.condition = condition;
        }
        public boolean passes(int argCount) {
            return condition.test(argCount);
        }
    }
    private final static Map<Type, Class<?>> unboxingMap = Map.of(
            boolean.class, Boolean.class,
            char.class, Character.class,
            double.class, Double.class,
            float.class, Float.class,
            int.class, Integer.class,
            long.class, Long.class
    );

    private static Type getClass(Object inst) {
        return inst == null ? null : inst.getClass();
    }

    public static boolean doesReturnTypeMatch(Type returnType, Object instance) throws ReflectiveOperationException{
        try{
            checkReturnTypeMatch(returnType, instance);
            return true;
        } catch(InvalidArgumentException ex) {
            return false;
        }
    }
    // todo - combine functionality with checkArgMatchParam - mostly similar logic but a few key differences
    public static void checkReturnTypeMatch(Type returnType, Object instance) throws InvalidArgumentException, ReflectiveOperationException {
        if(returnType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) returnType;
            for (int j = 0; j < typeVariable.getBounds().length; j++) {
                Type type = typeVariable.getBounds()[j];
                if (type instanceof ParameterizedType) {
                    // check the instance against the parameterized bound
                    Type raw = ((ParameterizedType) type).getRawType();
                    if(!((Class<?>) raw).isInstance(instance))
                        throw new InvalidArgumentTypeException("Instance does not match all parameterized bounds", raw, getClass(instance));
                } else if (type instanceof Class<?>) {
                    // check the instance against the Class bound
                    if(!((Class<?>) type).isInstance(instance))
                        throw new InvalidArgumentTypeException("Instance does not match all bounds", type, getClass(instance));
                } else {
                    // we should never expect this situation to occur.
                    // * wildcard types and array types cannot exist here
                    // * no such thing as <X extends Object[]> method(X arg1)
                    // * no such thing as <? super Interface> method(? arg1)
                    throw new ReflectiveOperationException("Unexpected type in bounds: " + type.getTypeName() + " : " + type.getClass());
                }
            }
        }
        if (unboxingMap.containsKey(returnType)) {
            // this is a primitive.
            // We convert the primitive to its object type, then we check that instance aligns with the primitive
            if(!unboxingMap.get(returnType).isInstance(instance))
                throw new InvalidArgumentTypeException("Unexpected primitive type", returnType, getClass(returnType));
        } else if(returnType instanceof ParameterizedType){
            Class returnClass = (Class)((ParameterizedType)returnType).getRawType();
            // this is an object.
            // Check the type of the instance against the parameter directly
            if(!returnClass.isInstance(instance))
                throw new InvalidArgumentTypeException("Instance does not match return type's class", returnType, getClass(instance));
        } else if(returnType instanceof Class) {
            if(!((Class)returnType).isInstance(instance))
                throw new InvalidArgumentTypeException("Instance does not match return type's class", returnType, getClass(instance));
        } else if(returnType instanceof GenericArrayType) isNull: {
            if(instance == null) {
                // if the instance is null, we will immediately accept the input
                // this is because null is an acceptable value for any array type T[]
                break isNull;
            }
            Class<?> instanceClass = instance.getClass();
            Type arrayType = returnType;
            while (arrayType instanceof GenericArrayType) {
                arrayType = ((GenericArrayType) arrayType).getGenericComponentType();
                // if the type specified is an array, then we expect that the instance should also be an array.
                // we traverse the instance as well
                instanceClass = instanceClass.getComponentType();
                if (instanceClass == null) {
                    throw new InvalidArgumentTypeException("There are more return type array dimensions than found in instance",
                            arrayType, null);
                }
            }
            // we expect that we have traversed both the instance and the parameter's type to the inner type
            if(instanceClass.getComponentType() != null)
                throw new InvalidArgumentTypeException("There are more instance array dimensions than specified by return type",
                        null, instanceClass.getComponentType());

            if (arrayType instanceof TypeVariable) {
                TypeVariable tv = ((TypeVariable) arrayType);
                for (int j = 0; j < tv.getBounds().length; j++) {
                    Type type = tv.getBounds()[j];
                    if (type instanceof ParameterizedType) {
                        Type raw = ((ParameterizedType) type).getRawType();
                        if(!((Class<?>) raw).isAssignableFrom(instanceClass))
                            throw new InvalidArgumentTypeException("Innermost generic array component is not assignable to all parametric types",
                                    raw, instanceClass);
                    } else if (type instanceof Class<?>) {
                        if(!((Class<?>) type).isAssignableFrom(instanceClass))
                            throw new InvalidArgumentTypeException("Innermost generic array component is not assignable to all parametric types",
                                    type, instanceClass);
                    } else {
                        // wildcard types and array types cannot exist here
                        // no such thing as <X extends Object[]> method(X arg1)
                        // no such thing as <? super Interface> method(? arg1)
                        throw new ReflectiveOperationException("Unexpected type in bounds: " + type.getTypeName() + " : " + type.getClass());
                    }
                }
            }
        }
    }

    public static boolean doesArgMatchParam(Parameter parameter, Object argument) throws ReflectiveOperationException{
        try{
            checkArgMatchParam(parameter, argument);
            return true;
        } catch(InvalidArgumentException ex) {
            return false;
        }
    }

    /**
     * Checks parameter against an argument we are attempting to pass in
     * @param parameter parameters from a method
     * @param argument object arguments we expect to pass in
     * @throws IllegalArgumentException if the arguments do not match the parameters
     * @throws ReflectiveOperationException if there's an unexpected parameter data type against the (current) java specification
     */
    public static void checkArgMatchParam(Parameter parameter, Object argument) throws InvalidArgumentException, ReflectiveOperationException {
        Type paramType = parameter.getParameterizedType();

        if (paramType instanceof TypeVariable) {
            // this is a TypeVariable, which may have bounds. We check the bounds of the parameter
            // against the instance
            TypeVariable typeVariable = ((TypeVariable) paramType);
            for (int j = 0; j < typeVariable.getBounds().length; j++) {
                Type type = typeVariable.getBounds()[j];
                if (type instanceof ParameterizedType) {
                    // check the instance against the parameterized bound
                    Type raw = ((ParameterizedType) type).getRawType();
                    if(!((Class<?>) raw).isInstance(argument))
                        throw new InvalidArgumentTypeException("Instance does not match all parameterized bounds", raw, getClass(argument));
                } else if (type instanceof Class<?>) {
                    // check the instance against the Class bound
                    if(!((Class<?>) type).isInstance(argument))
                        throw new InvalidArgumentTypeException("Instance does not match all bounds", type, getClass(argument));
                } else {
                    // we should never expect this situation to occur.
                    // * wildcard types and array types cannot exist here
                    // * no such thing as <X extends Object[]> method(X arg1)
                    // * no such thing as <? super Interface> method(? arg1)
                    throw new ReflectiveOperationException("Unexpected type in bounds: " + type.getTypeName() + " : " + type.getClass());
                }
            }
        }

        if (unboxingMap.containsKey(paramType)) {
            // this is a primitive.
            // We convert the primitive to its object type, then we check that instance aligns with the primitive
            if(!unboxingMap.get(paramType).isInstance(argument))
                throw new InvalidArgumentTypeException("Unexpected primitive type", paramType, getClass(argument));
        } else {
            // this is an object.
            // Check the type of the instance against the parameter directly
            if(!parameter.getType().isInstance(argument))
                throw new InvalidArgumentTypeException("Instance does not match parameter's class", parameter.getType(), getClass(argument));
        }

        // Note: nothing to do if Type is of ParameterizedType, as we can't get the argument's type at runtime.
        //       at best we can settle with checking if the instance is the right class
        // Note: Wildcard type not possible in an argument

        // if we encounter a generic array, check that the instance matches in dimensionality
        // check that the underlying component also matches, including bounds if the parameter is an array with generics
        isNull:
        if(paramType instanceof GenericArrayType) {
            if(Objects.isNull(argument)) {
                // if the instance is null, we will immediately accept the input
                // this is because null is an acceptable value for any array type T[]
                break isNull;
            }
            Class<?> instanceClass = argument.getClass();
            Type arrayType = paramType;
            while (arrayType instanceof GenericArrayType) {
                arrayType = ((GenericArrayType) arrayType).getGenericComponentType();
                // if the type specified is an array, then we expect that the instance should also be an array.
                // we traverse the instance as well
                instanceClass = instanceClass.getComponentType();
                if (instanceClass == null) {
                    throw new InvalidArgumentTypeException("There are more parameter array dimensions than found in instance",
                            arrayType, null);
                }
            }
            // we expect that we have traversed both the instance and the parameter's type to the inner type
            if(instanceClass.getComponentType() != null)
                throw new InvalidArgumentTypeException("There are more instance array dimensions than specified by parameter",
                        null, instanceClass.getComponentType());

            if (arrayType instanceof TypeVariable) {
                TypeVariable tv = ((TypeVariable) arrayType);
                for (int j = 0; j < tv.getBounds().length; j++) {
                    Type type = tv.getBounds()[j];
                    if (type instanceof ParameterizedType) {
                        Type raw = ((ParameterizedType) type).getRawType();
                        if(!((Class<?>) raw).isAssignableFrom(instanceClass))
                            throw new InvalidArgumentTypeException("Innermost generic array component is not assignable to all parametric types",
                                    raw, instanceClass);
                    } else if (type instanceof Class<?>) {
                        if(!((Class<?>) type).isAssignableFrom(instanceClass))
                            throw new InvalidArgumentTypeException("Innermost generic array component is not assignable to all parametric types",
                                    type, instanceClass);
                    } else {
                        // wildcard types and array types cannot exist here
                        // no such thing as <X extends Object[]> method(X arg1)
                        // no such thing as <? super Interface> method(? arg1)
                        throw new ReflectiveOperationException("Unexpected type in bounds: " + type.getTypeName() + " : " + type.getClass());
                    }
                }
            }
        }
    }

    private final static Map<String, Tuple3<ArgumentSpace, Collection<Class>, Collection<Class>>> DEFAULT_ARGSPACE = Map.of(
            // predicates
            "test", Tuple3.make(ArgumentSpace.ONE_OR_MORE, objectsAndPrimitives, Set.of(boolean.class)),
            // functions, including primitives
            "apply", Tuple3.make(ArgumentSpace.ONE_OR_MORE, objectsAndPrimitives, Set.of(Object.class)),
            "applyAsInt", Tuple3.make(ArgumentSpace.ONE_OR_MORE, objectsAndPrimitives, Set.of(int.class)),
            "applyAsDouble", Tuple3.make(ArgumentSpace.ONE_OR_MORE, objectsAndPrimitives, Set.of(double.class)),
            "applyAsLong", Tuple3.make(ArgumentSpace.ONE_OR_MORE, objectsAndPrimitives, Set.of(long.class)),
            // suppliers, including primitives
            "get", Tuple3.make(ArgumentSpace.ZERO, Set.of(), Set.of(Object.class)),
            "getAsBoolean", Tuple3.make(ArgumentSpace.ZERO, Set.of(), Set.of(boolean.class)),
            "getAsDouble", Tuple3.make(ArgumentSpace.ZERO, Set.of(), Set.of(double.class)),
            "getAsInt", Tuple3.make(ArgumentSpace.ZERO, Set.of(), Set.of(int.class)),
            "getAsLong", Tuple3.make(ArgumentSpace.ZERO, Set.of(), Set.of(long.class))
    );

    private final static MethodCheckUtil DEFAULT_INSTANCE = new MethodCheckUtil(DEFAULT_ARGSPACE);
    private final Map<String, Tuple3<ArgumentSpace, Collection<Class>, Collection<Class>>> argSpace;

    public MethodCheckUtil(Map<String, Tuple3<ArgumentSpace, Collection<Class>, Collection<Class>>> argSpace) {
        this.argSpace = argSpace;
    }
    public static MethodCheckUtil getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    private static boolean trySetAccessible(Method m) {
        try{
            return m.trySetAccessible();
        } catch(SecurityException ex) {
            return false;
        }
    }

    private boolean isTypeInSpace(Class space, Type type) throws ReflectiveOperationException{
        if(type instanceof ParameterizedType) {
            // we just check that the raw type is an instance of the space, since we can't get the parameters from Class
            Class rawType = (Class)((ParameterizedType)type).getRawType();
            return space.isAssignableFrom(rawType);
        } else if(type instanceof GenericArrayType) {
            GenericArrayType genericArray = (GenericArrayType) type;
            // if the space accepts an arbitrary object, then we pass
            if(space.isAssignableFrom(Object.class)) {
                return true;
            }
            // drill down to the base type within the array
            Class instance = space;
            Type traverser = genericArray;
            do {
                traverser = ((GenericArrayType) traverser).getGenericComponentType();
                instance = instance.getComponentType();
            } while(traverser instanceof GenericArrayType && instance.isArray());

            // mismatch on array dimension if one of these statements are still true
            if(traverser instanceof GenericArrayType || instance.isArray()) {
                return false;
            }
            return isTypeInSpace(instance, traverser);
        } else if(type instanceof TypeVariable) {
            // declared type variable. Check that all of the bounds match
            Type[] typeBounds = ((TypeVariable)type).getBounds();
            boolean result = true;
            for(Type bound : typeBounds) {
                result = isTypeInSpace(space, bound) && result;
            }
            return result;
        } else if(type instanceof Class) {
            // if it's a class then
            return space.isAssignableFrom((Class)type);
        } else {
            throw new ReflectiveOperationException("Unexpected type: " + type.getTypeName() + " : " + type.getClass());
        }
    }

    private boolean isTypeInSpace(Collection<Class> space, Type toCheck) throws ReflectiveOperationException {
        for(Class item : space) {
            if(isTypeInSpace(item, toCheck))
                return true;
        }
        return false;
    }

    public <Arguments, Result> boolean acceptableMethod(Method method, Arguments args, Result result) {
        String methodName = method.getName();
        try {
            // 1. check that we will utilize this function
            return argSpace.containsKey(methodName)
                    // 2. check that the # of parameters matches with what we'd expect with the function
                    && argSpace.get(methodName).getFirst().passes(method.getParameterCount())
                    // 3. check that the return type and the result type match arg space
                    && isTypeInSpace(argSpace.get(methodName).getSecond(), method.getReturnType())
                    && Arrays.stream(method.getGenericParameterTypes()).allMatch(type -> {
                        try {
                            return isTypeInSpace(argSpace.get(methodName).getThird(), type);
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    // 3. check we have the correct number of parameters available to pass into the function (parity).
                    //    If Arguments are a Tuple, then we pass the arguments in individually
                    && method.getParameterCount() == (args instanceof Tuple ? ((Tuple) args).getItems().length : 1)
                    // 4. check that the method we're trying to access is public, and try to set it to accessible
                    && (method.getModifiers() & Modifier.PUBLIC) != 0
                    && trySetAccessible(method)
                    // 5. check the return type of the method against the expected return type
                    && doesReturnTypeMatch(method.getGenericReturnType(), result)
                    // 6. check arguments against the parameters of the method
                    && Tuple2.zipe(
                    Arrays.stream(method.getParameters()).iterator(),
                    Arrays.stream(args instanceof Tuple ? ((Tuple) args).getItems() : new Object[]{args}).iterator())
                    .stream()
                    // ensure that all of the arguments match the parameters
                    .allMatch(it -> {
                        Parameter param = it.getFirst();
                        Object arg = it.getSecond();
                        try {
                            return doesArgMatchParam(param, arg);
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
