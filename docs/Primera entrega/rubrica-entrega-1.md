# Rubrica de Evaluacion — Entrega 1: Analisis del Sistema

**Materia**: Practica Profesionalizante I (PP1)  
**Carrera**: Tecnico Superior en Desarrollo de Software — 2do ano  
**Entrega**: 1 — Analisis del Sistema  
**Fecha limite**: 30/04/2026

---

## Criterios de evaluacion por actividad

### Actividad 1 — Identificacion de Actores (8%)

| Criterio | Excelente (10) | Bueno (7) | Insuficiente (4) |
|----------|----------------|-----------|-------------------|
| Completitud | Se identifican TODOS los actores del sistema, incluyendo actores secundarios y sistemas externos si corresponde. | Se identifican los actores principales pero falta algun actor secundario o sistema externo relevante. | Faltan actores principales o se listan actores que no interactuan con el sistema. |
| Descripcion del rol | Cada actor tiene una descripcion clara de su funcion en el sistema que permite distinguirlo de los demas. | Las descripciones existen pero son genericas o ambiguas (ej: "usa el sistema"). | No hay descripciones, o son copias entre si, o no reflejan lo que el actor realmente hace. |
| Clasificacion por tipo | Cada actor esta correctamente clasificado (usuario final, administrador, sistema externo, etc.) y la clasificacion es coherente con su rol. | La clasificacion existe pero hay errores menores (ej: un sistema externo clasificado como usuario). | No hay clasificacion o la mayoria esta mal asignada. |

---

### Actividad 2 — Requisitos Funcionales (12%)

| Criterio | Excelente (10) | Bueno (7) | Insuficiente (4) |
|----------|----------------|-----------|-------------------|
| Redaccion | Todos los RF siguen el formato "El sistema debe permitir que [actor] [accion]..." y describen una accion concreta y verificable. | La mayoria sigue el formato correcto, pero algunos son vagos o describen mas de una funcionalidad por requisito. | Los RF son genericos (ej: "gestionar pedidos"), no indican actor, o mezclan multiples funciones en uno solo. |
| Cobertura funcional | Los RF cubren las funcionalidades principales del dominio elegido (alta, baja, modificacion, consulta, y las especificas del negocio). | Cubren las funcionalidades basicas pero faltan operaciones esperables del dominio. | Solo hay 2 o 3 RF genericos que no representan el alcance real del sistema. |
| Referencia a HU | Cada RF tiene asociada al menos una HU existente en la seccion 4, y la relacion tiene sentido. | La mayoria referencia HU pero hay RF sin referencia o con referencias que no coinciden. | No hay columna de HU relacionada, o las referencias apuntan a HU que no existen. |
| Identificadores | Los RF tienen IDs unicos y consecutivos (RF-01, RF-02...) que permiten referenciarlos desde otras secciones. | Tienen IDs pero hay saltos, duplicados, o inconsistencias menores en la numeracion. | No hay IDs o el sistema de identificacion no permite referenciar los requisitos. |

---

### Actividad 3 — Requisitos No Funcionales (8%)

| Criterio | Excelente (10) | Bueno (7) | Insuficiente (4) |
|----------|----------------|-----------|-------------------|
| Variedad de categorias | Los RNF cubren al menos 3 categorias distintas (rendimiento, seguridad, usabilidad, disponibilidad, etc.) y son relevantes para el proyecto. | Cubren 2 categorias o incluyen alguna categoria poco relevante para el contexto. | Solo hay una categoria, o los RNF son en realidad requisitos funcionales disfrazados. |
| Especificidad | Los RNF son medibles o verificables (ej: "El sistema debe responder en menos de 3 segundos", "Las contrasenas deben tener al menos 8 caracteres"). | Algunos son especificos pero otros son vagos (ej: "el sistema debe ser rapido"). | Todos son genericos y no verificables (ej: "buena seguridad", "facil de usar"). |
| Clasificacion | Cada RNF tiene su categoria correctamente asignada y el ID es unico y consecutivo. | Existen categorias pero hay errores de clasificacion en algun caso. | No hay categorias asignadas o la mayoria esta mal clasificada. |

---

### Actividad 4 — Historias de Usuario (15%)

| Criterio | Excelente (10) | Bueno (7) | Insuficiente (4) |
|----------|----------------|-----------|-------------------|
| Formato | Todas las HU siguen el formato "Como [rol], quiero [accion] para [beneficio]" con las tres partes completas y coherentes entre si. | La mayoria sigue el formato, pero algunas tienen el beneficio vago o el rol no coincide con un actor identificado. | No siguen el formato, faltan partes (ej: sin beneficio), o el formato es libre sin estructura. |
| Coherencia con actores | El rol de cada HU corresponde a un actor listado en la seccion 1, y la accion es consistente con las capacidades de ese actor. | La mayoria coincide, pero hay HU con roles que no aparecen como actores o que no corresponden. | Los roles de las HU no tienen relacion con los actores identificados. |
| Cobertura | Las HU representan las funcionalidades clave del sistema y cubren los flujos principales de todos los actores relevantes. | Cubren los flujos principales de los actores mas importantes pero faltan flujos secundarios esperables. | Hay muy pocas HU (menos de 5) o solo cubren un actor o una parte minima del sistema. |
| Identificadores | Cada HU tiene un ID unico (HU-01, HU-02...) que permite referenciarla desde los RF y los CU. | Tienen IDs pero hay inconsistencias menores. | No hay IDs o no se puede vincular las HU con otras secciones. |

---

### Actividad 5 — Diagrama de Casos de Uso (15%)

| Criterio | Excelente (10) | Bueno (7) | Insuficiente (4) |
|----------|----------------|-----------|-------------------|
| Notacion UML | El diagrama usa correctamente la notacion UML: actores como figuras de palo, casos de uso como elipses dentro del limite del sistema, y relaciones con la linea correcta. | La notacion es mayormente correcta pero tiene errores menores (ej: falta el rectangulo del sistema, o usa flechas donde no corresponde). | No sigue la notacion UML (ej: usa cajas en vez de elipses, no hay limite del sistema, los actores estan dentro del sistema). |
| Relaciones | Las relaciones entre actores y casos de uso son correctas. Si hay relaciones include/extend, estan bien aplicadas y justificadas. | Las asociaciones actor-CU son correctas pero hay algun include/extend mal usado o innecesario. | Los actores no estan conectados a los CU que les corresponden, o hay relaciones inventadas que no tienen sentido. |
| Consistencia con especificaciones | Cada caso de uso del diagrama tiene su especificacion en la seccion 6, y cada especificacion de la seccion 6 aparece en el diagrama. Hay correspondencia 1 a 1. | La mayoria coincide pero hay 1-2 casos de uso sin especificacion o viceversa. | El diagrama y las especificaciones muestran sistemas distintos: CU que aparecen en uno pero no en el otro. |
| Legibilidad | El diagrama es claro, legible, con nombres descriptivos en los CU (verbo + sustantivo, ej: "Registrar pedido"). La imagen tiene resolucion suficiente para leer todo sin hacer zoom. | Es legible pero algunos nombres son ambiguos o la disposicion dificulta seguir las relaciones. | No se puede leer (imagen borrosa, texto cortado, CU superpuestos) o los nombres no son descriptivos. |

---

### Actividad 6 — Especificacion de Casos de Uso (15%)

| Criterio | Excelente (10) | Bueno (7) | Insuficiente (4) |
|----------|----------------|-----------|-------------------|
| Campos completos | Cada especificacion tiene: actor principal, descripcion, precondiciones, postcondiciones, flujo principal, y al menos un flujo alternativo. Todos los campos estan completos. | Tiene la mayoria de los campos pero alguno esta vacio o con contenido generico (ej: precondicion "ninguna" sin justificacion). | Faltan campos obligatorios en la mayoria de las especificaciones, o los campos presentes estan vacios. |
| Flujo principal | Los pasos del flujo principal son secuenciales, numerados, y describen la interaccion actor-sistema de forma clara. Cada paso indica QUIEN hace QUE (el actor o el sistema). | Los pasos son secuenciales pero no siempre queda claro si actua el actor o el sistema, o faltan pasos intermedios logicos. | El flujo no es secuencial, tiene pasos confusos, o describe el proceso en un solo parrafo sin estructura. |
| Flujos alternativos | Hay al menos un flujo alternativo por CU que cubre un escenario real (error de validacion, dato inexistente, cancelacion). El flujo esta en la columna paralela "Excepciones / Alternativas" con numeracion ligada al paso del que se desvia (ej: 4.1 para una excepcion del paso 4). | Hay flujos alternativos pero son triviales, no usan la columna paralela, o no indican desde que paso se desvian. | No hay flujos alternativos, o son copia del flujo principal con cambios cosmeticos. |
| Pre y postcondiciones | Las precondiciones describen el estado necesario del sistema ANTES de ejecutar el CU. Las postcondiciones describen el estado del sistema DESPUES. Ambas son verificables. | Existen pero son genericas (ej: "el usuario debe estar logueado" para todos los CU sin discriminar). | No hay pre/postcondiciones, o describen cosas que no son estados del sistema. |
| Cantidad | Se especifican todos los CU que aparecen en el diagrama (actividad 5). | Faltan 1-2 especificaciones respecto al diagrama. | Se especifican menos de la mitad de los CU del diagrama. |

---

### Trazabilidad transversal (7%)

Este criterio evalua la COHERENCIA entre todas las secciones del documento como un todo integrado.

| Criterio | Excelente (10) | Bueno (7) | Insuficiente (4) |
|----------|----------------|-----------|-------------------|
| RF a HU | Todos los RF referencian HU que existen y la relacion es logica (el RF describe una capacidad que la HU necesita). | La mayoria de las referencias son correctas pero hay 1-2 RF que apuntan a HU inexistentes o no relacionadas. | Las referencias son incorrectas, inexistentes, o no hay relacion logica entre RF y HU. |
| Actores consistentes | Los actores de la seccion 1 son los mismos que aparecen en las HU, en los RF, en el diagrama y en las especificaciones de CU. No hay actores "fantasma" que aparecen en un lugar y no en otro. | Hay consistencia general pero algun actor aparece con nombre distinto en diferentes secciones (ej: "Cliente" en una seccion y "Usuario" en otra para el mismo rol). | Cada seccion parece hablar de actores distintos, o hay actores en el diagrama que no estan en la seccion 1. |
| CU a diagrama | Los nombres de los CU en las especificaciones coinciden exactamente con los del diagrama. Los actores asociados son los mismos en ambos lugares. | Los nombres son similares pero no identicos, o hay diferencias menores en los actores asociados. | No hay correspondencia clara entre el diagrama y las especificaciones. |

---

## Defensa oral individual (20%)

La entrega incluye una instancia de **defensa oral** donde cada integrante del grupo sera evaluado de forma **individual**. El objetivo es verificar que cada persona entiende y puede explicar el trabajo entregado, no solo que participo en su confeccion.

Durante la defensa, cada integrante respondera preguntas sobre cualquier seccion del documento. La nota de defensa oral es **personal**: dos integrantes del mismo grupo pueden tener notas distintas en esta instancia.

**No alcanza con haber escrito una parte del documento.** Cada integrante debe poder explicar las decisiones del grupo completo: por que se eligieron esos actores, que criterio se uso para redactar los RF, como se relacionan las HU con los CU, por que un flujo alternativo cubre determinado escenario, etc.

Si un integrante no puede explicar secciones que no escribio personalmente, la nota de defensa refleja esa falta de comprension.

---

## Tabla resumen de pesos

| # | Actividad | Peso |
|---|-----------|------|
| 1 | Identificacion de Actores | 8% |
| 2 | Requisitos Funcionales | 12% |
| 3 | Requisitos No Funcionales | 8% |
| 4 | Historias de Usuario | 15% |
| 5 | Diagrama de Casos de Uso | 15% |
| 6 | Especificacion de Casos de Uso | 15% |
| T | Trazabilidad transversal | 7% |
| D | Defensa oral individual | 20% |
| | **Total** | **100%** |

---

## Notas importantes

- La nota del documento (actividades 1 a 6 + trazabilidad) es **grupal**. La nota de defensa oral es **individual**. La nota final de cada alumno combina ambas.
- Un trabajo puede tener buena nota en cada actividad por separado pero baja nota en trazabilidad si las secciones no se hablan entre si. Revisen la coherencia del documento completo antes de entregar.
- Si el diagrama no se puede leer (imagen borrosa, resolucion baja, texto cortado), se evalua con la nota minima en legibilidad. Exporten en buena calidad.
- Las entregas fuera de la fecha limite se evaluan con los criterios de esta rubrica pero con penalizacion segun el reglamento de la materia.
