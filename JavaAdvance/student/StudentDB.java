package info.kgeorgiy.ja.latanov.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
/**
 * @author created by Daniil Latanov
 */

public class StudentDB implements StudentQuery {

    private static final Comparator<Student> CUSTOM_COMPARATOR = Comparator.comparing(Student::getLastName)
            .thenComparing(Student::getFirstName).reversed()
            .thenComparing(Student::compareTo);

    private <T> List<T> getStudents(Collection<Student> students, Function<Student, T> fun) {
        return students.stream().map(fun).collect(Collectors.toList());
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getStudents(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getStudents(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return getStudents(students, Student::getGroup);
    }

    private <T> Predicate<Student> getStudentPredicate(Function<Student, T> fun, T name) {
        return i -> fun.apply(i).equals(name);
    }


    @Override
    public List<String> getFullNames(List<Student> students) {
        return getStudents(students, student -> ("%s %s").formatted(student.getFirstName(), student.getLastName()));
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream().map(Student::getFirstName).collect(Collectors.toSet());
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream().max(Comparator.comparing(Student::getId)).map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return students.stream().sorted(Student::compareTo).collect(toList());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return students.stream().sorted(CUSTOM_COMPARATOR).toList();
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return students.stream().filter(getStudentPredicate(Student::getFirstName, name)).sorted(CUSTOM_COMPARATOR).collect(toList());
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return students.stream().filter(getStudentPredicate(Student::getLastName, name)).collect(toList());
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return students.stream().filter(getStudentPredicate(Student::getGroup, group)).sorted(CUSTOM_COMPARATOR).collect(toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return students.stream().filter(getStudentPredicate(Student::getGroup, group)).collect(Collectors.toMap(
                Student::getLastName, Student::getFirstName, BinaryOperator.minBy(Comparator.naturalOrder())));
    }
}
