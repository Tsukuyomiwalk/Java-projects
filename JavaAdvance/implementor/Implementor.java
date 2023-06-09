package info.kgeorgiy.ja.latanov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.jar.Attributes;

import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import java.util.zip.ZipEntry;

/**
 * class implements {@link Impler} and {@link JarImpler}.
 *
 * @author created by Daniil Latanov
 */
public class Implementor implements JarImpler {

    /**
     * default main class for new Implementor {@link Impler}
     */

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Argument problems");
            return;
        }
        try {
            new Implementor().implement(Class.forName(args[0]), Paths.get(args[1]));
        } catch (ImplerException e) {
            System.err.println("ImplerException" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFound" + e.getMessage());
        }

    }
    /** default constructor
     * */
    public Implementor() {
    }


    /**
     * write to the file all the java class implementation for the token given
     * Create a {@link String} for all {@link Method}'s, {@link Parameter}'s, {@link Exception}'s
     * that constructed in {@link #methodsToString(Class)} and write all these java
     * implementation files down to file
     * {@link Impler}
     * @param token with type Class
     * @param root  with type path - root directory
     * {@link ImplerException}
     * @throws ImplerException for the wrong implementation files
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        final String HW_CONST = token.getSimpleName() + "Impl";
        if (!token.isInterface() || Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Can't implement this class");
        }

        Path path = Paths.get(root.toString(), token.getPackageName().replace(".", File.separator), HW_CONST + ".java");

        if (path.getParent() != null) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                System.err.println("Can't create directories " + e.getMessage());
            }
        }
        try (Writer writer = Files.newBufferedWriter(path)) {
            writer.write(token.getPackageName().isEmpty() ? "" : "package " +
                    token.getPackageName() + ";" + System.lineSeparator());
            writer.write("public class " +
                    HW_CONST + " implements " + token.getCanonicalName() + " {");
            writer.write(methodsToString(token) + "}");
        } catch (IOException e) {
            throw new ImplerException("IOException or troubles with writer ", e);
        }
    }

    /**
     * gives the {@link StringBuilder} of all methods
     * <p>
     * create a {@link StringBuilder} which concatenate all the methods
     * * which contains parameters from {@link #parametersToString(Method)},
     * * exceptions from {@link #exceptionsToString(Method)}
     * * and the return value from {@link #getReturnToString(Method)} for the method given
     *
     * @param token with type class</?> and class gives the methods' implementation for this token
     * @return {@link StringBuilder} for all methods in class file
     */
    private StringBuilder methodsToString(Class<?> token) {
        StringBuilder methodConstructor = new StringBuilder();
        Method[] methods;
        methods = token.getMethods();
        if (methods.length == 0) {
            return methodConstructor;
        }
        Arrays.stream(methods).forEach(m -> {
            methodConstructor.append("public ")
                    .append(getClassEntityToString(m)).append("(");

            methodConstructor.append(parametersToString(m));

            methodConstructor.append(exceptionsToString(m));

            methodConstructor.append("{")
                    .append(System.lineSeparator()).append(getReturnToString(m));
        });
        return methodConstructor;
    }

    /**
     * gives the {@link StringBuilder} of all parameters
     * create a {@link StringBuilder} which concatenate all the parameters for concrete method
     * using for methods implementation in  {@link #methodsToString(Class)}
     *
     * @return {@link StringBuilder} for all parameters in method
     */
    private StringBuilder parametersToString(Method m) {
        Parameter[] parameters;
        parameters = m.getParameters();
        if (parameters.length == 0) {
            return new StringBuilder(")");
        }
        String p = Arrays.stream(parameters).map(this::getClassEntityToString).collect(Collectors.joining(" ,"));
        return new StringBuilder(p + ")");


    }

    /**
     * gives the {@link StringBuilder} of all exceptions
     * gives the {@link StringBuilder} of all exceptions
     * create a {@link StringBuilder} which concatenate all the exceptions for concrete method
     * using for {@link Method}'s implementation in {@link #methodsToString(Class)}
     *
     * @return {@link StringBuilder} for all exceptions in method
     */
    private StringBuilder exceptionsToString(Method m) {
        StringBuilder st = new StringBuilder();
        if (m.getExceptionTypes().length == 0) {
            return st;
        }
        st.append(" throws ");
        Class<?>[] exceptions = m.getExceptionTypes();
        return st.append(Arrays.stream(exceptions).
                map(Class::getCanonicalName).collect(Collectors.joining(", ")));
    }

    /**
     * @param m it's a method that should be implementing
     *          from file gives the {@link String} to concatenate in a right java file way
     * @return right-compiling section of java class
     */
    private String getReturnToString(Method m) {
        return "return " + defaultValue(m) + ";" + System.lineSeparator()
                + "}" + System.lineSeparator();
    }

    /**
     * @param name it's a name for method in class
     * @return the {@link Method} name to {@link String}
     */
    private String getClassEntityToString(Method name) {
        return name.getReturnType().getCanonicalName() + " " + name.getName();
    }

    /**
     * @param name it's a name for parameter in method
     * @return the {@link Parameter} name to {@link String}
     */
    private String getClassEntityToString(Parameter name) {
        return name.getType().getCanonicalName() + " " + name.getName();
    }

    /**
     * @param method it's a method that should be implementing from file given
     * @return the default values for method types
     */
    private String defaultValue(Method method) {
        return switch (method.getReturnType().getName()) {
            case "boolean" -> "false";
            case "void" -> "";
            case "byte", "short", "int", "long", "float", "double", "char" -> "0";
            default -> "null";
        };

    }

    /**
     * gives the .jar file according to java implementation
     * make the implementation of java file in {@link #implement(Class, Path)},
     * after that uses compiling function {@link #compileFiles(Class, String)},
     * and finally uses {@link JarOutputStream} and {@link ZipEntry}
     * for writing down the implementation for the classfile to the .jar
     *
     * @param token   type token to create implementation for
     * @param jarFile type {@link Path} it's a path to the directory for .jar files
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        final String HW_CONST = token.getSimpleName() + "Impl";
        Path dir = Paths.get("");
        implement(token, dir);
        String classToString = String.valueOf(Paths.get(dir.toString(),
                token.getPackageName().replace(".", File.separator), HW_CONST));

        compileFiles(token, classToString);
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            String classToJar = classToString.replace(File.separatorChar, '/');
            jos.putNextEntry(new ZipEntry(getClass(classToJar)));
            Files.copy(Paths.get(getClass(classToJar)), jos);
        } catch (IOException e) {
            throw new ImplerException("Failed to write to jar file", e);
        }
    }

    /**
     * @param classToString it's a path to our classfile
     *                      concatenating {@link String} value of class to .class file
     * @return {@link String}
     */
    private String getClass(String classToString) {
        return classToString + ".class";
    }

    /**
     * Compile files for token and directory
     * uses {@link #getClassPath(Class)} and {@link #getJava(String)}
     *
     * @param token         type token to have a path for compile implementation
     * @param classToString it's a parameter that's give a path to java file for compiling
     */
    public static void compileFiles(Class<?> token, String classToString) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final String[] args = {"-cp", getClassPath(token), getJava(classToString), "-encoding", "utf-8"};
        final int exitCode = compiler.run(null, null, null, args);
        if (exitCode != 0) {
            throw new ImplerException("Compilation error");
        }
    }

    /**
     * @param classToString it's a path to our classfile
     *                      concatenating {@link String} value of class to .java file
     * @return {@link String}
     */
    private static String getJava(String classToString) {
        return classToString + ".java";
    }

    /**
     * @param token it's a parameter which contains all the information about file for implementation
     *              gives the classpath for token
     * @return the classpath for the token given
     */
    private static String getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }
}
