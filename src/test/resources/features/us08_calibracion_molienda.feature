# US08: Calibración de Molienda
# Como barista profesional,
# quiero registrar calibraciones de molienda
# para optimizar la extracción según el equipo y método utilizado.

Feature: US08 - Calibración de Molienda

  Scenario: Barista autenticado registra una calibración exitosamente
    Given un barista autenticado con perfil id 1 y calibración lista para crear
    When envía una solicitud para crear una calibración con nombre "Calibración Espresso" metodo "espresso" equipo "La Marzocco" numero "15"
    Then el sistema responde con código 201
    And la respuesta contiene el id de la calibración creada

  Scenario: Usuario no autenticado intenta registrar una calibración
    Given un usuario no autenticado
    When envía una solicitud para crear una calibración con nombre "Calibración Espresso" metodo "espresso" equipo "La Marzocco" numero "15"
    Then el sistema responde con código 401

  Scenario: Registro de calibración falla por datos inválidos
    Given un barista autenticado con perfil id 1 pero creación de calibración falla
    When envía una solicitud para crear una calibración con nombre "Calibración Espresso" metodo "espresso" equipo "La Marzocco" numero "15"
    Then el sistema responde con código 400
