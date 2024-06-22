package com.example.webflux.entity;

import org.springframework.data.relational.core.mapping.Table;

@Table("movies")
public record Movie(Long id, String title) {
}
