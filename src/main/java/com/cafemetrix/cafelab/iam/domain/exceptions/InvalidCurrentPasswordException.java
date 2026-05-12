package com.cafemetrix.cafelab.iam.domain.exceptions;

/**
 * Lanzada por el cambio de contraseña cuando la contraseña actual provista por el usuario no
 * coincide con la almacenada. Se mapea a un 400 (entrada incorrecta del usuario, no 401 de sesión
 * caducada).
 */
public class InvalidCurrentPasswordException extends RuntimeException {
    public InvalidCurrentPasswordException() {
        super("La contraseña actual no es válida");
    }
}
