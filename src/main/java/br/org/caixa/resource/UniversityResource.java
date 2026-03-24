package br.org.caixa.resource;

import br.org.caixa.dto.*;
import br.org.caixa.model.University;
import io.quarkus.logging.Log;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.org.caixa.model.Course;
import br.org.caixa.model.Lesson;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/universities")
public class UniversityResource {
    public Optional<Course> findCourse(Long universityId, Long courseId){
        Optional<University> possibleUniversity = University.findByIdOptional(universityId);
        if (possibleUniversity.isEmpty()) {
            Log.info("University with ID " + universityId + " not found");
            return Optional.empty();
        }

        Optional<Course> possibleCourse = Course.findByIdOptional(courseId);
        if (possibleCourse.isEmpty()) {
            Log.info("Course with ID " + courseId + " not found");
            return Optional.empty();
        }

        return possibleCourse;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createUniversity(@Valid UniversityRequest request) {

        University university = new University(request.abbreviation(), request.name(), request.city(), request.state(), request.country());

        university.persist();

        URI location = URI.create("/universities/" + university.id);

        UniversityResponse payload = new UniversityResponse(
                university.id,
                university.getAbbreviation(),
                university.getName(),
                university.getCity(),
                university.getState(),
                university.getCountry(),
                List.of()
        );

        return Response.created(location)
                .header("Content-Type", "application/json")
                .entity(payload)
                .build();

    }

    @POST
    @Path("/{universityId}/courses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createCourse(@PathParam("universityId") Long universityId, @Valid CreateCourseRequest request) {
        // Verifica se a universidade existe
        University university = University.findById(universityId);
        if (university == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Course course = new Course(request.name());

        course.persist();

        university.addCourse(course);


        URI location = URI.create("/universities/" + universityId + "/courses/" + course.id);

        CourseResponse payload = new CourseResponse(universityId, course.id, course.getName(), List.of());

        return Response.created(location)
                .header("Content-Type", "application/json")
                .entity(payload)
                .build();

    }

    @PUT
    @Path("/{universityId}/courses/{id}")
    @Transactional
    public Response updateCourse(@PathParam("universityId") Long universityId, @PathParam("id") Long courseId, @Valid CreateCourseRequest request) {
        Log.info("Updating University(ID=" + universityId + ") with Course(ID=" + courseId + ")");

        Optional<Course> possibleCourse = findCourse(universityId, courseId);
        if (possibleCourse.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build(); // early-return

        Course course = possibleCourse.get();
        course.changeName(request.name());
        Log.info("Course(ID=" + courseId + ") in University(ID=" + universityId + " Updated");

        return Response.ok(new CourseResponse(universityId, course.id, course.getName(), List.of())).build();
    }

    @DELETE
    @Path("/{universityId}/courses/{id}")
    @Transactional
    public Response deleteCourse(@PathParam("universityId") Long universityId, @PathParam("id") Long courseId) {
        Course.deleteById(courseId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{universityId}/courses/")
    public Response getCourses(@PathParam("universityId") Long universityId) {
        List<Course> courses = Course.find("university.id = ?1", universityId).list();
        List<CourseResponse> response = courses
                .stream()
                .map((Course c) -> new CourseResponse(universityId, c.id, c.getName(), List.of()))
                .toList();

        return Response.ok(response).build();
    }

    @GET
    @Path("/{universityId}/courses/{id}")
    public Response getCourseById(@PathParam("universityId") Long universityId, @PathParam("id") Long courseId) {
        Log.info("Getting University(ID=" + universityId + ") with Course(ID=" + courseId + ")");
        Course course = Course.findById(courseId);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(new CourseResponse(course.id, course.getName(), List.of())).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/lessons")
    @Transactional
    public Response createLesson(@PathParam("id") Long id, @Valid CreateLessonRequest request) {

        Course course = Course.findById(id);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Lesson lesson = new Lesson(request.name());

        lesson.persist();

        course.addLesson(lesson);

        URI location = URI.create("/courses/" + course.id + "/lessons/" + lesson.id);

        LessonResponse response = new LessonResponse(lesson.id, lesson.getName());

        return Response.created(location)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{id}/lessons")
    public Response getLessonsByCourseId(@PathParam("id") Long id) {

        Course course = Course.findById(id);

        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<LessonResponse> response = course.getLessons()
                .stream()
                .map((Lesson l) -> new LessonResponse(l.id, l.getName()))
                .toList();

        return Response.ok(response).build();
    }
}
