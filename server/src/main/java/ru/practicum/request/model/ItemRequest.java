package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "item_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    @Column(nullable = false)
    private LocalDateTime created;
    @Column(nullable = false, length = 1000)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;
}
