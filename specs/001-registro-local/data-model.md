# Phase 1 Data Model: Registro del local en una sola pantalla

**Feature**: 001-registro-local | **Date**: 2026-07-04

Modelo derivado de las entidades clave y requisitos de la spec. Las reglas de validación
provienen de FR-002, FR-004, FR-010 y de los edge cases. El modelo de dominio es agnóstico
de frameworks; el mapeo a tablas se materializa en `src/main/resources/db/schema/schema.sql`.

## Entidades

### Local (Negocio)

Comercio registrado y publicado para búsquedas.

| Campo | Tipo | Obligatorio | Reglas |
|-------|------|-------------|--------|
| id | UUID | Sí (generado) | Identificador único |
| nombre | String | Sí | No vacío; 2–120 caracteres |
| tipoNegocioId | ref → TipoNegocio | Sí | Debe existir en el catálogo precargado |
| ubicacion | Ubicacion (embebida) | Sí | Ver reglas de Ubicacion |
| whatsapp | String | Sí | Formato E.164 (`+` y 8–15 dígitos). Contacto del local; en el MVP coincide con el WhatsApp del Comerciante |
| horarios | List<HorarioAtencion> | Sí | Al menos 1 elemento |
| servicios | List<Servicio> | No | Opcional; puede estar vacío |
| estadoPublicacion | Enum {BORRADOR, PUBLICADO} | Sí | Pasa a PUBLICADO al guardar válido |
| comercianteId | ref → Comerciante | Sí | Propietario del local |
| fechaCreacion | Instant | Sí (generado) | Marca de alta |

**Reglas de negocio**:
- Para publicar, todos los campos obligatorios deben estar completos (FR-002, FR-004).
- Un WhatsApp + ubicación equivalente ya existente marca posible duplicado (edge case).

### Comerciante

Persona propietaria del/los local(es). En esta historia crea su primer local sin perfil previo.

| Campo | Tipo | Obligatorio | Reglas |
|-------|------|-------------|--------|
| id | UUID | Sí (generado) | Identificador único |
| whatsapp | String | Sí | Formato E.164; **identifica** al comerciante (clave natural en el MVP) |
| fechaRegistro | Instant | Sí (generado) | — |

**Relación**: Comerciante 1 — N Local.

**Resolución durante el registro**: El `RegistrarLocalRequest` NO incluye datos separados del
comerciante. El servicio toma el WhatsApp del formulario y **busca un Comerciante existente por
ese número**; si no existe, lo **crea**. El WhatsApp resultante se asigna tanto al Comerciante
(identidad) como al Local (contacto). Un WhatsApp de comerciante es único en el sistema.

### HorarioAtencion

Franja de disponibilidad del local. Un local tiene al menos uno (FR-002).

| Campo | Tipo | Obligatorio | Reglas |
|-------|------|-------------|--------|
| id | UUID | Sí (generado) | — |
| diaSemana | Enum {LUN..DOM} | Sí | — |
| horaApertura | LocalTime | Sí | — |
| horaCierre | LocalTime | Sí | Debe ser posterior a apertura salvo cruce de medianoche declarado (FR-010) |
| cruzaMedianoche | Boolean | Sí | Default false |

### Ubicacion (Value Object embebido)

Punto geográfico del local (FR-005, FR-006).

| Campo | Tipo | Obligatorio | Reglas |
|-------|------|-------------|--------|
| latitud | Double | Sí | Rango [-90, 90] |
| longitud | Double | Sí | Rango [-180, 180] |
| origen | Enum {GPS, MANUAL} | Sí | Indica cómo se capturó |
| direccionTexto | String | No | Opcional, referencia legible |

### TipoNegocio (Catálogo precargado)

Categoría del comercio, seleccionable de un conjunto predefinido (Assumption de la spec).
Se carga en `db/data/data.sql`.

| Campo | Tipo | Obligatorio | Reglas |
|-------|------|-------------|--------|
| id | Long (integer int64) | Sí | Clave del catálogo (consistente con el contrato OpenAPI) |
| nombre | String | Sí | Único (p.ej. Restaurante, Farmacia, Ferretería) |

### Servicio (Opcional)

Servicio ofrecido por el local (campo opcional del formulario, FR-003).

| Campo | Tipo | Obligatorio | Reglas |
|-------|------|-------------|--------|
| id | UUID | Sí (generado) | — |
| nombre | String | Sí (si se incluye el servicio) | No vacío |

## Relaciones

```text
Comerciante 1 ──< Local >── 1 TipoNegocio
                   │  │
                   │  └──< Servicio (0..N)
                   └──< HorarioAtencion (1..N)
                   Local 1 ── 1 Ubicacion (embebida)
```

## Transiciones de estado (Local)

```text
[nuevo] --completa obligatorios & guarda--> PUBLICADO
[nuevo] --guarda con obligatorios faltantes--> (rechazado: error de validación, sin persistir)
```

## Validaciones consolidadas (origen en requisitos)

- Obligatorios: nombre, tipoNegocio, ubicación, whatsapp, ≥1 horario (FR-002, FR-004).
- WhatsApp con formato válido antes de publicar (FR-010).
- Coherencia de horario apertura/cierre (FR-010).
- Coordenadas dentro de rango; permitir origen MANUAL si no hay GPS (FR-005, FR-006).
- Máximo 10 campos en el formulario de entrada (FR-001) — se refleja en el contrato OpenAPI.
- Resolución de Comerciante por WhatsApp: crear si no existe, reutilizar si existe (FR-007).
- Registro atómico: un fallo al guardar no deja un Local parcialmente publicado (FR-012).
- Reintento seguro: reenviar los mismos datos no crea duplicados; un WhatsApp + ubicación
  equivalente ya existente responde como duplicado (FR-012, edge case de duplicado).
