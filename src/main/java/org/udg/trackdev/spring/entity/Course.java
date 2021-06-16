package org.udg.trackdev.spring.entity;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "courses")
public class Course extends BaseEntityLong {

    public Course() {}

    public Course(String name) {
        this.name = name;
    }

    @NonNull
    private String name;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId")
    private User owner;

    @Column(name = "ownerId", insertable = false, updatable = false)
    private String ownerId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    private Collection<CourseYear> courseYears;

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwner(@NonNull User owner) {
        this.owner = owner;
    }

    public void addCourseYear(CourseYear courseYear) { this.courseYears.add(courseYear); }
}
