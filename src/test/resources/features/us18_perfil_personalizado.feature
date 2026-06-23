# US18: Perfil Personalizado
# Como usuario nuevo,
# quiero crear mi perfil personalizado en la plataforma
# para acceder a todas las funcionalidades según mi rol.

Feature: US18 - Perfil Personalizado

  Scenario: Usuario crea su perfil exitosamente
    Given un perfil listo para crear con rol barista
    When envía una solicitud para crear un perfil con nombre "Juan Barista" email "juan@cafelab.com" rol "barista"
    Then el sistema responde con código 201
    And la respuesta contiene el id del perfil creado

  Scenario: Creación de perfil falla por error interno
    Given un perfil que falla al crearse
    When envía una solicitud para crear un perfil con nombre "Juan Barista" email "juan@cafelab.com" rol "barista"
    Then el sistema responde con código 400

  Scenario: Creación de perfil falla por rol inválido
    Given un perfil que falla al crearse
    When envía una solicitud para crear un perfil con nombre "Juan Barista" email "juan@cafelab.com" rol "admin"
    Then el sistema responde con código 400
