package com.github.sibmaks.sp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author sibmaks
 * Created at 12-10-2021
 */
@Data
@Entity
@Table(name = "room")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "room_id_seq", sequenceName = "room_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_id_seq")
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @ManyToOne(optional = false)
    private User author;
    @Column(name = "voting", nullable = false)
    private boolean voting;
    @Column(name = "created", nullable = false)
    private Date created;
    @Column(name = "expired", nullable = false)
    private Date expired;
}
