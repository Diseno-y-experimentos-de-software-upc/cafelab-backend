# US02: Gestión de Lotes de Café
# Como barista profesional,
# quiero registrar lotes de café con su información de origen
# para rastrear la procedencia de cada preparación.

Feature: US02 - Gestión de Lotes de Café

  Scenario: Barista autenticado registra un lote de café exitosamente
    Given un barista autenticado con perfil id 1 y proveedor id 1 disponible para lote
    When envía una solicitud para crear un lote con nombre "Lote Etiopia" tipo "Arabica" metodo "Natural" altitud 1800 peso 50.0 origen "Etiopia" estado "Disponible"
    Then el sistema responde con código 201
    And la respuesta contiene el id del lote creado

  Scenario: Usuario no autenticado intenta registrar un lote
    Given un usuario no autenticado
    When envía una solicitud para crear un lote con nombre "Lote Etiopia" tipo "Arabica" metodo "Natural" altitud 1800 peso 50.0 origen "Etiopia" estado "Disponible"
    Then el sistema responde con código 401

  Scenario: Barista intenta registrar un lote con proveedor que no le pertenece
    Given un barista autenticado con perfil id 1 pero proveedor id 99 no le pertenece
    When envía una solicitud para crear un lote con nombre "Lote Etiopia" tipo "Arabica" metodo "Natural" altitud 1800 peso 50.0 origen "Etiopia" estado "Disponible"
    Then el sistema responde con código 403
