# Phase 0 Research: Registro del local en una sola pantalla

**Feature**: 001-registro-local | **Date**: 2026-07-04

Este documento consolida las decisiones técnicas y resuelve los puntos abiertos del
Technical Context, alineados con la constitución del proyecto.

## Decisión 1 — Lenguaje y framework

- **Decision**: Java 21 (LTS) + Spring Boot 3.3.x.
- **Rationale**: La constitución exige JaCoCo (ecosistema JVM) y openapi-generator, ambos
  de primer nivel en Java. Spring Boot ofrece validación, capa web e inicialización de
  datasource embebido con mínima fricción, y se integra limpiamente con Arquitectura Limpia
  manteniendo el dominio libre del framework.
- **Alternatives considered**: Kotlin + Spring (descartado por simplicidad de tooling del
  equipo); Quarkus/Micronaut (descartado, menor familiaridad y sin ventaja para el alcance).

## Decisión 2 — Persistencia y base de datos precargada

- **Decision**: Base de datos embebida **H2** inicializada desde `src/main/resources/db`
  con `db/schema/schema.sql` (DDL) y `db/data/data.sql` (datos precargados), consumida por
  Spring Data JPA en la capa de infraestructura. Inicialización controlada por
  `spring.sql.init` con `mode=always`.
- **Rationale**: Cumple explícitamente la petición de tener en el paquete `resources` un db
  con esquemas y datos precargados. H2 permite arranque sin infraestructura externa, ideal
  para el MVP y para pruebas de integración reproducibles. Los datos precargados incluyen el
  catálogo de tipos de negocio, necesario para mantener el formulario dentro de 10 campos.
- **Alternatives considered**: Flyway/Liquibase (mayor robustez de migraciones, pero excede
  YAGNI para el MVP; se puede adoptar más adelante); PostgreSQL en contenedor (añade
  dependencia operativa innecesaria para esta etapa).

## Decisión 3 — API First con OpenAPI

- **Decision**: Contrato `contracts/registro-local.openapi.yaml` como fuente única de verdad.
  Generación de interfaces de API y DTOs con `openapi-generator-maven-plugin`
  (generator `spring`, `interfaceOnly=true`, `useSpringBoot3=true`). El controlador de la capa
  de infraestructura implementa la interfaz generada.
- **Rationale**: Cumple el principio IV (API First). `interfaceOnly` evita sobrescribir la
  lógica y mantiene el contrato como verdad, regenerando solo interfaces/DTOs ante cambios.
- **Alternatives considered**: springdoc (genera OpenAPI desde el código = code-first,
  contrario a la constitución); escribir DTOs a mano (viola "no escribir lo que el generador
  produce").

## Decisión 4 — Estrategia de pruebas BDD

- **Decision**: Tres niveles con enfoque BDD:
  - **Unitarias** (JUnit 5): entidades de dominio y `RegistrarLocalUseCase` en aislamiento,
    con dobles de prueba para los puertos de salida.
  - **Integración** (Cucumber-JVM + JUnit Platform, `@SpringBootTest`): colaboración
    caso de uso ↔ adaptador de persistencia contra H2.
  - **Funcionales** (Cucumber + MockMvc/RestAssured): API de extremo a extremo mapeando los
    5 escenarios de aceptación de la spec en `registro-local.feature`.
- **Rationale**: Cumple el principio II. Cucumber expresa Given–When–Then legible por negocio
  y reutiliza los escenarios de la spec como documentación viva verificable.
- **Alternatives considered**: JBehave (menor adopción); solo JUnit con nombres BDD (no
  produce documentación viva en Gherkin).

## Decisión 5 — Cobertura y quality gate (JaCoCo)

- **Decision**: `jacoco-maven-plugin` con `report` y `check`. Reglas:
  `BUNDLE` line coverage ≥ 0.80 (global) y `CLASS` line coverage > 0.80 por clase. El objetivo
  `check` se enlaza a la fase `verify` y **rompe el build** si no se cumple.
- **Rationale**: Cumple el principio V con verificación automatizada. Se excluyen del cómputo
  las clases generadas por openapi-generator y la clase `main` de arranque, que no aportan
  lógica de negocio testeable.
- **Alternatives considered**: Cobertura (obsoleto); umbral solo global (no cumple la métrica
  por clase exigida).

## Decisión 6 — Detección de ubicación (GPS vs. manual)

- **Decision**: El backend acepta coordenadas (latitud/longitud) y un indicador de origen
  (`GPS` | `MANUAL`). La captura por GPS es responsabilidad del cliente móvil; el servicio
  valida rango de coordenadas y permite ubicación manual como alternativa.
- **Rationale**: Mantiene el dominio agnóstico del dispositivo (Clean Architecture) y cumple
  los escenarios de GPS y del edge case de GPS denegado sin acoplar el servicio al hardware.
- **Alternatives considered**: Geocodificación de dirección en el backend (fuera de alcance
  del MVP; YAGNI).

## Puntos NEEDS CLARIFICATION resueltos

Ninguno pendiente. Los supuestos de la spec (móvil con GPS, WhatsApp como canal principal,
catálogo predefinido de tipos de negocio, visibilidad inmediata al publicar) se adoptan como
base y quedan reflejados en las decisiones anteriores.
