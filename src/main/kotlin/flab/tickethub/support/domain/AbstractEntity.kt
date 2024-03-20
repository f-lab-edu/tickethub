package flab.tickethub.support.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class AbstractEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private val id: Long? = null,

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "modified_at")
    private val modifiedAt: LocalDateTime? = null,
) : Identifiable {

    override fun id(): Long? = id

}
