---
description: "Task list for Registro del local en una sola pantalla"
---

# Tasks: Registro del local en una sola pantalla

**Input**: Design documents from `/specs/001-registro-local/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/registro-local.openapi.yaml

**Tests**: INCLUDED — la constitución del proyecto (Principio II) exige pruebas BDD unitarias,
de integración y funcionales. Las pruebas se escriben antes de la implementación (rojo → verde).

**Organization**: Tareas agrupadas por historia de usuario. Solo existe una historia (US1, P1).

> **Estado: IMPLEMENTADO ✅** (2026-07-04). Build verde de extremo a extremo con
> `gradlew build`: 26 pruebas en verde (unitarias + 10 escenarios BDD) y quality gate
> JaCoCo cumplido (global 97.5%, por clase ≥92%, todas >80%).
>
> **Adaptaciones respecto al plan** (por alinearse con el scaffold real existente):
> - **Gradle + Spring Boot 4.1** en lugar de Maven + Spring Boot 3.3 (el proyecto ya venía
>   scaffoldeado con Gradle). openapi-generator y JaCoCo se configuraron como plugins Gradle.
> - **Java 26** (único JDK instalado) en lugar de Java 21; toolchain de Gradle en 26.
> - Paquete base **`com.cj7.ubicate`** (del scaffold) en lugar de `com.ubicate.registro`.
> - **Mapper manual** (`LocalWebMapper`) en lugar de MapStruct, para reducir riesgo con
>   Jackson 3 / Spring Boot 4; se mantiene un único punto de mapeo (DRY).
> - Spring Boot 4 usa **Jackson 3** (`tools.jackson.databind`); las pruebas lo reflejan.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencias pendientes)
- **[Story]**: Historia a la que pertenece la tarea (US1)
- Todas las rutas asumen proyecto Maven único con Arquitectura Limpia (ver plan.md)

Base package: `src/main/java/com/ubicate/registro/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicialización del proyecto y tooling exigido por la constitución.

- [x] T001 Crear la estructura del proyecto Maven con paquetes de Arquitectura Limpia (`domain`, `application`, `infrastructure`) bajo `src/main/java/com/ubicate/registro/` y árbol de tests en `src/test/java/com/ubicate/registro/` según plan.md
- [x] T002 Configurar `pom.xml` con Java 21, Spring Boot 3.3.x (web, validation, data-jpa), H2 y MapStruct
- [x] T003 [P] Configurar `openapi-generator-maven-plugin` en `pom.xml` (generator `spring`, `interfaceOnly=true`, `useSpringBoot3=true`) apuntando a `specs/001-registro-local/contracts/registro-local.openapi.yaml`
- [x] T004 [P] Configurar `jacoco-maven-plugin` en `pom.xml` con reglas de cobertura (BUNDLE line ≥ 0.80 global y CLASS line > 0.80 por clase), enlazado a la fase `verify`, excluyendo clases generadas y la clase `main`
- [x] T005 [P] Configurar dependencias de prueba en `pom.xml`: JUnit 5, Cucumber-JVM + cucumber-junit-platform-engine, spring-boot-starter-test y rest-assured
- [x] T006 Crear la clase de arranque `src/main/java/com/ubicate/registro/RegistroApplication.java` (Spring Boot)

**Checkpoint**: El proyecto compila (`mvn compile`) y el tooling de generación/cobertura está listo.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Base de datos precargada, generación de la API y wiring que TODA la historia necesita.

**⚠️ CRITICAL**: Ninguna tarea de la historia puede empezar hasta completar esta fase.

- [x] T007 Crear el esquema DDL en `src/main/resources/db/schema/schema.sql` con tablas `comerciante`, `tipo_negocio` (id `BIGINT`), `local`, `horario_atencion`, `servicio` según data-model.md, con **restricción UNIQUE en `comerciante.whatsapp`** (identidad del comerciante) para garantizar reintento seguro (FR-012)
- [x] T008 Crear los datos precargados en `src/main/resources/db/data/data.sql` (catálogo de `tipo_negocio` y seed base) según data-model.md
- [x] T009 Configurar `src/main/resources/application.yml` con datasource H2 en memoria y `spring.sql.init` (schema-locations y data-locations apuntando a `classpath:db/schema/schema.sql` y `classpath:db/data/data.sql`)
- [x] T010 Generar las interfaces de API y DTOs desde el contrato OpenAPI ejecutando `mvn generate-sources` y verificar la salida en `target/generated-sources`
- [x] T011 Crear la configuración de wiring de puertos en `src/main/java/com/ubicate/registro/infrastructure/config/BeanConfig.java` (inyección de casos de uso y adaptadores)
- [x] T012 [P] Crear el manejador global de errores en `src/main/java/com/ubicate/registro/infrastructure/web/GlobalExceptionHandler.java` que mapee excepciones de dominio a 422 (validación) y 409 (duplicado)

**Checkpoint**: BD embebida arranca con esquema+datos; interfaces de API generadas; wiring listo.

---

## Phase 3: User Story 1 - Registro rápido del local en una sola pantalla (Priority: P1) 🎯 MVP

**Goal**: Registrar y publicar un local con los datos de una sola pantalla, validando obligatorios,
soportando ubicación GPS/manual y exponiendo el WhatsApp de contacto.

**Independent Test**: Enviar `POST /api/v1/locales` con los obligatorios completos devuelve `201`
con `estadoPublicacion: PUBLICADO`; enviarlo incompleto devuelve `422` con los campos faltantes.

### Tests for User Story 1 (escribir PRIMERO, deben FALLAR antes de implementar) ⚠️

- [x] T013 [P] [US1] Definir los escenarios Gherkin (5 escenarios de aceptación de la spec) en `src/test/resources/features/registro-local.feature`
- [x] T014 [P] [US1] Crear el runner Cucumber en `src/test/java/com/ubicate/registro/bdd/RunCucumberTest.java` (JUnit Platform, `@SpringBootTest`)
- [x] T015 [P] [US1] Prueba unitaria del dominio (validaciones de `Local`, `HorarioAtencion`, `Ubicacion`) en `src/test/java/com/ubicate/registro/domain/LocalTest.java`
- [x] T016 [P] [US1] Prueba unitaria del caso de uso `RegistrarLocalUseCase` con dobles de los puertos en `src/test/java/com/ubicate/registro/application/RegistrarLocalUseCaseTest.java`
- [x] T017 [P] [US1] Prueba funcional de la API (camino feliz 201, validación 422 y duplicado/reintento 409) en `src/test/java/com/ubicate/registro/infrastructure/web/RegistrarLocalApiTest.java` (MockMvc/RestAssured)
- [x] T018 [US1] Step definitions BDD que enlazan los escenarios con la API en `src/test/java/com/ubicate/registro/bdd/RegistroLocalSteps.java`

### Implementation for User Story 1

- [x] T019 [P] [US1] Crear value object `Ubicacion` (validación de rango lat/long y origen GPS/MANUAL) en `src/main/java/com/ubicate/registro/domain/model/Ubicacion.java`
- [x] T020 [P] [US1] Crear entidad `HorarioAtencion` (coherencia apertura/cierre y cruce de medianoche) en `src/main/java/com/ubicate/registro/domain/model/HorarioAtencion.java`
- [x] T021 [P] [US1] Crear entidades `TipoNegocio`, `Servicio` y `Comerciante` en `src/main/java/com/ubicate/registro/domain/model/`
- [x] T022 [US1] Crear entidad raíz `Local` con reglas de obligatorios y estado de publicación en `src/main/java/com/ubicate/registro/domain/model/Local.java` (depende de T019–T021)
- [x] T023 [P] [US1] Crear excepciones de dominio `CamposObligatoriosFaltantesException` y `LocalDuplicadoException` en `src/main/java/com/ubicate/registro/domain/exception/`
- [x] T024 [P] [US1] Definir los puertos de salida `LocalRepositoryPort` y `ComercianteRepositoryPort` (buscar por WhatsApp y guardar) en `src/main/java/com/ubicate/registro/domain/port/`
- [x] T025 [P] [US1] Definir el puerto de entrada `RegistrarLocalPort` en `src/main/java/com/ubicate/registro/application/port/in/RegistrarLocalPort.java`
- [x] T026 [US1] Implementar `RegistrarLocalUseCase` de forma **transaccional/atómica** (validar obligatorios; **resolver Comerciante por WhatsApp: crear si no existe o reutilizar**; detectar duplicado; publicar) en `src/main/java/com/ubicate/registro/application/usecase/RegistrarLocalUseCase.java` (depende de T022–T025) — cubre FR-007 y FR-012 (atomicidad y reintento seguro)
- [x] T027 [P] [US1] Crear entidades JPA y repositorios Spring Data para `Local` y `Comerciante` (con `findByWhatsapp`) en `src/main/java/com/ubicate/registro/infrastructure/persistence/` (mapeo a tablas del schema.sql)
- [x] T028 [US1] Implementar los adaptadores `LocalRepositoryAdapter` y `ComercianteRepositoryAdapter` (implementan sus puertos sobre JPA) en `src/main/java/com/ubicate/registro/infrastructure/persistence/` (depende de T024, T027)
- [x] T029 [P] [US1] Crear el mapper MapStruct DTO(generado)↔dominio en `src/main/java/com/ubicate/registro/infrastructure/web/mapper/LocalMapper.java`
- [x] T030 [US1] Implementar el controlador que realiza la interfaz de API generada en `src/main/java/com/ubicate/registro/infrastructure/web/RegistrarLocalController.java` (depende de T026, T029)
- [x] T031 [US1] Ejecutar las pruebas de la historia y confirmar que pasan a verde (`mvn test`), ajustando la implementación hasta cubrir los 5 escenarios

**Checkpoint**: US1 completamente funcional y testeable de forma independiente.

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Endurecimiento, cobertura y documentación.

- [x] T032 Ejecutar `mvn verify` y confirmar el quality gate JaCoCo (por clase > 80% y global ≥ 80%); añadir pruebas donde falte cobertura
- [x] T033 [P] Validar el flujo completo con `quickstart.md` (arranque, curl 201/422, reporte de cobertura)
- [x] T034 [P] Revisar adherencia a SOLID/YAGNI/DRY y refactorizar duplicación en mapeos y validaciones
- [x] T035 [P] Actualizar `README.md` con instrucciones de build, generación de API y ejecución de pruebas

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sin dependencias — inicia de inmediato.
- **Foundational (Phase 2)**: Depende de Setup — BLOQUEA la historia.
- **User Story 1 (Phase 3)**: Depende de Foundational.
- **Polish (Phase 4)**: Depende de que US1 esté completa.

### Within User Story 1

- Tests (T013–T018) se escriben antes y deben FALLAR antes de implementar.
- Dominio (T019–T023) antes que puertos/casos de uso.
- Caso de uso (T026) depende de dominio y puertos.
- Adaptadores de persistencia (T027–T028) y web (T029–T030) tras el caso de uso.
- T031 cierra la historia validando verde.

### Parallel Opportunities

- Setup: T003, T004, T005 en paralelo.
- Foundational: T012 en paralelo con T007–T009 (archivos distintos).
- US1 tests: T013, T014, T015, T016, T017 en paralelo (archivos distintos).
- US1 dominio: T019, T020, T021, T023, T024, T025 en paralelo; T029 en paralelo con persistencia.

---

## Parallel Example: User Story 1

```bash
# Lanzar las pruebas de US1 en paralelo (deben fallar primero):
Task: "Escenarios Gherkin en src/test/resources/features/registro-local.feature"
Task: "Prueba unitaria de dominio en src/test/java/com/ubicate/registro/domain/LocalTest.java"
Task: "Prueba unitaria del caso de uso en .../application/RegistrarLocalUseCaseTest.java"
Task: "Prueba funcional de la API en .../infrastructure/web/RegistrarLocalApiTest.java"

# Lanzar el modelo de dominio en paralelo:
Task: "Ubicacion en domain/model/Ubicacion.java"
Task: "HorarioAtencion en domain/model/HorarioAtencion.java"
Task: "TipoNegocio, Servicio, Comerciante en domain/model/"
```

---

## Implementation Strategy

### MVP First (User Story 1)

1. Completar Phase 1 (Setup).
2. Completar Phase 2 (Foundational) — BD precargada + API generada.
3. Completar Phase 3 (US1) siguiendo tests → dominio → aplicación → infraestructura.
4. **STOP y VALIDAR**: probar US1 de forma independiente (201 y 422).
5. Completar Phase 4 (Polish) para asegurar el quality gate de cobertura.

### Notes

- [P] = archivos distintos, sin dependencias.
- Verificar que las pruebas fallan antes de implementar (Principio II, rojo→verde).
- Commit tras cada tarea o grupo lógico.
- El contrato OpenAPI es la fuente de verdad: ante cambios, regenerar (no editar a mano lo generado).
