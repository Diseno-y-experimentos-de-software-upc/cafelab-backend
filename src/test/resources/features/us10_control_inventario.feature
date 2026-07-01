# US10: Control de Inventario
# Como barista profesional,
# quiero registrar el consumo de mis lotes de café
# para mantener un control preciso del inventario disponible.

Feature: US10 - Control de Inventario

  Scenario: Barista autenticado registra una entrada de inventario exitosamente
    Given un barista autenticado con perfil id 1 y lote id 1 disponible para inventario
    When envia una solicitud para crear una entrada de inventario con lote 1 cantidad 10.0 reason "bar" notes "Use for bar drinks"
    Then el sistema responde con código 201
    And la respuesta contiene el id de la entrada de inventario creada

  Scenario: Usuario no autenticado intenta registrar una entrada de inventario
    Given un usuario no autenticado
    When envia una solicitud para crear una entrada de inventario con lote 1 cantidad 10.0 reason "bar" notes "Use for bar drinks"
    Then el sistema responde con código 401

  Scenario: Barista intenta registrar consumo de un lote que no le pertenece
    Given un barista autenticado con perfil id 1 pero lote id 99 no le pertenece para inventario
    When envia una solicitud para crear una entrada de inventario con lote 99 cantidad 10.0 reason "bar" notes "Use for bar drinks"
    Then el sistema responde con código 403
