package com.cafemetrix.cafelab.profiles.domain.exceptions;

/**
 * Lanzada cuando se intenta modificar un perfil con un valor único que otro perfil ya está usando
 * (por ahora email, name o cafeteriaName). El campo afectado se entrega aparte para que el
 * frontend lo pueda mostrar en el sitio correcto del modal.
 */
public class ProfileFieldInUseException extends RuntimeException {

    public enum Field {
        EMAIL("email"),
        NAME("name"),
        CAFETERIA_NAME("cafeteriaName");

        private final String wireName;

        Field(String wireName) {
            this.wireName = wireName;
        }

        /** Nombre tal como viaja en JSON al frontend. */
        public String wireName() {
            return wireName;
        }
    }

    private final Field field;

    public ProfileFieldInUseException(Field field, String message) {
        super(message);
        this.field = field;
    }

    public Field field() {
        return field;
    }
}
