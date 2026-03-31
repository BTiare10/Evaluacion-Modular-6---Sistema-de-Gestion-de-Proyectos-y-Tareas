# Actividad: Evaluación Final del Módulo 6

## Sistema de Gestión de Proyectos y Tareas

**Objetivo:** Crear una API REST segura para gestionar proyectos y tareas.

**Descripción:**  
La empresa TechSolutions desea implementar un sistema de gestión de proyectos que permita a los usuarios administrar
proyectos y sus tareas asociadas. Han contactado a tu equipo para desarrollar una API REST que gestione esta información
de manera segura utilizando Spring Framework y JWT para la autenticación.

### INSTRUCCIONES:

* Revisa las instrucciones detalladas que se presentan en la página 2 de este documento.
* Revisa la rúbrica de evaluación expuesta en la página 3 de este documento.

### INDICACIONES

* **Tipo de Entrega:** .Zip, .Rar.
* **Tiempo:** 120 minutos.
* **Número de participantes:** Grupos de 2 - 3 personas.

---

## Instrucciones Detalladas (Página 2)

1. **Configura un proyecto Spring Boot:** Utiliza Spring Initializr para crear un proyecto con las dependencias de
   Spring Web, Spring Data JPA, Spring Security y driver a la base de datos.
2. **Crea las entidades:**
    * **a. Proyecto:** con los atributos id (Long), nombre (String), descripción (String).
    * **b. Tarea:** con los atributos id (Long), nombre (String), descripción (String), estado (String), proyecto (
      relación ManyToOne con Proyecto).
3. **Implementa los repositorios:**
    * **a.** Proyecto Repository y Tarea Repository extendiendo de JpaRepository.
4. **Crea los servicios:**
    * **a.** ProyectoService y TareaService para la lógica de negocio.
5. **Desarrolla los controladores REST:**
    * **a. ProyectoController:** CRUD para proyectos.
    * **b. TareaController:** CRUD para tareas, incluyendo la asociación de tareas a proyectos.
6. **Agrega seguridad con JWT:**
    * **a.** Configura Spring Security para proteger las rutas.
    * **b.** Implementa el proceso de autenticación y generación de tokens JWT.
    * **c.** Asegura que solo usuarios autenticados puedan acceder a los endpoints.
7. **Pruebas:**
    * **a.** Realiza pruebas de los endpoints utilizando Postman o una herramienta similar.
    * **b.** Guarda las imágenes de las pruebas y compártelas en el comprimido.

---

## Criterios de Evaluación: Rúbrica (Página 3)

| Criterio | Muy Bien (90-100%) | Bien (70-89%) | Suficiente (50-69%) | Regular (0-49%) |
| :--- | :--- | :--- | :--- | :--- |
| **Implementación del Modelo (Proyecto y Tarea)** | Implementa correctamente las clases Proyecto y Tarea con sus propiedades, relaciones, y métodos necesarios. | Implementa las clases Proyecto y Tarea con algunos errores menores o relaciones parcialmente correctas. | Implementa las clases Proyecto y Tarea, pero de forma incompleta o con errores significativos en las relaciones. | No implementa adecuadamente las clases Proyecto y Tarea, o no están implementadas. |
| **Implementación de la API REST** | La API REST funciona perfectamente, cumpliendo con los principios REST, rutas bien definidas y respuestas correctas. | La API REST tiene pequeños errores en rutas o en la consistencia de las respuestas. | La API REST funciona parcialmente, con errores en el manejo de peticiones o respuestas incorrectas. | La API REST no funciona correctamente o no está implementada. |
| **Seguridad con JWT** | La implementación de JWT es segura, funcional, y protege adecuadamente las rutas según roles. | JWT está implementado con algunos problemas menores de seguridad o manejo de roles. | JWT implementado de forma básica, pero sin proteger adecuadamente todas las rutas o roles. | JWT no está implementado o es ineficaz en la protección de rutas. |
| **Acceso a Datos (JPA y Repositorios)** | Gestiona de forma eficiente la persistencia de datos usando JPA, con consultas optimizadas y relaciones bien configuradas. | El acceso a datos funciona, pero hay errores menores en consultas o configuraciones de relaciones. | El acceso a datos está incompleto, con errores en consultas o problemas de integridad de datos. | No se implementa el acceso a datos o hay errores críticos que impiden su funcionamiento. |
| **Documentación y Buenas Prácticas** | El código está bien documentado, con comentarios claros y sigue las buenas prácticas de programación. | El código tiene buena documentación, aunque algunos métodos clave carecen de comentarios. | El código tiene poca documentación, dificultando su comprensión. | El código carece de documentación o está completamente desorganizado. |

---
**KIBERNUM IT Academy**