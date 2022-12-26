package community.mingle.app.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="block_member")

public class BlockMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="block_member_id")
    private Long blockMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_member_id")
    private Member blockedMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_member_id")
    private Member blockerMember;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static BlockMember CreateBlockMember(Member blockedMember, Member blockerMember) {
        BlockMember blockMember = new BlockMember();
        blockMember.setBlockedMember(blockedMember);
        blockMember.setBlockerMember(blockerMember);
        blockMember.createdAt = LocalDateTime.now();
        return blockMember;
    }
}
