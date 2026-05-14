# Consigna — Entrega 1: Análisis del Sistema

**Materia**: Práctica Profesionalizante I (PP1) — 2026  
**Fecha límite**: 30/04/2026  
**Formato de entrega**: completar el archivo [`entrega-1-analisis.md`](./entrega-1-analisis.md) de este repositorio y subir el link del repo a Moodle.

---

## Objetivo

Comprender y definir los elementos fundamentales del sistema **antes de comenzar con la programación**. A través de actividades de análisis, deberán identificar los actores, definir requisitos, redactar historias de usuario y modelar el comportamiento del sistema mediante diagramas y especificaciones.

---

## Actividades

### 1. Identificación de Actores

Analizar el proyecto integrador elegido e identificar todos los usuarios o entidades externas que interactúan con el sistema.

Para cada actor incluir:

| Campo | Descripción |
|-------|-------------|
| **Nombre** | Nombre del actor |
| **Rol / Función** | Qué hace dentro del sistema |
| **Tipo** | Usuario final, administrador, sistema externo, etc. |

### 2. Historias de Usuario

Redactar historias de usuario que cubran los **flujos principales** del sistema, desde la perspectiva de cada actor identificado. Usar el formato estándar y asignar un ID a cada historia:

> **HU-01**: Como [actor], quiero [acción o funcionalidad], para [beneficio o resultado esperado].

**Reglas**:
- Cada historia debe ser independiente, pequeña y verificable.
- No mencionar tecnología, botones ni base de datos — hablar del usuario y su necesidad.
- Cubrir al menos los flujos principales de cada actor.

### 3. Requisitos Funcionales

Enumerar los requisitos funcionales del sistema. Cada requisito debe describir una acción concreta que el sistema debe permitir o realizar.

| Campo | Descripción |
|-------|-------------|
| **ID** | Identificador único (RF-01, RF-02, etc.) |
| **Descripción** | "El sistema debe permitir que [actor] [acción]..." |
| **Actor** | Actor relacionado con este requisito |
| **HU relacionada** | ID de la historia de usuario que origina este requisito |

La columna **HU relacionada** es obligatoria. Cada requisito funcional debe poder rastrearse a una historia de usuario.

### 4. Requisitos No Funcionales

Listar los requisitos no funcionales: aspectos que no representan acciones del sistema, pero sí condiciones que debe cumplir.

| Campo | Descripción |
|-------|-------------|
| **ID** | Identificador único (RNF-01, RNF-02, etc.) |
| **Categoría** | Rendimiento, seguridad, usabilidad, portabilidad, etc. |
| **Descripción** | Condición específica y medible |

**Evitar** requisitos genéricos como "el sistema debe funcionar bien". Ser específicos: "El sistema debe responder en menos de 3 segundos ante cualquier consulta de búsqueda."

### 5. Diagrama de Casos de Uso

Diseñar un diagrama de casos de uso (UML) que represente:

- Los **actores** identificados.
- Los **casos de uso** (acciones principales del sistema).
- Las **relaciones** entre actores y casos de uso (asociación, inclusión, extensión).

**Requisitos del diagrama**:
- Usar notación UML estándar.
- Representar al menos un caso de uso por cada funcionalidad principal.
- Exportar como imagen y agregarla en el archivo de entrega.

**Herramientas sugeridas**: Draw.io, Lucidchart, StarUML, PlantUML o Mermaid.

### 6. Especificación de Casos de Uso

Para cada caso de uso del diagrama, completar una ficha con los siguientes campos:

| Campo | Descripción |
|-------|-------------|
| **ID + Nombre** | Identificador único (CU-01, CU-02...) y nombre del caso de uso (debe coincidir con el diagrama) |
| **Actor principal** | Actor que inicia la interacción |
| **Descripción** | Resumen breve de qué hace este caso de uso |
| **Precondiciones** | Qué debe ser verdadero antes de que comience |
| **Postcondiciones (criterios de aceptación)** | Qué es verdadero después de que termina exitosamente |

El **flujo** se documenta en una tabla paralela de dos columnas:

- **Secuencia Normal (Camino feliz)**: pasos numerados (1, 2, 3...) del camino exitoso. Cada paso indica QUIÉN hace QUÉ (actor o sistema).
- **Excepciones / Alternativas**: flujos alternativos numerados **ligados al paso del que se desvían** (ej: 4.1 es una excepción del paso 4, 5.1 del paso 5). Se escriben en la misma fila que el paso al que corresponden.

---

## Guía de trabajo

![Entrega 1 — Guía de Trabajo: qué entregar, dónde, con qué herramienta y cómo coordinar en equipo](./guia-trabajo-entrega-1.png)
