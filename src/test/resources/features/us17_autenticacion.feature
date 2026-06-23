# US17: Registro y Autenticación (Usuarios)
# Como barista profesional o dueño de cafetería de especialidad,
# quiero registrarme y acceder de forma segura
# para mantener la confidencialidad de mis datos.

Feature: US17 - Registro y Autenticación de Usuarios

  Scenario: Registro exitoso de un nuevo usuario
    Given un usuario con email "barista@cafelab.com" y contraseña "Password123"
    When el usuario envía una solicitud de registro
    Then el sistema responde con código 201
    And la respuesta contiene un token de acceso

  Scenario: Inicio de sesión exitoso con credenciales válidas
    Given un usuario registrado con email "barista@cafelab.com" y contraseña "Password123"
    When el usuario envía una solicitud de inicio de sesión
    Then el sistema responde con código 200
    And la respuesta contiene un token de acceso

  Scenario: Inicio de sesión fallido con credenciales inválidas
    Given un usuario no registrado con email "noexiste@cafelab.com" y contraseña "wrongpass"
    When el usuario envía una solicitud de inicio de sesión
    Then el sistema responde con código 404
