<<<<<<<< HEAD:src/main/java/com/ebiz/drivel/domain/block/entity/BlockMember.java
package com.ebiz.drivel.domain.block.entity;
========
package com.ebiz.drivel.domain.block;
>>>>>>>> 5e5fb8b (refactor: 신고/차단 기능 수정):src/main/java/com/ebiz/drivel/domain/block/BlockMember.java

import com.ebiz.drivel.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "block_member")
@EqualsAndHashCode(of = {"member", "targetMember"})
public class BlockMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id", nullable = false)
    private Member targetMember;
}
