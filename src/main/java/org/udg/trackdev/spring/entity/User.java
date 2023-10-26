package org.udg.trackdev.spring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.udg.trackdev.spring.configuration.UserType;
import org.udg.trackdev.spring.entity.views.EntityLevelViews;
import org.udg.trackdev.spring.entity.views.PrivacyLevelViews;
import org.udg.trackdev.spring.serializer.JsonDateSerializer;
import org.udg.trackdev.spring.serializer.JsonRolesSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "users")
public class User extends BaseEntityUUID {

  public static final int USERNAME_LENGTH = 12;
  public static final int EMAIL_LENGTH = 128;

  public User() {}

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  @NotNull
  @Column(unique=true, length=USERNAME_LENGTH)
  private String username;

  @NotNull
  @Column(unique=true, length=EMAIL_LENGTH)
  private String email;

  @NotNull
  private String password;

  @JsonView(EntityLevelViews.Basic.class)
  @JsonSerialize(using = JsonDateSerializer.class)
  private Date lastLogin;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
  private Collection<Subject> subjectsOwns = new ArrayList<>();

  @ManyToMany(mappedBy = "members")
  private Collection<Project> projects = new ArrayList<>();

  @ManyToMany(mappedBy = "students")
  private Collection<Course> course = new ArrayList<>();

  @ManyToMany()
  private Set<Role> roles = new HashSet<>();

  private String githubName;

  /**APARTIR D'AQUI SON MODIFICACIONS PER EL TFG**/

  @ManyToOne
  @JoinColumn(name = "currentProjectId")
  private Project currentProject;

  private String nicename;

  @NotNull
  private Boolean changePassword;

  @NotNull
  private Boolean enabled;

  /**********************************/

  @JsonView(PrivacyLevelViews.Private.class)
  public String getId() {
    return super.getId();
  }

  @JsonView(PrivacyLevelViews.Private.class)
  public String getEmail() {
    return email;
  }

  @JsonView({PrivacyLevelViews.Public.class, EntityLevelViews.Basic.class})
  public String getUsername() {
    return username;
  }

  @JsonView(PrivacyLevelViews.Public.class)
  public Project getCurrentProject() { return currentProject; }

  public void setCurrentProject(Project currentProject) { this.currentProject = currentProject; }

  @JsonView(PrivacyLevelViews.Public.class)
  public String getNicename() { return nicename; }

  public void setNicename() { this.nicename = nicename; }

  @JsonIgnore
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @JsonView(PrivacyLevelViews.Private.class)
  @JsonSerialize(using= JsonRolesSerializer.class)
  public Set<Role> getRoles() { return roles; }

  /** COSES NOVES **/

  @JsonView(PrivacyLevelViews.Public.class)
  public String nicename() { return nicename; }

  public void setNicename(String nicename) { this.nicename = nicename; }

  @JsonView(PrivacyLevelViews.Public.class)
  public Boolean getChangePassword() { return changePassword; }

  public void setChangePassword(Boolean changePassword) { this.changePassword = changePassword; }

  @JsonView(PrivacyLevelViews.Public.class)
  public Boolean getEnabled() { return enabled; }

  public void setEnabled(Boolean enabled) { this.enabled = enabled; }

  /***********/

  public void addRole(Role role) {
    roles.add(role);
  }

  public boolean isUserType(UserType userType) {
    boolean inRole = false;
    for(Role role: roles) {
      if(role.getUserType() == userType) {
        inRole = true;
        break;
      }
    }
    return inRole;
  }

  @JsonView(PrivacyLevelViews.Public.class)
  public Date getLastLogin(){ return lastLogin; }

  public void setLastLogin(Date lastLogin) {
    this.lastLogin = lastLogin;
  }

  public void addOwnCourse(Subject subject) { subjectsOwns.add(subject); }

  public void addToGroup(Project project) {
    this.projects.add(project);
  }

  public void removeFromGroup(Project project) {
    if(this.projects.contains(project)) {
      this.projects.remove(project);
    }
  }

  public void enrollToCourseYear(Course course) { this.course.add(course); }

  public void removeFromCourseYear(Course course) { this.course.remove(course); }

  @JsonIgnore
  public Collection<Course> getEnrolledCourseYears() { return this.course; }
}
