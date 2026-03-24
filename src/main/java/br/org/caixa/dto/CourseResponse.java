package br.org.caixa.dto;

import java.util.List;

public record CourseResponse(Long universityId, Long id, String name, List<LessonResponse> lessons) {
}
