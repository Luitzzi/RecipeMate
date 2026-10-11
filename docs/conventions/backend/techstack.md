# Techstack

- **Java Spring Boot:**
  
    Modules:

  - Spring MVC
  
  - Spring Data JPA
 
    -> Includes Hibernate
  
  - Spring OpenAPI
  
    → Automatically hosts [SwaggerUi](http://localhost:8080/swagger-ui/index.html#/)


- **Lombok:**

    Generates repetitive boilerplate code at compile time.
    Use for example @Getter and @Setter to generate getters and setters for all fields.
    Avoid grouped annotations such as @Data (contains @Getter, @Setter, ...), just use all
    annotations specifically

- **Mapstruct:**

    Define mappings in an abstract class extending AbstractMapper.
    Mapstruct then generates a xMapperImpl class at compile time implementing the mappings.
    For simple mappings this is perfect, avoid it for complex ones. 
    Verbose self implemented mappings are better than mapstruct magic.
    
- **Typescript Generator Plugin:**

    Generates the api-interfaces of all compiled classes with the prefix *UiDTO for the frontend.
    
    