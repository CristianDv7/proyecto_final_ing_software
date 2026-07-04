# language: es
Característica: Registro del local en una sola pantalla
  Como comerciante local sin perfil previo
  Quiero registrar mi negocio en una sola operación
  Para aparecer en las búsquedas de consumidores cercanos

  Escenario: Registro exitoso con los datos obligatorios completos
    Dado un comerciante sin perfil previo
    Cuando registra su local con los datos obligatorios completos
    Entonces el local queda publicado con código 201

  Escenario: La ubicación por GPS se registra sin escribir dirección
    Dado un comerciante sin perfil previo
    Cuando registra su local con la ubicación detectada por GPS
    Entonces el local queda publicado con código 201
    Y el perfil expone el origen de ubicación "GPS"

  Escenario: El número de WhatsApp queda disponible como contacto
    Dado un comerciante sin perfil previo
    Cuando registra su local con el WhatsApp "+593987650001"
    Entonces el local queda publicado con código 201
    Y el perfil expone el WhatsApp "+593987650001"

  Escenario: Un formulario incompleto informa los campos obligatorios faltantes
    Dado un comerciante sin perfil previo
    Cuando intenta registrar su local sin WhatsApp ni horarios
    Entonces el registro es rechazado con código 422
    Y la respuesta informa campos obligatorios faltantes

  Escenario: Se publica el perfil solo con los campos obligatorios (servicios opcionales)
    Dado un comerciante sin perfil previo
    Cuando registra su local sin servicios
    Entonces el local queda publicado con código 201
    Y el perfil no expone servicios

  Escenario: Un local duplicado es rechazado
    Dado un local ya registrado con WhatsApp "+593987650002"
    Cuando otro registro usa el mismo WhatsApp y la misma ubicación
    Entonces el registro es rechazado con código 409

  Escenario: El mismo comerciante puede registrar otro local en distinta ubicación
    Dado un local ya registrado con WhatsApp "+593987650003"
    Cuando el mismo comerciante registra otro local en distinta ubicación
    Entonces el local queda publicado con código 201

  Escenario: Un WhatsApp con formato inválido es rechazado
    Dado un comerciante sin perfil previo
    Cuando registra su local con un WhatsApp con formato inválido
    Entonces el registro es rechazado con código 422

  Escenario: Un horario incoherente es rechazado
    Dado un comerciante sin perfil previo
    Cuando registra su local con un horario cuyo cierre es anterior a la apertura
    Entonces el registro es rechazado con código 422

  Escenario: Un tipo de negocio inexistente es rechazado
    Dado un comerciante sin perfil previo
    Cuando registra su local con un tipo de negocio inexistente
    Entonces el registro es rechazado con código 422
