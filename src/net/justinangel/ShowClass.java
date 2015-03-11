package net.justinangel;

/**
 * http://www.java2s.com/Code/Java/Reflection/Classreflection.htm
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ShowClass {

    public static String printClass(Class aClass) {
        return printClass(aClass, "").toString();
    }

    public static String printClass(Class aClass, String newLineModifier) {
        StringBuilder sb = new StringBuilder();
        printClass(aClass, newLineModifier, sb);
        return sb.toString();
    }

    public static void printClass(Class aClass, String newLineModifier, StringBuilder sb) {
        printClassSignature(aClass, newLineModifier,sb);

        Constructor[] constructors = aClass.getDeclaredConstructors();
        for (int i = 0; i < constructors.length; i++)
            printMethodOrConstructor(constructors[i], newLineModifier, sb);

        Field[] fields = aClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
            printField(fields[i], newLineModifier, sb);

        Method[] methods = aClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++)
            printMethodOrConstructor(methods[i], newLineModifier, sb);

        sb.append(newLineModifier + " }\n\n");
    }

    public static String printClassSignature(Class aClass, String newLineModifier) {
        StringBuilder sb = new StringBuilder();
        printClassSignature(aClass, newLineModifier, sb);
        return sb.toString();
    }

    private static void printClassSignature(Class aClass, String newLineModifier, StringBuilder sb) {
        if (aClass.isInterface()) {
            sb.append(newLineModifier  + Modifier.toString(aClass.getModifiers()) + " "
                    + typeName(aClass, true));
        } else if (aClass.getSuperclass() != null) {
            sb.append(newLineModifier + Modifier.toString(aClass.getModifiers()) + " class "
                    + typeName(aClass, true) + " extends " + typeName(aClass.getSuperclass()));
        } else {
            sb.append(newLineModifier + Modifier.toString(aClass.getModifiers()) + " class "
                    + typeName(aClass, true));
        }

        Class[] interfaces = aClass.getInterfaces();
        if ((interfaces != null) && (interfaces.length > 0)) {
            if (aClass.isInterface())
                sb.append(" extends ");
            else
                sb.append(" implements ");
            for (int i = 0; i < interfaces.length; i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append(typeName(interfaces[i]));
            }
        }

        sb.append(" {\n");
    }

    public static String typeName(Class t) {
        return typeName(t, false);
    }

    public static String typeName(Class t, boolean fullName) {
        String brackets = "";
        while (t.isArray()) {
            brackets += "[]";
            t = t.getComponentType();
        }
        String name = t.getName();
        int pos = name.lastIndexOf('.');
        if (pos != -1 && !fullName)
            name = name.substring(pos + 1);
        return name + brackets;
    }

    public static String modifiers(int m) {
        if (m == 0)
            return "";
        else
            return Modifier.toString(m) + " ";
    }

    public static String printField(Field f, String newLineModifier) {
        StringBuilder sb = new StringBuilder();
        printField(f, newLineModifier, sb);
        return sb.toString();
    }


    public static void printField(Field f, String newLineModifier, StringBuilder sb) {
        sb.append(newLineModifier+ "   " +  modifiers(f.getModifiers())
                + typeName(f.getType()) + " " + f.getName() + ";\n");
    }

    public static String printMethodOrConstructor(Member member, String newLineModifier) {
        StringBuilder sb = new StringBuilder();
        printMethodOrConstructor(member, newLineModifier, sb);
        return sb.toString();
    }

    public static void printMethodOrConstructor(Member member, String newLineModifier, StringBuilder sb) {
        Class returntype = null, parameters[], exceptions[];
        if (member instanceof Method) {
            Method m = (Method) member;
            returntype = m.getReturnType();
            parameters = m.getParameterTypes();
            exceptions = m.getExceptionTypes();
            sb.append(newLineModifier + "   " + modifiers(member.getModifiers())
                    + typeName(returntype) + " " + member.getName() + "(");
        } else {
            Constructor c = (Constructor) member;
            parameters = c.getParameterTypes();
            exceptions = c.getExceptionTypes();
            sb.append(newLineModifier + "   " + modifiers(member.getModifiers())
                    + typeName(c.getDeclaringClass()) + "(");
        }

        for (int i = 0; i < parameters.length; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(typeName(parameters[i]));
        }
        sb.append(")");
        if (exceptions.length > 0)
            sb.append(" throws ");
        for (int i = 0; i < exceptions.length; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(typeName(exceptions[i]));
        }
        sb.append(";\n");
    }
}



