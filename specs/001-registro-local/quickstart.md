# Quickstart: Registro del local en una sola pantalla

**Feature**: 001-registro-local | **Date**: 2026-07-04

Guía para levantar y validar el servicio de registro con la base de datos embebida
precargada desde el paquete de `resources`.

## Requisitos previos

- JDK 21 (LTS)
- Maven 3.9+

## Estructura relevante de la base de datos precargada

Los esquemas y datos precargados viven en el paquete de `resources`:

```text
src/main/resources/
├── application.yml            # Datasource H2 + inicialización SQL
└── db/
    ├── schema/schema.sql      # DDL de tablas (local, comerciante, horario_atencion, tipo_negocio, servicio)
    └── data/data.sql          # Datos precargados (catálogo de tipos de negocio y seed)
```

Configuración de inicialización (en `application.yml`):

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:ubicate;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
  sql:
    init:
      mode: always
      schema-locations: classpath:db/schema/schema.sql
      data-locations: classpath:db/data/data.sql
```

## Generar el código de la API desde el contrato (API First)

El contrato OpenAPI es la fuente de verdad; las interfaces y DTOs se generan con
openapi-generator (no se escriben a mano):

```bash
mvn generate-sources
```

Entrada: `specs/001-registro-local/contracts/registro-local.openapi.yaml`
Salida: interfaces de API y DTOs bajo `target/generated-sources` (generator `spring`,
`interfaceOnly=true`).

## Levantar el servicio

```bash
mvn spring-boot:run
```

El servicio arranca sobre H2 en memoria con el esquema y los datos precargados.

## Probar el registro (camino feliz)

```bash
curl -X POST http://localhost:8080/api/v1/locales \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Panadería La Espiga",
    "tipoNegocioId": 3,
    "ubicacion": { "latitud": -0.180653, "longitud": -78.467834, "origen": "GPS" },
    "whatsapp": "+593987654321",
    "horarios": [
      { "diaSemana": "LUN", "horaApertura": "08:00:00", "horaCierre": "18:00:00" }
    ]
  }'
```

Respuesta esperada: `201 Created` con `estadoPublicacion: "PUBLICADO"`.

## Probar validación de obligatorios (FR-004)

Enviar el mismo cuerpo sin `whatsapp` ni `horarios` debe responder `422` con
`camposFaltantes: ["whatsapp", "horarios"]`.

## Ejecutar pruebas y cobertura (BDD + JaCoCo)

```bash
# Todas las pruebas (unitarias, integración y funcionales BDD)
mvn test

# Verificación con quality gate de cobertura (rompe el build si <80%)
mvn verify
```

Reporte JaCoCo: `target/site/jacoco/index.html` (por clase > 80%, global ≥ 80%).

Escenarios BDD (Given–When–Then) en `src/test/resources/features/registro-local.feature`,
mapeados desde los 5 escenarios de aceptación de la spec.

## Validación de la feature

- [ ] El local se publica y queda visible al enviar los obligatorios completos.
- [ ] La opción GPS fija la ubicación sin escribir dirección.
- [ ] El WhatsApp queda disponible para contacto directo desde el perfil.
- [ ] Un formulario incompleto informa los campos obligatorios faltantes antes de publicar.
- [ ] Los campos opcionales pueden omitirse sin impedir la publicación.
- [ ] Cobertura JaCoCo por clase > 80% y global ≥ 80%.
