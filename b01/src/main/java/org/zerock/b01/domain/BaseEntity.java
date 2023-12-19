package org.zerock.b01.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@MappedSuperclass //공통으로 사용되는 컬럼들
@EntityListeners(value = { AuditingEntityListener.class }) //엔티티가 DB에 추가, 변경될 때 자동으로 시간 값 지정됨
@Getter
public class BaseEntity {
    @CreatedDate
    @Column(name = "regdate", updatable = false) //DB 컬럼명 지정, 수정불가
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "moddate")
    private LocalDateTime modDate;
}
