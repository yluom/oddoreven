package org.ekino;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.lang.StringTemplate.STR;

/**
 * Dumb generator of dumb code, but in java 21 8-)<br>
 *
 * https://www.baeldung.com/java-code-too-large-error
 * https://stackoverflow.com/questions/11437905/java-too-many-constants-jvm-error
 * https://www.jetbrains.com/help/idea/using-language-injections.html#use-language-injection-comments
 */
public class Generator {
    final Path javaFile = Paths.get("/Users/leo.mouly/IdeaProjects/oddoreven/src/main/java/org/ekino/OddOrEven.java");

    void main(String[] args) {
        // Nombre de conditions à gérer -> le number à maximiser
        long maxNNumberOfConditions = Long.parseLong(args[0]);
        Generator generator = new Generator();
        generator.generateSimple(maxNNumberOfConditions);

        // generator.generateGolfed(maxNNumberOfConditions);
        // generator.generateWithSubMethods(maxNNumberOfConditions, true);
        // generator.generateWithSubMethods(maxNNumberOfConditions, false);
        // generator.generateWithSubMethodsNoConstants(maxNNumberOfConditions, true);
    }

    Generator() {
        initFile();
    }

    // 4095 2^12
    void generateSimple(long conditionsToGenerate) {
        System.out.println("Generating DUMB java implementation.");

        appendToFile("""
                // java --source 21 --enable-preview OddOrEven.java 4095
                void main(String[] args) {
                    long number = Long.parseLong(args[0]);""");

        //    if (number == 0) System.out.println("even");
        var conditions = LongStream.range(0, conditionsToGenerate)
                .mapToObj(number -> STR."    if (number == \{number}) System.out.println(\"\{ isEvenDontLookMyImplemPlease(number) ? "even" : "odd"}\");")
                .collect(Collectors.joining("\n"));

        appendToFile(conditions);
        appendToFile("}");
    }


    // Max 5460/5461
    void generateGolfed(long conditionsToGenerate) {
        System.out.println("Generating GOLFED java implementation.");
        appendToFile("""
                // java --source 21 --enable-preview OddOrEven.java 5460
                void even(){System.out.println("even");}
                void odd(){System.out.println("odd");}
                void main(String[] a){
                var n = Long.parseLong(a[0]);""");

        var conditions = LongStream.range(0, conditionsToGenerate)
                .mapToObj(num -> STR."if (n==\{num}) \{isEvenDontLookMyImplemPlease(num) ? "even" : "odd"}();")
                .collect(Collectors.joining("\n"));

        appendToFile(conditions);
        appendToFile("}");
    }

    // maxL 32739 - 32740 = 2^15 // maxI 98206 - 98207 2^15+2^16
    void generateWithSubMethods(long conditionsToGenerate, boolean longType) {
        int maxNumberOfConditions = 5460;
        System.out.println(STR."Generating java implementation with submethods of size \{maxNumberOfConditions}");

        appendToFile("// java --source 21 --enable-preview OddOrEven.java 32739");

        // Write submethods
        partition(LongStream.range(0, conditionsToGenerate).boxed(), maxNumberOfConditions)
                .map(l -> createMethod(l, longType ? "long" : "int"))
                .forEachOrdered(this::appendToFile);

        // Write main Boilerplate
        appendToFile("""
                void even(){System.out.println("even");}
                void odd(){System.out.println("odd");}
                void main(String[] a){""");
        appendToFile(longType ? "var n = Long.parseLong(a[0]);" : "var n = Integer.parseInt(a[0]);");

        // Write method calls
        LongStream.range(0, conditionsToGenerate)
                .filter(num -> num % maxNumberOfConditions == 0)
                .forEach(num -> appendToFile(STR."    process\{num}(n);"));
        appendToFile("}");
    }

    // ????
    void generateWithSubMethodsNoConstants(long conditionsToGenerate, boolean longType) {
        int maxNumberOfConditions = 5041; // 5041 is the new max here
        System.out.println(STR."Generating java implementation with submethods of size \{maxNumberOfConditions} WITHOUT constants");

        appendToFile("""
                     // java --source 21 --enable-preview OddOrEven.java 32739
                     static long next = -1;
                     long next(){return ++next;}""");

        // Write submethods
        partition(LongStream.range(0, conditionsToGenerate).boxed(), maxNumberOfConditions)
                .map(l -> createMethodNoConstants(l, longType ? "long" : "int"))
                .forEachOrdered(this::appendToFile);

        // Write main Boilerplate
        appendToFile("""
                void even(){System.out.println("even");}
                void odd(){System.out.println("odd");}
                void main(String[] a){""");
        appendToFile(longType ? "var n = Long.parseLong(a[0]);" : "var n = Integer.parseInt(a[0]);");

        // Write method calls
        LongStream.range(0, conditionsToGenerate)
                .filter(num -> num % maxNumberOfConditions == 0)
                .forEach(num -> appendToFile(STR."    process\{num}(n);"));
        appendToFile("}");
    }

    private String createMethod(List<Long> longs, String type) {
        return STR."void process\{longs.getFirst()}(\{type} n) {\n"
               + longs.stream()
                       .map(num -> STR."    if (n==\{num}) \{isEvenDontLookMyImplemPlease(num) ? "even" : "odd"}();")
                       .collect(Collectors.joining("\n"))
               + "\n}\n";
    }

    private String createMethodNoConstants(List<Long> longs, String type) {
        return STR."void process\{longs.getFirst()}(\{type} n) {\n"
               + longs.stream()
                       .map(num -> STR."    if (n==next()) \{isEvenDontLookMyImplemPlease(num) ? "even" : "odd"}();")
                       .collect(Collectors.joining("\n"))
               + "\n}\n";
    }

    private void appendToFile(String line) {
        try {
            Files.writeString(javaFile, STR."\{line}\n", StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initFile() {
        try {
            if (Files.exists(javaFile)) {
                Files.delete(javaFile);
            }
            Files.createFile(javaFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Copy pasted code from the internet, DON'T TRUST !
     */
    static boolean isEvenDontLookMyImplemPlease(long num) {
        return num % 2 == 0;
    }
    static Stream<List<Long>> partition(Stream<Long> stream, int batchSize) {
        List<List<Long>> currentBatch = new ArrayList<List<Long>>(); //just to make it mutable
        currentBatch.add(new ArrayList<Long>(batchSize));
        return Stream.concat(stream
                .sequential()
                .map(t -> {
                    currentBatch.getFirst().add(t);
                    return currentBatch.getFirst().size() == batchSize ? currentBatch.set(0, new ArrayList<>(batchSize)) : null;
                }), Stream.generate(() -> currentBatch.getFirst().isEmpty() ? null : currentBatch.getFirst())
                .limit(1)
        ).filter(Objects::nonNull);
    }


}

