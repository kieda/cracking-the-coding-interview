package scratch;
//
//import common.InvalidArgumentException;
//import common.InvalidArgumentSizeException;
//import common.InvalidArgumentTypeException;
//
//import javax.lang.model.type.ArrayType;
//import java.io.Serializable;
//import java.lang.reflect.*;
//import java.sql.Ref;
//import java.util.*;
//
//public class Params {
//
//
//
//    static class XYZ extends Params implements Iterable<String>,
//            Comparable<String>
//            {
//        @Override
//        public Iterator<String> iterator() {
//            return null;
//        }
//
//        @Override
//        public int compareTo(String o) {
//            return 0;
//        }
//    }
//
//    public static <W extends String, T extends Params & Iterable<W[]> & Comparable<String>, U extends String, Z extends Number> void coiffle
//            (
//                    T t,
//                    Collection<U> u,
//                    T[][] why,
//                    Object[][][] zz
//                    ,
//                    List<? super Params.XYZ> xyz
//                    , int asd, Z[] arr, int[] ia, long cray, boolean bool, Boolean brule, Integer i
//            ) {
//
//    }
//
//    private static boolean checkBounds(Class<?> c, Type t, Object inst) throws ReflectiveOperationException {
//
//
//        return true;
//    }
//
//    private final static Map<Type, Class<?>> unboxingMap = Map.of(
//            boolean.class, Boolean.class,
//            char.class, Character.class,
//            double.class, Double.class,
//            float.class, Float.class,
//            int.class, Integer.class,
//            long.class, Long.class
//    );
//
//    private static Type getClass(Object inst) {
//        return inst == null ? null : inst.getClass();
//    }
//
//    /**
//     * Checks parameters against arguments
//     * @param parameters parameters from a method
//     * @param arguments object arguments we expect to pass in
//     * @throws IllegalArgumentException if the arguments do not match the parameters
//     * @throws ReflectiveOperationException if there's an unexpected parameter
//     */
//    public static void checkParameters(Parameter[] parameters, Object[] arguments) throws InvalidArgumentException, ReflectiveOperationException {
//        if(parameters.length != arguments.length)
//            throw new InvalidArgumentSizeException("Invalid number of parameters specified", parameters.length, arguments.length);
//
//        for(int i = 0; i < parameters.length; i++) {
//            Parameter param = parameters[i];
//            Object inst = arguments[i];
//            Type paramType = param.getParameterizedType();
//
//            if (paramType instanceof TypeVariable) {
//                // this is a TypeVariable, which may have bounds. We check the bounds of the parameter
//                // against the instance
//                TypeVariable typeVariable = ((TypeVariable) paramType);
//                for (int j = 0; j < typeVariable.getBounds().length; j++) {
//                    Type type = typeVariable.getBounds()[j];
//                    if (type instanceof ParameterizedType) {
//                        // check the instance against the parameterized bound
//                        Type raw = ((ParameterizedType) type).getRawType();
//                        if(!((Class<?>) raw).isInstance(inst))
//                            throw new InvalidArgumentTypeException("Instance does not match all parameterized bounds", i, param, inst, raw, getClass(inst));
//                    } else if (type instanceof Class<?>) {
//                        // check the instance against the Class bound
//                        if(!((Class<?>) type).isInstance(inst))
//                            throw new InvalidArgumentTypeException("Instance does not match all bounds", i, param, inst, type, getClass(inst));
//                    } else {
//                        // we should never expect this situation to occur.
//                        // * wildcard types and array types cannot exist here
//                        // * no such thing as <X extends Object[]> method(X arg1)
//                        // * no such thing as <? super Interface> method(? arg1)
//                        throw new ReflectiveOperationException("Unexpected type in bounds: " + type.getTypeName() + " : " + type.getClass());
//                    }
//                }
//            }
//
//            if (unboxingMap.containsKey(paramType)) {
//                // this is a primitive.
//                // We convert the primitive to its object type, then we check that instance aligns with the primitive
//                if(!unboxingMap.get(paramType).isInstance(inst))
//                    throw new InvalidArgumentTypeException("Unexpected primitive type", i, param, inst, paramType, getClass(inst));
//            } else {
//                // this is an object.
//                // Check the type of the instance against the parameter directly
//                if(!param.getType().isInstance(inst))
//                    throw new InvalidArgumentTypeException("Instance does not match parameter's class", i, param, inst, param.getType(), getClass(inst));
//            }
//
//            // Note: nothing to do if Type is of ParameterizedType, as we can't get the argument's type at runtime.
//            //       at best we can settle with checking if the instance is the right class
//            // Note: Wildcard type not possible in an argument
//
//            // if we encounter a generic array, check that the instance matches in dimensionality
//            // check that the underlying component also matches, including bounds if the parameter is an array with generics
//            isNull:
//            if(paramType instanceof GenericArrayType) {
//                if(Objects.isNull(inst)) {
//                    // if the instance is null, we will immediately accept the input
//                    // this is because null is an acceptable value for any array type T[]
//                    break isNull;
//                }
//                Class<?> instanceClass = inst.getClass();
//                Type arrayType = paramType;
//                while (arrayType instanceof GenericArrayType) {
//                    arrayType = ((GenericArrayType) paramType).getGenericComponentType();
//                    // if the type specified is an array, then we expect that the instance should also be an array.
//                    // we traverse the instance as well
//                    instanceClass = instanceClass.getComponentType();
//                    if (instanceClass == null) {
//                        throw new InvalidArgumentTypeException("There are more parameter array dimensions than found in instance",
//                                i, param, inst, arrayType, instanceClass);
//                    }
//                }
//                // we expect that we have traversed both the instance and the parameter's type to the inner type
//                if(instanceClass.getComponentType() != null)
//                    throw new InvalidArgumentTypeException("There are more instance array dimensions than specified by parameter",
//                            i, param, inst, null, instanceClass.getComponentType());
//
//                if (arrayType instanceof TypeVariable) {
//                    TypeVariable tv = ((TypeVariable) arrayType);
//                    for (int j = 0; j < tv.getBounds().length; j++) {
//                        Type type = tv.getBounds()[j];
//                        if (type instanceof ParameterizedType) {
//                            Type raw = ((ParameterizedType) type).getRawType();
//                            if(!((Class<?>) raw).isAssignableFrom(instanceClass))
//                                throw new InvalidArgumentTypeException("Innermost generic array component is not assignable to all parametric types",
//                                        i, param, inst, raw, instanceClass);
//                        } else if (type instanceof Class<?>) {
//                            if(!((Class<?>) type).isAssignableFrom(instanceClass))
//                                throw new InvalidArgumentTypeException("Innermost generic array component is not assignable to all parametric types",
//                                        i, param, inst, type, instanceClass);
//                        } else {
//                            // wildcard types and array types cannot exist here
//                            // no such thing as <X extends Object[]> method(X arg1)
//                            // no such thing as <? super Interface> method(? arg1)
//                            throw new ReflectiveOperationException("Unexpected type in bounds: " + type.getTypeName() + " : " + type.getClass());
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    public static void main(String[] args) throws ReflectiveOperationException{
//        Method me = Arrays.stream(Params.class.getDeclaredMethods())
//            .filter(m -> m.getName().equals("coiffle"))
//                .findFirst().get();
//        List<Parameter> params = List.of(me.getParameters());
//        Object[] instances = new Object[] {
//            new XYZ(),
//            List.of("asdf", "fsad"),
//            new XYZ[][]{{new XYZ()}, {null, null}},
//            new Object[][][]{{{1},{2,3}},{{4}, {5,6},{7,8,9}}},
//            List.of(new Params(), new Object(), new XYZ()),
//            7,
//            new Integer[]{1, 2, 3}, // note: will not match up if this is String[]
//            new int[]{4, 5, 6},
//            -123012309L,
//            true,
//            false,
//            -10
//        };
//        for(int i = 0; i < params.size(); i++) {
//            Parameter p = params.get(i);
//            Class<?> c = p.getType();
//            Object inst = instances[i];
//            Type t = p.getParameterizedType();
//            System.out.println(p.getName() + " : " + p.getType() + " : " + c + " : "+ t + " : " + t.getClass());
//            if(t instanceof TypeVariable) {
//                TypeVariable tv = ((TypeVariable)t);
//                System.out.println(tv.getTypeName() + " : " + tv.getName());
//                System.out.println(Arrays.asList(tv.getBounds()));
//                boolean boundsMatch = true;
//                for(int j = 0; j < tv.getBounds().length; j++) {
//                    Type type = tv.getBounds()[j];
//                    if (type instanceof ParameterizedType){
//                        Type raw = ((ParameterizedType) type).getRawType();
//                        boundsMatch = ((Class<?>)raw).isInstance(inst);
//                    } else if(type instanceof Class<?>) {
//                        boundsMatch = ((Class<?>) type).isInstance(inst);
//                    } else {
//                        // wildcard types and array types cannot exist here
//                        // no such thing as <X extends Object[]> method(X arg1)
//                        // no such thing as <? super Interface> method(? arg1)
//                        throw new ReflectiveOperationException("Unexpected type variable : " + type.getTypeName() + " : " + type.getClass());
//                    }
//                    if(!boundsMatch)
//                        break;
//                }
//
//                System.out.println("bounds match: " + boundsMatch);
//            }
//
//            // Wildcard type not possible, we can't infer anything from ParameterizedType
//            if(t instanceof ParameterizedType) {
//                // nothing to do here, we can't tell what the instance's generic type is
//                Arrays.stream(((ParameterizedType) t).getActualTypeArguments()).forEach(x -> {
//                    System.out.println(x.getTypeName() + " : " + x.getClass());
//                });
//                System.out.println("YY: " + List.of(inst.getClass().getTypeParameters()));
//            }
//
//            System.out.println("instance : " + inst);
//            System.out.println("class : " + inst.getClass());
//            if(unboxingMap.containsKey(t)) {
//                // this is a primitive, we just check that the argument type aligns
//                System.out.println("instances match : " + unboxingMap.get(t).isInstance(inst));
//            } else {
//                System.out.println("instances match : " + c.isInstance(inst));
//            }
//
//            // drill down each time componentType is
//            boolean arraysMatch = true;
//            Class<?> instanceClass = inst.getClass();
//            while(t instanceof GenericArrayType) {
//                t = ((GenericArrayType)t).getGenericComponentType();
//                // if the type specified is an array, then we expect that the instance should also be an array.
//                // we traverse the instance as well
//                instanceClass = instanceClass.getComponentType();
//                if(instanceClass == null) {
//                    arraysMatch = false;
//                    break;
//                }
//            }
//            System.out.println("arrays match : " + arraysMatch);
//            System.out.println("instance : " + instanceClass);
//            System.out.println("param type : " + t + " : " + t.getClass());
//
//            if(t instanceof TypeVariable) {
//                TypeVariable tv = ((TypeVariable)t);
//                System.out.println(tv.getTypeName() + " : " + tv.getName());
//                System.out.println(Arrays.asList(tv.getBounds()));
//                boolean boundsMatch = true;
//                for(int j = 0; j < tv.getBounds().length; j++){
//                    Type type = tv.getBounds()[j];
//                    if (type instanceof ParameterizedType){
//                        Type raw = ((ParameterizedType) type).getRawType();
//                        boundsMatch = ((Class<?>)raw).isAssignableFrom(instanceClass);
//                    } else if(type instanceof Class<?>) {
//                        boundsMatch = ((Class<?>) type).isAssignableFrom(instanceClass);
//                    } else {
//                        // wildcard types and array types cannot exist here
//                        // no such thing as <X extends Object[]> method(X arg1)
//                        // no such thing as <? super Interface> method(? arg1)
//                        throw new ReflectiveOperationException("Unexpected type variable : " + type.getTypeName() + " : " + type.getClass());
//                    }
//                    if(!boundsMatch)
//                        break;
//                }
//
//                System.out.println("bounds match: " + boundsMatch);
//            }
//            System.out.println();
//        }
//    }
//}
