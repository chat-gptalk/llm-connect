package chat.gptalk.lmm.gateway.entity;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("api_keys")
public class ApiKeyEntity {

    @Id
    private Integer id;
    private String name;
    private String hashedKey;
    private Integer ownerId;
    private String ownerType;
    private String permissions;
    private String ipWhitelist;
    private String rateLimit;
    private Boolean isActive;
    private Instant lastUsedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiredAt;
}
