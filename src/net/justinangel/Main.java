package net.justinangel;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {

    private static String newerJar = "C:\\Program Files (x86)\\Android\\sdk\\platforms\\android-22\\android.jar";
    private static String olderJar = "C:\\Program Files (x86)\\Android\\sdk\\platforms\\android-21\\android.jar";

    public static void main(String[] args) {
        if (args.length >= 1)
            newerJar = args[0];
        if (args.length >= 2)
            olderJar = args[1];

        try {
            String result = CompareJarsForRealz();

            System.out.print(result);

            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            writer.print(result);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        new Scanner(System.in).next();
    }

    private static String CompareJarsForRealz() throws IOException, ClassNotFoundException {
        Map<String,Class> newJarClasses = getClassesFrom(newerJar);
        Map<String,Class> oldJarClasses = getClassesFrom(olderJar);

        StringBuilder sb = new StringBuilder();
        sb.append("\n===== Results of comparing two JARs =====\n\n");
        sb.append("Comparing ").append(newJarClasses.size()).append(" classes from ").append(newerJar).append("\n");
        sb.append("To ").append(oldJarClasses.size()).append(" classes from ").append(olderJar).append("\n\n");


        // print new or modified classes
        for(String className : newJarClasses.keySet())
        {
            Class newClass = newJarClasses.get(className);
            Class olderClass = oldJarClasses.get(className);

            // print new classes
            if (olderClass == null)
            {
                ShowClass.printClass(newClass, "+ ", sb);
                continue;
            }

            // print modified classes
            if (!ShowClass.printClass(newClass).equals(ShowClass.printClass(olderClass))) {
                printChangesInClass(newClass, olderClass, sb);
            }
        }

        // print removed classes
        for(String className : oldJarClasses.keySet())
        {
            Class newClass = newJarClasses.get(className);
            Class olderClass = oldJarClasses.get(className);

            if (newClass == null)
            {
                ShowClass.printClass(olderClass, "- ", sb);
                continue;
            }
        }


        return sb.toString();
    }

    private static void printChangesInClass(Class newClass, Class olderClass, StringBuilder sb) {
        String newClassSignature = ShowClass.printClassSignature(newClass, "");
        String oldClassSignature = ShowClass.printClassSignature(olderClass, "");

        if (newClassSignature.equals(oldClassSignature)) {
            sb.append(newClassSignature);
        } else {
            sb.append("- " + oldClassSignature);
            sb.append("* " + newClassSignature);
        }


        PrintDifferencesInMethodsOrConsructors(sb, newClass.getDeclaredConstructors(), olderClass.getDeclaredConstructors());

        PrintDifferencesInFields(sb, newClass.getDeclaredFields(), olderClass.getDeclaredFields());


        PrintDifferencesInMethodsOrConsructors(sb, newClass.getDeclaredMethods(), olderClass.getDeclaredMethods());

        sb.append("}\n\n");
    }

    private static void PrintDifferencesInMethodsOrConsructors(StringBuilder sb, Member[] newConstructors, Member[] oldConstructors) {
        // find removed members
        outerloop:
        for (Member oldConstructor : oldConstructors){
            for (Member newConstructor : newConstructors){
                if (ShowClass.printMethodOrConstructor(newConstructor, "").equals(ShowClass.printMethodOrConstructor(oldConstructor, ""))) {
                    continue outerloop;
                }
            }
            sb.append(ShowClass.printMethodOrConstructor(oldConstructor, "- "));
        }

        // find new members
        outerloop:
        for (Member newConstructor : newConstructors){
            for (Member oldConstructor : oldConstructors) {
                if (ShowClass.printMethodOrConstructor(newConstructor, "").equals(ShowClass.printMethodOrConstructor(oldConstructor, ""))) {
                    continue outerloop;
                }
            }
            sb.append(ShowClass.printMethodOrConstructor(newConstructor, "+ "));
        }
    }

    private static void PrintDifferencesInFields(StringBuilder sb, Field[] newFields, Field[] oldFields) {
        // find removed fields
        outerloop:
        for (Field oldField : oldFields){
            for (Field newField : newFields){
                if (ShowClass.printField(newField, "").equals(ShowClass.printField(oldField, ""))) {
                    continue outerloop;
                }
            }
            sb.append(ShowClass.printField(oldField, "- "));
        }

        // find new fields
        outerloop:
        for (Field newField : newFields){
            for (Field oldField : oldFields) {
                if (ShowClass.printField(newField, "").equals(ShowClass.printField(oldField, ""))) {
                    continue outerloop;
                }
            }
            sb.append(ShowClass.printField(newField, "+ "));
        }
    }

    private static boolean debug = true;
    // copied from @ http://stackoverflow.com/questions/11016092/how-to-load-classes-at-runtime-from-a-folder-or-jar
    public static Map<String,Class> getClassesFrom(String pathToJar) throws IOException, ClassNotFoundException {
        Map<String,Class> classes = new HashMap<String,Class>();


        JarFile jarFile = new JarFile(pathToJar);
        Enumeration e = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + pathToJar +"!/") };
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = (JarEntry) e.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            Class c = cl.loadClass(className);
            classes.put(className, c);
        }

        return classes;
    }
}
