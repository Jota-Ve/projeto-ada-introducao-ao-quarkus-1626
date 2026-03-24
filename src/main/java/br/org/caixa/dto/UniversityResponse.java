package br.org.caixa.dto;

import br.org.caixa.model.Course;

import java.util.List;

public record UniversityResponse(Long id, String abbreviation, String name, String city, String state, String country, List<Course> courses) {
}
