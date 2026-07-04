<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read the current plan:
`specs/001-registro-local/plan.md`

Active feature: 001-registro-local (Registro del local en una sola pantalla) — IMPLEMENTADO
Stack: Java 26, Spring Boot 4.1 (Gradle), paquete base com.cj7.ubicate, H2 embebida
(src/main/resources/db: schema.sql + data.sql), openapi-generator (plugin Gradle, contrato en
specs/001-registro-local/contracts), Cucumber (BDD), JaCoCo (global 97.5%). Arquitectura Limpia
(domain/application/infrastructure). Build: `./gradlew build` (JAVA_HOME=JDK26).
<!-- SPECKIT END -->
