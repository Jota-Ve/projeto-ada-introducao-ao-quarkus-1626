package br.org.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "University")
public class University extends PanacheEntity {

    private String abbreviation;
    private String name;
    private String city;
    private String state;
    private String country;

    @OneToMany
    private final Map<Long, Course> courses = new HashMap<>();

    // required for JPA
    protected University() {
    }

    public University(String abbreviation, String name, String city, String state, String country) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public void addCourse(Course course){
        this.courses.put(course.getId(), course);
    }

    public void addCourse(List<Course> courses){
        courses.forEach(this::addCourse);
    }

    public Course removeCourse(Long courseId){
        return this.courses.remove(courseId);
    }

    public Course removeCourse(Course course){
        return removeCourse(course.getId());
    }

    public List<Course> removeCourse(List<Course> courses){
        List<Course> allRemoved = new ArrayList<>();

        for (Course c: courses) {
            Course removed = removeCourse(c);
            if (removed != null)
                allRemoved.add(removed);
        }
        return allRemoved;
    }
}
