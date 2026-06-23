# US01: Gestión de Proveedores
# Como barista profesional,
# quiero registrar y gestionar mis proveedores de café
# para mantener un control de mis fuentes de materia prima.

Feature: US01 - Gestión de Proveedores

  Scenario: Barista autenticado registra un proveedor exitosamente
    Given un barista autenticado con perfil id 1 y proveedor listo para crear
    When envía una solicitud para crear un proveedor con nombre "Hacienda Colombia" email "proveedor@cafelab.com" telefono 1234567890 ubicacion "Huila Colombia"
    Then el sistema responde con código 201
    And la respuesta contiene el id del proveedor creado

  Scenario: Usuario no autenticado intenta registrar un proveedor
    Given un usuario no autenticado
    When envía una solicitud para crear un proveedor con nombre "Hacienda Colombia" email "proveedor@cafelab.com" telefono 1234567890 ubicacion "Huila Colombia"
    Then el sistema responde con código 401

  Scenario: Creación de proveedor falla por error interno
    Given un barista autenticado con perfil id 1 pero creación de proveedor falla
    When envía una solicitud para crear un proveedor con nombre "Hacienda Colombia" email "proveedor@cafelab.com" telefono 1234567890 ubicacion "Huila Colombia"
    Then el sistema responde con código 400
