package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {
    // 스프링 데이터 JPA를 사용하여 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶을 때
    //  - 등록일
    //  - 수정일
    //  - 등록자
    //  - 수정자
    // 사용하려면 메인(DataJpaApplication)에 @EnableJpaAuditing을 붙여줘야 함
    
    // 등록일
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;
    
    // 수정일
    @LastModifiedDate
    private LocalDateTime lastModifyDate;
    
    // 등록자
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    // 수정자
    @LastModifiedBy
    private String lastModifiedBy;
    
    // 등록자, 수정자를 등록하기 위해서는 메인(DataJpaApplication)에서 AuditorAware 스프링 빈을 등록해야 한다
}
// 실무에서 대부분의 엔티티는 등록 시간, 수정 시간이 필요하지만, 등록자, 수정자는 없을 수도 있다
//  - Base 타입을 분리하고, 원하는 타입을 선택해서 상속한다
// ex) BaseTimeEntity를 생성하여 등록일, 수정일을 넣어두고, BaseEntity에서는 BaseTimeEntity를 상속받게 만든다
//  - 시간만 필요하면 엔티티에 BaseTimeEntity를, 시간과 사람 모두 필요하면 BaseEntity를 상속받게 한다

// 전체 적용
// @EntityListeners(AuditingEntityListener.class) 를 생략하고 스프링 데이터 JPA 가 제공하는 이벤트를 엔티티 전체에 적용하려면 orm.xml에 등록한다
/*
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence/orm http://xmlns.jcp.org/xml/ns/persistence/orm_2_2.xsd" version="2.2">
    <persistence-unit-metadata>
        <persistence-unit-defaults>
            <entity-listeners>
                <entity-listener class="org.springframework.data.jpa.domain.support.AuditingEntityListener"/>
            </entity-listeners>
        </persistence-unit-defaults>
    </persistence-unit-metadata>
</entity-mappings>
*/