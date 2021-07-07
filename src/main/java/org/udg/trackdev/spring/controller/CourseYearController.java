package org.udg.trackdev.spring.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.udg.trackdev.spring.entity.*;
import org.udg.trackdev.spring.entity.views.EntityLevelViews;
import org.udg.trackdev.spring.entity.views.PrivacyLevelViews;
import org.udg.trackdev.spring.service.AccessChecker;
import org.udg.trackdev.spring.service.CourseYearService;
import org.udg.trackdev.spring.service.GroupService;
import org.udg.trackdev.spring.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping(path = "/courses/years")
public class CourseYearController extends BaseController {

    @Autowired
    CourseYearService courseYearService;

    @Autowired
    GroupService groupService;

    @Autowired
    UserService userService;

    @Autowired
    AccessChecker accessChecker;

    @GetMapping
    @JsonView(EntityLevelViews.CourseYearComplete.class)
    public Collection<CourseYear> getCourseYears(Principal principal) {
        String userId = super.getUserId(principal);
        User user = userService.get(userId);
        return user.getEnrolledCourseYears();
    }

    @GetMapping(path = "/{yearId}")
    @JsonView(EntityLevelViews.CourseYearComplete.class)
    public CourseYear getCourseYear(Principal principal, @PathVariable("yearId") Long yearId) {
        CourseYear courseYear = courseYearService.get(yearId);
        return courseYear;
    }

    @PostMapping(path = "/{yearId}/invites")
    public IdObjectLong createInvite(Principal principal,
                                    @PathVariable("yearId") Long yearId,
                                    @Valid @RequestBody NewCourseInvite inviteRequest) {
        String userId = super.getUserId(principal);
        Invite createdInvite = courseYearService.createInvite(inviteRequest.email, yearId, userId);
        return new IdObjectLong(createdInvite.getId());
    }

    @GetMapping(path = "/{yearId}/students")
    @JsonView(PrivacyLevelViews.Public.class)
    public Set<User> getStudents(Principal principal,
                                 @PathVariable("yearId") Long yearId) {
        String userId = super.getUserId(principal);
        CourseYear courseYear = courseYearService.get(yearId);
        accessChecker.checkCanViewCourseYearStudents(courseYear, userId);
        return courseYear.getStudents();
    }

    @DeleteMapping(path = "/{yearId}/students/{username}")
    public ResponseEntity getStudents(Principal principal,
                                      @PathVariable("yearId") Long yearId,
                                      @PathVariable("username") String username) {
        String principalUserId = super.getUserId(principal);
        courseYearService.removeStudent(yearId, username, principalUserId);
        return okNoContent();
    }

    @GetMapping(path = "/{yearId}/groups")
    @JsonView(EntityLevelViews.Basic.class)
    public Collection<Group> getGroups(Principal principal,
                                    @PathVariable("yearId") Long yearId) {
        String userId = super.getUserId(principal);
        CourseYear courseYear = courseYearService.get(yearId);
        accessChecker.checkCanViewCourseYearGroups(courseYear, userId);
        return courseYear.getGroups();
    }

    @PostMapping(path = "/{yearId}/groups")
    public IdObjectLong createGroup(Principal principal,
                                     @PathVariable("yearId") Long yearId,
                                     @Valid @RequestBody NewGroup groupRequest) {
        String userId = super.getUserId(principal);
        Group createdGroup = groupService.createGroup(groupRequest.name, groupRequest.members, yearId, userId);
        return new IdObjectLong(createdGroup.getId());
    }

    static class NewCourseInvite {
        @NotNull
        @Email
        @Size(max = User.EMAIL_LENGTH)
        public String email;
    }

    static class NewGroup {
        @NotBlank
        @Size(max = Group.NAME_LENGTH)
        public String name;

        public Collection<String> members;
    }
}