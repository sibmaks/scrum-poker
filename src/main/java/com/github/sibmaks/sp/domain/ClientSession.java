package com.github.sibmaks.sp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author sibmaks
 * Created at 24-12-2021
 */
@Data
@Entity
@Table(name = "client_session")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSession implements Serializable {
    @Id
    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "valid_to", nullable = false)
    private Date validTo;
}
