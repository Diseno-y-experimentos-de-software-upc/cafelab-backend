# US01b: Ficha de proveedor ampliada (TUS01)
# Como dueño/admin de cafetería de especialidad,
# quiero registrar la persona de contacto y el enlace web del proveedor
# para tener una ficha más completa y poder contactarlo y verificar su sitio.

Feature: US01b - Ficha de proveedor ampliada

  Scenario: Se guarda la persona de contacto del proveedor
    Given un barista autenticado con perfil id 1 y proveedor ampliado listo para crear
    When envía una solicitud para crear un proveedor ampliado con contacto "Maria Lopez" y enlace web "https://hacienda.com"
    Then el sistema responde con código 201
    And la respuesta del proveedor contiene contacto "Maria Lopez"

  Scenario: El enlace web válido se almacena y se muestra en el detalle
    Given un barista autenticado con perfil id 1 y proveedor ampliado existente con id 20
    When consulta el detalle del proveedor con id 20
    Then el sistema responde con código 200
    And la respuesta del proveedor contiene enlace web "https://hacienda.com"

  Scenario: Rechaza un enlace web con formato inválido
    Given un barista autenticado con perfil id 1 y proveedor ampliado listo para crear
    When envía una solicitud para crear un proveedor ampliado con contacto "Maria Lopez" y enlace web "no-es-una-url"
    Then el sistema responde con código 400
