# US13: Gestión de Costos de Producción
# Como barista profesional,
# quiero calcular y registrar los costos de producción por lote
# para determinar el precio de venta adecuado.

Feature: US13 - Gestión de Costos de Producción

  Scenario: Barista autenticado registra un costo de producción exitosamente
    Given un barista autenticado con perfil id 1 y lote id 1 disponible para costos
    When envía una solicitud para crear un registro de costo con lote 1 moneda "PEN" kg 50.0 materia 100.0 mano 50.0 transporte 20.0 almacenamiento 10.0 procesamiento 30.0 otros 5.0
    Then el sistema responde con código 201
    And la respuesta contiene el id del registro de costo creado

  Scenario: Usuario no autenticado intenta registrar costos de producción
    Given un usuario no autenticado
    When envía una solicitud para crear un registro de costo con lote 1 moneda "PEN" kg 50.0 materia 100.0 mano 50.0 transporte 20.0 almacenamiento 10.0 procesamiento 30.0 otros 5.0
    Then el sistema responde con código 401

  Scenario: Barista intenta registrar costos con un lote que no le pertenece
    Given un barista autenticado con perfil id 1 pero lote id 99 no le pertenece para costos
    When envía una solicitud para crear un registro de costo con lote 99 moneda "PEN" kg 50.0 materia 100.0 mano 50.0 transporte 20.0 almacenamiento 10.0 procesamiento 30.0 otros 5.0
    Then el sistema responde con código 403
