package com.cafemetrix.cafelab.defects.domain;

import com.cafemetrix.cafelab.defects.domain.model.aggregates.Defect;
import com.cafemetrix.cafelab.defects.domain.model.commands.CreateDefectCommand;
import com.cafemetrix.cafelab.defects.domain.model.valueobjects.DefectName;
import com.cafemetrix.cafelab.defects.domain.model.valueobjects.DefectType;
import com.cafemetrix.cafelab.defects.domain.model.valueobjects.ProbableCause;
import com.cafemetrix.cafelab.defects.domain.model.valueobjects.SuggestedSolution;
import com.cafemetrix.cafelab.defects.interfaces.rest.resources.CreateDefectResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Feature: Librería de defectos — modelo de dominio")
class DefectDomainBDDTest {

    // ─── DefectName ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Scenario: Creación de DefectName")
    class DefectNameBDD {

        @Test
        @DisplayName("Given un valor válido, When se crea DefectName, Then almacena el valor correctamente")
        void givenValidValue_whenCreate_thenValueStored() {
            var defectName = new DefectName("Grano negro");
            assertThat(defectName.value()).isEqualTo("Grano negro");
        }

        @Test
        @DisplayName("Given valor nulo, When se crea DefectName, Then lanza IllegalArgumentException")
        void givenNullValue_whenCreate_thenThrows() {
            assertThatThrownBy(() -> new DefectName(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nombre del defecto");
        }

        @Test
        @DisplayName("Given valor en blanco, When se crea DefectName, Then lanza IllegalArgumentException")
        void givenBlankValue_whenCreate_thenThrows() {
            assertThatThrownBy(() -> new DefectName("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nombre del defecto");
        }
    }

    // ─── DefectType ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Scenario: Creación de DefectType")
    class DefectTypeBDD {

        @Test
        @DisplayName("Given un tipo válido, When se crea DefectType, Then almacena el valor correctamente")
        void givenValidValue_whenCreate_thenValueStored() {
            var defectType = new DefectType("Categoría 1");
            assertThat(defectType.value()).isEqualTo("Categoría 1");
        }

        @Test
        @DisplayName("Given valor nulo, When se crea DefectType, Then lanza IllegalArgumentException")
        void givenNullValue_whenCreate_thenThrows() {
            assertThatThrownBy(() -> new DefectType(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tipo de defecto");
        }

        @Test
        @DisplayName("Given valor en blanco, When se crea DefectType, Then lanza IllegalArgumentException")
        void givenBlankValue_whenCreate_thenThrows() {
            assertThatThrownBy(() -> new DefectType(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tipo de defecto");
        }
    }

    // ─── ProbableCause ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Scenario: Creación de ProbableCause")
    class ProbableCauseBDD {

        @Test
        @DisplayName("Given una causa válida, When se crea ProbableCause, Then almacena el valor correctamente")
        void givenValidValue_whenCreate_thenValueStored() {
            var cause = new ProbableCause("Temperatura excesiva durante el tueste");
            assertThat(cause.value()).isEqualTo("Temperatura excesiva durante el tueste");
        }

        @Test
        @DisplayName("Given valor nulo, When se crea ProbableCause, Then lanza IllegalArgumentException")
        void givenNullValue_whenCreate_thenThrows() {
            assertThatThrownBy(() -> new ProbableCause(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("causa probable");
        }

        @Test
        @DisplayName("Given valor en blanco, When se crea ProbableCause, Then lanza IllegalArgumentException")
        void givenBlankValue_whenCreate_thenThrows() {
            assertThatThrownBy(() -> new ProbableCause("  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("causa probable");
        }
    }

    // ─── SuggestedSolution ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Scenario: Creación de SuggestedSolution")
    class SuggestedSolutionBDD {

        @Test
        @DisplayName("Given una solución válida, When se crea SuggestedSolution, Then almacena el valor correctamente")
        void givenValidValue_whenCreate_thenValueStored() {
            var solution = new SuggestedSolution("Reducir temperatura a 190°C");
            assertThat(solution.value()).isEqualTo("Reducir temperatura a 190°C");
        }

        @Test
        @DisplayName("Given valor nulo, When se crea SuggestedSolution, Then lanza IllegalArgumentException")
        void givenNullValue_whenCreate_thenThrows() {
            assertThatThrownBy(() -> new SuggestedSolution(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("solución sugerida");
        }

        @Test
        @DisplayName("Given valor en blanco, When se crea SuggestedSolution, Then lanza IllegalArgumentException")
        void givenBlankValue_whenCreate_thenThrows() {
            assertThatThrownBy(() -> new SuggestedSolution(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("solución sugerida");
        }
    }

    // ─── CreateDefectResource ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Scenario: Validación de CreateDefectResource")
    class CreateDefectResourceBDD {

        @Test
        @DisplayName("Given todos los campos válidos, When se construye el recurso, Then no lanza excepción")
        void givenValidFields_whenBuild_thenNoException() {
            assertThatNoException().isThrownBy(() ->
                new CreateDefectResource(
                    "Café Etiopía", "Yirgacheffe", "Heirloom",
                    500.0, "Grano negro", "Categoría 1",
                    25.0, 5.0, "Temperatura excesiva", "Reducir temperatura"
                )
            );
        }

        @Test
        @DisplayName("Given nombre de café nulo, When se construye el recurso, Then lanza IllegalArgumentException")
        void givenNullCoffeeName_whenBuild_thenThrows() {
            assertThatThrownBy(() ->
                new CreateDefectResource(
                    null, "Yirgacheffe", "Heirloom",
                    500.0, "Grano negro", "Categoría 1",
                    25.0, 5.0, "Temperatura excesiva", "Reducir temperatura"
                )
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("nombre del café");
        }

        @Test
        @DisplayName("Given peso del defecto negativo, When se construye el recurso, Then lanza IllegalArgumentException")
        void givenNegativeDefectWeight_whenBuild_thenThrows() {
            assertThatThrownBy(() ->
                new CreateDefectResource(
                    "Café Etiopía", "Yirgacheffe", "Heirloom",
                    500.0, "Grano negro", "Categoría 1",
                    -1.0, 5.0, "Temperatura excesiva", "Reducir temperatura"
                )
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("peso del defecto");
        }

        @Test
        @DisplayName("Given porcentaje mayor a 100, When se construye el recurso, Then lanza IllegalArgumentException")
        void givenPercentageOver100_whenBuild_thenThrows() {
            assertThatThrownBy(() ->
                new CreateDefectResource(
                    "Café Etiopía", "Yirgacheffe", "Heirloom",
                    500.0, "Grano negro", "Categoría 1",
                    25.0, 150.0, "Temperatura excesiva", "Reducir temperatura"
                )
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("porcentaje");
        }

        @Test
        @DisplayName("Given porcentaje negativo, When se construye el recurso, Then lanza IllegalArgumentException")
        void givenNegativePercentage_whenBuild_thenThrows() {
            assertThatThrownBy(() ->
                new CreateDefectResource(
                    "Café Etiopía", "Yirgacheffe", "Heirloom",
                    500.0, "Grano negro", "Categoría 1",
                    25.0, -1.0, "Temperatura excesiva", "Reducir temperatura"
                )
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("porcentaje");
        }
    }

    // ─── Defect aggregate ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("Scenario: Creación del agregado Defect")
    class DefectAggregateBDD {

        @Test
        @DisplayName("Given un CreateDefectCommand válido, When se crea Defect, Then todos los campos son mapeados correctamente")
        void givenValidCommand_whenCreate_thenFieldsMappedCorrectly() {
            var command = new CreateDefectCommand(
                1L, "Café Etiopía", "Yirgacheffe", "Heirloom",
                500.0, "Grano negro", "Categoría 1",
                25.0, 5.0, "Temperatura excesiva", "Reducir temperatura"
            );

            var defect = new Defect(command);

            assertThat(defect.getUserId()).isEqualTo(1L);
            assertThat(defect.getCoffeeDisplayName()).isEqualTo("Café Etiopía");
            assertThat(defect.getCoffeeRegion()).isEqualTo("Yirgacheffe");
            assertThat(defect.getCoffeeVariety()).isEqualTo("Heirloom");
            assertThat(defect.getCoffeeTotalWeight()).isEqualTo(500.0);
            assertThat(defect.getName()).isEqualTo("Grano negro");
            assertThat(defect.getDefectType()).isEqualTo("Categoría 1");
            assertThat(defect.getDefectWeight()).isEqualTo(25.0);
            assertThat(defect.getPercentage()).isEqualTo(5.0);
            assertThat(defect.getProbableCause()).isEqualTo("Temperatura excesiva");
            assertThat(defect.getSuggestedSolution()).isEqualTo("Reducir temperatura");
        }

        @Test
        @DisplayName("Given coffeeDisplayName con espacios, When se crea Defect, Then se aplica trim")
        void givenDisplayNameWithSpaces_whenCreate_thenTrimApplied() {
            var command = new CreateDefectCommand(
                1L, "  Café Etiopía  ", null, null,
                null, "Grano negro", "Categoría 1",
                25.0, 5.0, "Causa", "Solución"
            );

            var defect = new Defect(command);

            assertThat(defect.getCoffeeDisplayName()).isEqualTo("Café Etiopía");
        }
    }
}
