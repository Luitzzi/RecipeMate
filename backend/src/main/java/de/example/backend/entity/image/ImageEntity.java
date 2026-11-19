package de.example.backend.entity.image;

import de.example.backend.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = ImageEntity.tableName)
@Getter
@Setter
@NoArgsConstructor
public class ImageEntity {
    public static final String tableName = "images";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false, unique = true)
    private String key;

    public enum ImageUploadStatus {
        RESERVED,
        ACTIVE
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageUploadStatus status;

    public ImageEntity(UserEntity createdBy, Instant createdAt, String key, ImageUploadStatus status) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.key = key;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true; // Ensures x.equals(x) == true
        if (!(o instanceof ImageEntity that))
            return false;
        return getId() != null && Objects.equals(getId(), that.getId()); // Id is null if the entity was not persisted
                                                                         // yet. If so comparing it by id is not
                                                                         // possible.
    }

    @Override
    public int hashCode() {
        // Id is only assigned on persist -> Constant hashcode
        return ImageEntity.class.hashCode();
    }
}
