# US05: Cata Digital Estructurada
# Como barista profesional,
# quiero registrar evaluaciones sensoriales estructuradas
# para documentar las características de cada lote y tueste.

Feature: US05 - Cata Digital Estructurada

  Scenario: Barista autenticado registra una sesión de cata exitosamente
    Given un barista autenticado con perfil id 1
    When envía una solicitud para crear una cata con nombre "Cata Etiopía Yirgacheffe" origen "Etiopía" variedad "Heirloom" procesamiento "Natural" fecha "2026-05-12"
    Then el sistema responde con código 201
    And la respuesta contiene el id de la sesión de cata

  Scenario: Barista consulta su historial de catas
    Given un barista autenticado con perfil id 1 y 2 catas registradas
    When envía una solicitud para listar sus sesiones de cata
    Then el sistema responde con código 200
    And la respuesta contiene una lista con 2 sesiones

  Scenario: Usuario no autenticado intenta registrar una cata
    Given un usuario no autenticado
    When envía una solicitud para crear una cata con nombre "Cata Etiopía Yirgacheffe" origen "Etiopía" variedad "Heirloom" procesamiento "Natural" fecha "2026-05-12"
    Then el sistema responde con código 401
