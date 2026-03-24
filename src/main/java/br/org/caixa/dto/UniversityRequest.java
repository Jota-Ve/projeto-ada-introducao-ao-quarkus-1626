package br.org.caixa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UniversityRequest(
        @NotNull @NotBlank @Size(min = 3, max = 5) String abbreviation,
        @NotNull @NotBlank @Size(min = 10, max = 100) String name,
        @NotNull @NotBlank @Size(min = 3, max = 100) String city,
        @NotNull @NotBlank @Size(min = 3, max = 100) String state,
        @NotNull @NotBlank @Size(min = 3, max = 100) String country) {
}
