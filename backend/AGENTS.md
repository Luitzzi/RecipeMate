# Backend

Java Spring Boot + Hibernate application.
Additionally:
- Lombok
- Mapstruct (Only for simple mappings)
- Typescript generator plugin

## Conventions
- **Boundary-Service-Entity Pattern**
  - Boundary: \
      Interface to the outside of the application.
      Contains all api-endpoints. When the endpoint is for the frontend it is named
      xUiController and offers a xUiDTOs.
  - Servie: \
    Contains all service (xBA) classes. They implement the logic to
    transform a UiDTO into the (xBE). Only place where the equally named
    BE can be written to the DB.
    The DTO is mapped to the BE using a mapper. If the trasformation is simple use mapstruct,
    if not implement it yourself.
    - Entity: \
        Contains all entity (xBE) classes. They represent DB-tables managed by Hibernate.
    - Data flow: \
        The flow is always x[Ui]Controller -> xBA -> xRepository and the opposite way around.
        
- **Lombok:** \
    Do not use coupled annotations such as @Data. Always prefer for example @Getter, @Setter... specifically.