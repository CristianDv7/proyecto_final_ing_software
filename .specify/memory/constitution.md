<!--
Sync Impact Report
==================
Version change: (template) → 1.0.0
Bump rationale: Initial ratification of the project constitution (MAJOR baseline).

Modified principles: N/A (initial adoption)
Added principles:
  - I. Arquitectura Limpia (Clean Architecture)
  - II. Pruebas BDD (Unitarias, Integración, Funcionales)
  - III. Buenas Prácticas de Programación (SOLID, YAGNI, DRY)
  - IV. API First con Contrato OpenAPI
  - V. Cobertura de Pruebas y Reportes (JaCoCo)
Added sections:
  - Restricciones Técnicas y de Calidad
  - Flujo de Desarrollo y Puertas de Calidad
  - Governance

Templates requiring updates:
  - ✅ .specify/templates/plan-template.md (Constitution Check gates align with principles; no edit required)
  - ✅ .specify/templates/spec-template.md (scope/requirements compatible; no edit required)
  - ✅ .specify/templates/tasks-template.md (test/contract task types compatible; no edit required)

Follow-up TODOs: None. Ratification date set to adoption date 2026-07-04.
-->

# Ubicate Service Constitution

## Core Principles

### I. Arquitectura Limpia (Clean Architecture)

El sistema DEBE estructurarse siguiendo la Arquitectura Limpia (Clean Architecture) de
Robert C. Martin. Las capas obligatorias son: Entidades (Enterprise Business Rules),
Casos de Uso (Application Business Rules), Adaptadores de Interfaz (controllers,
presenters, gateways) e Infraestructura/Frameworks (web, base de datos, librerías).

Reglas no negociables:

- La Regla de Dependencia DEBE respetarse: las dependencias del código fuente SOLO
  apuntan hacia adentro. Las capas internas NUNCA conocen a las externas.
- La lógica de negocio (entidades y casos de uso) DEBE ser independiente de frameworks,
  base de datos, UI y agentes externos.
- El cruce entre capas DEBE realizarse mediante interfaces/puertos (inversión de
  dependencias); los detalles de infraestructura se inyectan hacia adentro.

**Rationale**: Aislar las reglas de negocio de los detalles técnicos garantiza
testeabilidad, reemplazabilidad de la infraestructura y mantenibilidad a largo plazo.

### II. Pruebas BDD (Unitarias, Integración, Funcionales)

Cada funcionalidad DEBE contar con pruebas unitarias, de integración y funcionales
escritas bajo el enfoque BDD (Behavior-Driven Development) usando el esquema
Given–When–Then. Los escenarios de aceptación de la especificación DEBEN mapearse
directamente a pruebas ejecutables.

Reglas no negociables:

- Las pruebas unitarias DEBEN cubrir entidades y casos de uso de forma aislada.
- Las pruebas de integración DEBEN validar la colaboración entre adaptadores e
  infraestructura (base de datos, APIs, mensajería).
- Las pruebas funcionales DEBEN validar el comportamiento observable de extremo a
  extremo contra los escenarios BDD definidos.
- El lenguaje de las pruebas DEBE expresar comportamiento (Given–When–Then), no
  detalles de implementación.

**Rationale**: BDD alinea las pruebas con el comportamiento esperado por el negocio y
produce documentación viva verificable.

### III. Buenas Prácticas de Programación (SOLID, YAGNI, DRY)

Todo el código DEBE adherirse a los principios SOLID, YAGNI y DRY.

Reglas no negociables:

- **SOLID**: responsabilidad única, abierto/cerrado, sustitución de Liskov, segregación
  de interfaces e inversión de dependencias DEBEN guiar el diseño de clases y módulos.
- **YAGNI**: NO se implementa funcionalidad que no esté requerida por una necesidad
  actual y explícita.
- **DRY**: la duplicación de conocimiento DEBE eliminarse mediante abstracción
  apropiada, sin introducir acoplamiento innecesario.

**Rationale**: Estas prácticas mantienen el código simple, cohesionado y extensible,
reduciendo deuda técnica.

### IV. API First con Contrato OpenAPI

El desarrollo de APIs DEBE seguir el enfoque API First. Antes de implementar cualquier
endpoint DEBE existir y aprobarse un contrato OpenAPI.

Reglas no negociables:

- Cada API DEBE tener un contrato OpenAPI versionado como fuente única de verdad.
- El código de la API (interfaces, modelos, stubs) DEBE generarse a partir del contrato
  usando `openapi-generator`; NO se escribe a mano lo que el generador produce.
- Todo cambio de comportamiento externo de la API DEBE reflejarse primero en el contrato
  OpenAPI y luego regenerarse.

**Rationale**: El contrato como fuente de verdad garantiza consistencia entre productor
y consumidores, y elimina divergencias entre documentación e implementación.

### V. Cobertura de Pruebas y Reportes (JaCoCo)

La cobertura de pruebas DEBE medirse con JaCoCo y cumplir umbrales obligatorios.

Reglas no negociables:

- La cobertura por clase DEBE ser **mayor al 80%**.
- La cobertura global DEBE ser **mayor o igual al 80%**.
- Los reportes de cobertura DEBEN generarse con JaCoCo en cada build.
- El build DEBE fallar (quality gate) si algún umbral no se cumple; no se fusiona código
  que incumpla estas métricas.

**Rationale**: Umbrales de cobertura verificables y automatizados protegen la calidad y
previenen regresiones no detectadas.

## Restricciones Técnicas y de Calidad

- La organización del repositorio DEBE reflejar las capas de la Arquitectura Limpia,
  manteniendo la lógica de negocio libre de dependencias de infraestructura.
- Los contratos OpenAPI DEBEN residir bajo control de versiones y ser el artefacto de
  entrada de `openapi-generator`.
- La configuración de JaCoCo (umbrales por clase >80% y global >=80%) DEBE formar parte
  de la configuración de build y ejecutarse automáticamente.
- Las herramientas de pruebas DEBEN soportar el estilo BDD (Given–When–Then) en los tres
  niveles: unitario, integración y funcional.

## Flujo de Desarrollo y Puertas de Calidad

- Toda característica DEBE iniciar con la especificación y, para APIs, con el contrato
  OpenAPI aprobado antes de escribir la implementación.
- El diseño DEBE pasar el "Constitution Check" del plan antes de la fase de
  implementación; las violaciones DEBEN justificarse en Complexity Tracking o corregirse.
- Ningún cambio se fusiona sin: pruebas BDD (unitarias, integración y funcionales) en
  verde, cumplimiento de umbrales JaCoCo y verificación de adherencia a SOLID/YAGNI/DRY.
- Las revisiones de código (PR) DEBEN verificar explícitamente el cumplimiento de todos
  los principios de esta constitución.

## Governance

Esta constitución tiene precedencia sobre cualquier otra práctica del proyecto. Cuando
un lineamiento entre en conflicto con esta constitución, prevalece la constitución.

- **Enmiendas**: Toda modificación DEBE documentarse mediante PR, con justificación y,
  cuando aplique, un plan de migración. Las enmiendas requieren aprobación del equipo.
- **Versionado**: La versión de la constitución sigue Versionado Semántico.
  MAJOR para cambios incompatibles o eliminación/redefinición de principios; MINOR para
  nuevos principios o secciones o guía materialmente ampliada; PATCH para aclaraciones y
  correcciones no semánticas.
- **Cumplimiento**: Todos los PRs y revisiones DEBEN verificar el cumplimiento de los
  principios. La complejidad no justificada DEBE rechazarse. El equipo revisa
  periódicamente la adherencia a esta constitución.

**Version**: 1.0.0 | **Ratified**: 2026-07-04 | **Last Amended**: 2026-07-04
