# US09: Portafolio de Bebidas
# Como barista profesional,
# quiero organizar mis recetas en portafolios
# para categorizar y presentar mis preparaciones.

Feature: US09 - Portafolio de Bebidas

  Scenario: Barista autenticado crea un portafolio exitosamente
    Given un barista autenticado con perfil id 1 y portafolio listo para crear
    When envía una solicitud para crear un portafolio con nombre "Espressos Clásicos"
    Then el sistema responde con código 201
    And la respuesta contiene el id del portafolio creado

  Scenario: Usuario no autenticado intenta crear un portafolio
    Given un usuario no autenticado
    When envía una solicitud para crear un portafolio con nombre "Espressos Clásicos"
    Then el sistema responde con código 401

  Scenario: Creación de portafolio falla por error interno
    Given un barista autenticado con perfil id 1 pero creación de portafolio falla
    When envía una solicitud para crear un portafolio con nombre "Espressos Clásicos"
    Then el sistema responde con código 400
