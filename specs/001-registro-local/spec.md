# Feature Specification: Registro del local en una sola pantalla

**Feature Branch**: `001-registro-local`

**Created**: 2026-07-04

**Status**: Draft

**Input**: User description: "US-01 · Registro del local en una sola pantalla · épica E-01 · 5 pts — Como comerciante local sin perfil previo, quiero registrar mi negocio (nombre, tipo, ubicación, horario y servicios) en una sola pantalla desde el teléfono en menos de 5 minutos, para aparecer en las búsquedas de consumidores cercanos sin gestionar redes sociales ni detener la atención al cliente. Origen: us:US-01, req:R-01, req:R-09, req:R-12, pain:invisibilidad-geografica, pain:registro-complejo"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registro rápido del local en una sola pantalla (Priority: P1)

Como comerciante local sin perfil previo en la plataforma, quiero registrar mi negocio
con nombre, tipo, ubicación, horario y servicios en una sola pantalla desde el teléfono,
en menos de 5 minutos, para aparecer en las búsquedas de consumidores cercanos sin tener
que gestionar redes sociales ni detener la atención al cliente.

**Why this priority**: Es la puerta de entrada de todo comerciante a la plataforma.
Sin un registro simple y rápido no existe oferta que los consumidores puedan descubrir;
ataca directamente los dolores de invisibilidad geográfica y registro complejo. Entrega
valor por sí sola: un local registrado ya es descubrible.

**Independent Test**: Puede probarse completamente registrando un local nuevo desde un
teléfono, completando el formulario de una sola pantalla y verificando que el perfil
queda publicado y visible en búsquedas cercanas en menos de 5 minutos.

**Acceptance Scenarios**:

1. **Given** que soy un comerciante sin perfil previo, **When** completo el formulario de
   registro (máx. 10 campos en una sola pantalla), **Then** mi local queda visible en
   búsquedas en menos de 5 minutos.
2. **Given** que ingreso mi ubicación, **When** elijo la opción GPS, **Then** el sistema
   detecta mi posición sin que yo escriba una dirección.
3. **Given** que ingreso un número de WhatsApp durante el registro, **When** un consumidor
   encuentra mi perfil, **Then** puede contactarme directamente desde ese número sin pasos
   adicionales.
4. **Given** que el formulario está incompleto en algún campo obligatorio, **When** intento
   guardar, **Then** el sistema indica qué campos son obligatorios (nombre, tipo de negocio,
   ubicación, número de WhatsApp y al menos un horario) antes de publicar el perfil.
5. **Given** que completo solo los campos obligatorios (nombre, tipo de negocio, ubicación,
   número de WhatsApp y al menos un horario), **When** guardo el formulario, **Then** el
   perfil se publica con esos datos y el resto de campos permanece como opcional.

---

### Edge Cases

- **GPS no disponible o denegado**: Cuando el comerciante rechaza el permiso de ubicación
  o el GPS no obtiene señal, el sistema DEBE permitir ingresar/ajustar la ubicación de
  forma manual sin bloquear el registro.
- **Ubicación imprecisa**: Cuando la posición GPS detectada es aproximada, el comerciante
  DEBE poder confirmar o corregir el punto antes de publicar.
- **Número de WhatsApp con formato inválido**: Cuando el número no tiene un formato de
  teléfono válido, el sistema DEBE señalar el campo antes de publicar.
- **Horario incoherente**: Cuando la hora de cierre es anterior a la de apertura sin cruce
  de medianoche declarado, el sistema DEBE señalar el horario como inválido.
- **Registro duplicado**: Cuando ya existe un local con el mismo número de WhatsApp y
  ubicación equivalente, el sistema DEBE advertir el posible duplicado antes de publicar.
- **Conexión intermitente al guardar**: Cuando se pierde la conexión durante el guardado,
  el sistema DEBE preservar los datos ingresados y permitir reintentar sin recomenzar.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST presentar el registro del local en una sola pantalla con un
  máximo de 10 campos.
- **FR-002**: El sistema MUST tratar como obligatorios los campos: nombre, tipo de negocio,
  ubicación, número de WhatsApp y al menos un horario de atención.
- **FR-003**: El sistema MUST tratar como opcionales los campos restantes (p. ej. servicios
  y demás datos complementarios), permitiendo publicar el perfil sin ellos.
- **FR-004**: El sistema MUST validar, antes de publicar, que todos los campos obligatorios
  estén completos e indicar explícitamente cuáles faltan cuando el formulario esté incompleto.
- **FR-005**: El sistema MUST ofrecer una opción de detección de ubicación por GPS que fije
  la posición del local sin requerir que el comerciante escriba una dirección.
- **FR-006**: El sistema MUST permitir ingresar o ajustar la ubicación manualmente cuando el
  GPS no esté disponible o sea rechazado.
- **FR-007**: El sistema MUST capturar un número de WhatsApp y exponerlo en el perfil de modo
  que un consumidor pueda contactar al comercio directamente por ese número sin pasos adicionales.
- **FR-008**: El sistema MUST publicar el perfil del local y hacerlo visible en las búsquedas
  de consumidores cercanos una vez guardado con los campos obligatorios completos.
- **FR-009**: El sistema MUST completar el flujo de registro (desde inicio hasta perfil visible)
  en menos de 5 minutos para un comerciante sin perfil previo.
- **FR-010**: El sistema MUST validar el formato del número de WhatsApp y la coherencia del
  horario (apertura/cierre) antes de publicar.
- **FR-011**: El sistema MUST permitir registrar el local sin requerir cuentas de redes sociales.
- **FR-012**: El sistema MUST preservar los datos ya ingresados ante un error o interrupción al
  guardar, permitiendo reintentar sin recomenzar el formulario.

### Key Entities *(include if feature involves data)*

- **Local (Negocio)**: Representa el comercio registrado. Atributos clave: nombre, tipo de
  negocio, ubicación (coordenadas y/o dirección), número de WhatsApp, horarios de atención,
  servicios (opcional), estado de publicación (publicado/borrador).
- **Comerciante**: Persona que registra y es propietaria del perfil del local. Se relaciona con
  uno o más locales; en el alcance de esta historia crea su primer local sin perfil previo.
- **Horario de atención**: Franja de disponibilidad del local (día, hora de apertura, hora de
  cierre). Un local tiene al menos uno.
- **Ubicación**: Punto geográfico del local, obtenido por GPS o ingresado/ajustado manualmente.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 90% de los comerciantes sin perfil previo completa el registro y obtiene un
  perfil visible en búsquedas en menos de 5 minutos.
- **SC-002**: El 100% de los locales publicados con datos obligatorios completos aparece en las
  búsquedas de consumidores cercanos.
- **SC-003**: Al menos el 80% de los registros usa la detección de ubicación por GPS sin que el
  comerciante escriba una dirección.
- **SC-004**: El 100% de los perfiles publicados incluye un número de WhatsApp de contacto
  funcional accesible por el consumidor en un solo paso.
- **SC-005**: Cuando el formulario está incompleto, el 100% de los intentos de guardar informa
  con claridad los campos obligatorios faltantes antes de publicar.
- **SC-006**: El registro se completa en una sola pantalla, sin pasos ni cuentas de redes
  sociales adicionales, en el 100% de los casos.

## Assumptions

- El registro se realiza desde un dispositivo móvil con capacidad de GPS y acceso a internet.
- "Visible en búsquedas en menos de 5 minutos" mide el tiempo total del flujo de registro y la
  disponibilidad del perfil, no un procesamiento diferido de horas.
- El número de WhatsApp provisto por el comerciante es el canal de contacto principal expuesto
  al consumidor.
- El catálogo de "tipo de negocio" se selecciona de un conjunto predefinido para mantener el
  formulario dentro del límite de 10 campos y facilitar la búsqueda.
- El límite de 10 campos incluye tanto obligatorios como opcionales mostrados en la pantalla.
- La búsqueda por cercanía de consumidores ya existe o se provee como capacidad de la plataforma;
  esta historia se limita a producir un perfil descubrible por ella.
