# US07: Gestión de Recetas de Preparación
# Como barista profesional,
# quiero crear recetas de preparación personalizadas
# para documentar y replicar mis mejores preparaciones.

Feature: US07 - Gestión de Recetas de Preparación

  Scenario: Barista autenticado crea una receta exitosamente
    Given un barista autenticado con perfil id 1 y receta lista para crear
    When envía una solicitud para crear una receta con nombre "Espresso Etiopia" metodo "espresso" ratio "1:2" tiempo 25
    Then el sistema responde con código 201
    And la respuesta contiene el id de la receta creada

  Scenario: Usuario no autenticado intenta crear una receta
    Given un usuario no autenticado
    When envía una solicitud para crear una receta con nombre "Espresso Etiopia" metodo "espresso" ratio "1:2" tiempo 25
    Then el sistema responde con código 401

  Scenario: Creación de receta falla por error interno
    Given un barista autenticado con perfil id 1 pero creación de receta falla
    When envía una solicitud para crear una receta con nombre "Espresso Etiopia" metodo "espresso" ratio "1:2" tiempo 25
    Then el sistema responde con código 400
