package br.org.caixa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLessonRequest(@NotNull @NotBlank String name) {
}
