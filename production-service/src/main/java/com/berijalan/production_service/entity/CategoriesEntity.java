package com.berijalan.production_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="mst_categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesEntity {
    public enum Type {
        PREPAID,
        POSTPAID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Type type;
}
