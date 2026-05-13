# US06: Historial de Catas
# Como barista profesional,
# quiero consultar el historial de mis sesiones de cata
# para analizar la evolución de mis evaluaciones sensoriales.

Feature: US06 - Historial de Catas

  Scenario: Barista autenticado consulta su historial de catas
    Given un barista autenticado con perfil id 1 y 2 catas registradas
    When envía una solicitud para listar sus sesiones de cata
    Then el sistema responde con código 200
    And la respuesta contiene una lista con 2 sesiones

  Scenario: Barista sin catas registradas consulta historial vacío
    Given un barista autenticado con perfil id 1 y 0 catas registradas
    When envía una solicitud para listar sus sesiones de cata
    Then el sistema responde con código 200
    And la respuesta contiene una lista con 0 sesiones

  Scenario: Usuario no autenticado intenta consultar el historial de catas
    Given un usuario no autenticado
    When envía una solicitud para listar sus sesiones de cata
    Then el sistema responde con código 401
