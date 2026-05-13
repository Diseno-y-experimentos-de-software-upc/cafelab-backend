# US03: Creación de Perfil de Tueste
# Como barista profesional,
# quiero crear perfiles de tueste personalizados
# para documentar y replicar mis mejores resultados.

Feature: US03 - Creación de Perfil de Tueste

  Scenario: Barista autenticado crea un perfil de tueste exitosamente
    Given un barista autenticado con perfil id 1 y lote id 1 disponible
    When envía una solicitud para crear un perfil con nombre "Etiopía Natural" tipo "Light" duración 12 tempInicio 180.0 tempFin 195.0
    Then el sistema responde con código 201
    And la respuesta contiene el id del perfil de tueste creado

  Scenario: Usuario no autenticado intenta crear un perfil de tueste
    Given un usuario no autenticado
    When envía una solicitud para crear un perfil con nombre "Etiopía Natural" tipo "Light" duración 12 tempInicio 180.0 tempFin 195.0
    Then el sistema responde con código 401

  Scenario: Barista intenta crear un perfil con un lote que no le pertenece
    Given un barista autenticado con perfil id 1 pero sin acceso al lote id 99
    When envía una solicitud para crear un perfil con nombre "Etiopía Natural" tipo "Light" duración 12 tempInicio 180.0 tempFin 195.0
    Then el sistema responde con código 403
