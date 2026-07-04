# Implementation Plan: Registro del local en una sola pantalla

**Branch**: `001-registro-local` | **Date**: 2026-07-04 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-registro-local/spec.md`

## Summary

Exponer un servicio backend (API REST) que permita a un comerciante sin perfil previo
registrar su local en una sola operación con los datos capturados en una única pantalla
(nombre, tipo de negocio, ubicación por GPS o manual, número de WhatsApp, al menos un
horario y servicios opcionales), validando los campos obligatorios y publicando el perfil
para que sea descubrible en búsquedas por cercanía. El enfoque técnico sigue Arquitectura
Limpia sobre Java 21 + Spring Boot, con contrato OpenAPI primero (código generado con
openapi-generator), pruebas BDD (unitarias, integración y funcionales) y una base de datos
embebida H2 inicializada desde el paquete de `resources` con esquemas y datos precargados.

## Technical Context

**Language/Version**: Java 26 (JDK instalado; toolchain de Gradle en 26)
<!-- Nota de reconciliación: el plan original apuntaba a Java 21 + Maven; la implementación se
     alineó con el scaffold real (Gradle + Spring Boot 4.1 + JDK 26 disponible). Ver tasks.md. -->

**Primary Dependencies**: Gradle 9.5 + Spring Boot 4.1.0 (spring-boot-starter-webmvc,
spring-boot-starter-validation, spring-boot-starter-data-jpa, spring-boot-h2console), plugin
`org.openapi.generator` (interfaces/DTOs desde el contrato), Lombok, mapper manual DTO↔dominio,
Cucumber-JVM (BDD), plugin `jacoco` (cobertura). Jackson 3 (`tools.jackson`) via Spring Boot 4.

**Storage**: Base de datos embebida H2 inicializada desde `src/main/resources/db` mediante scripts
SQL (esquema + datos precargados). Persistencia vía Spring Data JPA en la capa de infraestructura.

**Testing**: JUnit 5 (pruebas unitarias de dominio y casos de uso), Cucumber-JVM + JUnit Platform
(escenarios BDD Given–When–Then para integración y funcionales), Spring Boot Test + MockMvc /
RestAssured (funcionales de extremo a extremo), JaCoCo para reportes de cobertura.

**Target Platform**: JVM sobre servidor Linux (empaquetable en contenedor). Consumido por un cliente
móvil; el móvil no forma parte de este alcance de servicio.

**Project Type**: Backend web-service (API REST única), organizado por Arquitectura Limpia.

**Performance Goals**: Flujo de registro visible en < 5 minutos de extremo a extremo (métrica de
negocio); creación de local con p95 < 300 ms bajo carga nominal; el perfil es descubrible de
inmediato tras publicarse.

**Constraints**: Cobertura por clase > 80% y global ≥ 80% (JaCoCo, quality gate que rompe el build);
contrato OpenAPI obligatorio como fuente de verdad; formulario limitado a 10 campos; sin dependencia
de cuentas de redes sociales.

**Scale/Scope**: MVP de una sola historia (registro). Dimensionado inicial para miles de comercios y
picos de registro concurrente moderados.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principio de la constitución | Cómo lo cumple este plan | Estado |
|------------------------------|--------------------------|--------|
| I. Arquitectura Limpia | Paquetes `domain`, `application` (puertos/casos de uso), `infrastructure` (adaptadores web y persistencia). La Regla de Dependencia apunta hacia adentro; dominio libre de Spring/JPA. | ✅ PASS |
| II. Pruebas BDD (unit/integración/funcional) | JUnit 5 para dominio y casos de uso; Cucumber Given–When–Then para integración y funcionales mapeando los 5 escenarios de aceptación de la spec. | ✅ PASS |
| III. SOLID / YAGNI / DRY | Puertos e inversión de dependencias (DIP), casos de uso con responsabilidad única (SRP), sin funcionalidad no requerida (solo registro), mapeo centralizado con MapStruct (DRY). | ✅ PASS |
| IV. API First con OpenAPI | Contrato `contracts/registro-local.openapi.yaml` como fuente de verdad; interfaces y DTOs generados con openapi-generator-maven-plugin antes de implementar. | ✅ PASS |
| V. Cobertura y reportes (JaCoCo) | Plugin JaCoCo con reglas: cobertura por clase > 80% y global ≥ 80%; el build falla si no se cumplen. | ✅ PASS |

**Resultado del gate**: PASS — sin violaciones. No se requiere Complexity Tracking.

## Project Structure

### Documentation (this feature)

```text
specs/001-registro-local/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
│   └── registro-local.openapi.yaml
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

Estructura de proyecto Maven único con paquetes por capa según Arquitectura Limpia. Los
esquemas y datos precargados de la base de datos residen en el paquete de `resources`.

```text
pom.xml                                     # Maven: openapi-generator, jacoco, spring-boot
src/
├── main/
│   ├── java/com/ubicate/registro/
│   │   ├── domain/                         # Entidades y reglas de negocio (sin frameworks)
│   │   │   ├── model/                      # Local, Comerciante, HorarioAtencion, Ubicacion...
│   │   │   ├── exception/                  # Excepciones de dominio (validación)
│   │   │   └── port/                       # Puertos de salida (p.ej. LocalRepositoryPort)
│   │   ├── application/                    # Casos de uso (reglas de aplicación)
│   │   │   ├── usecase/                    # RegistrarLocalUseCase (SRP)
│   │   │   └── port/in/                    # Puertos de entrada (contratos de casos de uso)
│   │   └── infrastructure/                 # Adaptadores (frameworks y detalles)
│   │       ├── web/                        # Controladores que implementan la API generada
│   │       │   └── mapper/                 # MapStruct DTO(generado)↔dominio
│   │       ├── persistence/                # Adaptador JPA: entidades JPA, repositorios, mapeo
│   │       └── config/                     # Configuración Spring, beans, wiring de puertos
│   └── resources/                          # Paquete de recursos
│       ├── application.yml                 # Configuración (datasource H2, init SQL)
│       └── db/                             # Base de datos: esquemas + datos precargados
│           ├── schema/
│           │   └── schema.sql              # DDL: tablas del modelo (local, horario, ...)
│           └── data/
│               └── data.sql                # Datos precargados (tipos de negocio, seed)
└── test/
    ├── java/com/ubicate/registro/
    │   ├── domain/                         # Pruebas unitarias de dominio (JUnit 5)
    │   ├── application/                    # Pruebas unitarias de casos de uso (JUnit 5)
    │   ├── bdd/                            # Runner Cucumber + step definitions
    │   └── infrastructure/web/            # Pruebas funcionales de la API (MockMvc/RestAssured)
    └── resources/
        └── features/                       # Escenarios BDD .feature (Gherkin de la spec)
            └── registro-local.feature
```

**Structure Decision**: Proyecto Gradle único (backend web-service) con separación estricta por
capas de Arquitectura Limpia (`domain` → `application` → `infrastructure`). Se elige un solo proyecto
porque el alcance es un único servicio de API; no hay frontend en este servicio. La base de datos
embebida H2 se inicializa desde `src/main/resources/db` (esquemas en `db/schema` y datos precargados
en `db/data`), cumpliendo la petición de tener en el paquete de `resources` un db con schemas y datos
pre cargados. El código de la API (interfaces + DTOs) se genera con openapi-generator a partir del
contrato en `specs/001-registro-local/contracts/`.

## Complexity Tracking

> No aplica: el Constitution Check pasó sin violaciones.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| — | — | — |
