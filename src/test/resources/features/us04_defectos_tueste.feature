# US04: Registro de Defectos de Tueste
# Como barista profesional,
# quiero registrar defectos detectados en el tueste
# para mejorar la calidad del proceso.

Feature: US04 - Registro de Defectos de Tueste

  Scenario: Barista autenticado registra un defecto exitosamente
    Given un barista autenticado con perfil id 1 y defecto listo para crear
    When envía una solicitud para crear un defecto de café "Etiopia Yirgacheffe" tipo "Quakers" peso 2.5 porcentaje 5.0
    Then el sistema responde con código 201
    And la respuesta contiene el id del defecto creado

  Scenario: Usuario no autenticado intenta registrar un defecto
    Given un usuario no autenticado
    When envía una solicitud para crear un defecto de café "Etiopia Yirgacheffe" tipo "Quakers" peso 2.5 porcentaje 5.0
    Then el sistema responde con código 401

  Scenario: Registro de defecto falla por datos inválidos
    Given un barista autenticado con perfil id 1 pero creación de defecto falla
    When envía una solicitud para crear un defecto de café "Etiopia Yirgacheffe" tipo "Quakers" peso 2.5 porcentaje 5.0
    Then el sistema responde con código 400
