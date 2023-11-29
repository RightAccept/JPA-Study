package JpaEntityMapping;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
@Entity
@Table(name="MBR")
public class Member {
    @Id // primary key로 지정, primary key를 직접 할당할 때는 혼자 사용하고, 자동으로 넣을 경우 @GeneratedValue와 함께 사용한다
//    @GeneratedValue // 값을 자동으로 할당(Oracle의 sequence, MySQL 계열의 auto increment)
    @GeneratedValue(strategy = GenerationType.AUTO) // DB에 맞게 생성(persistence에서 설정한 방언에 따라서 Oracle의 sequence, MySQL의 auto_increment를 선택해서 실행)
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 데이터베이스에 위임, MySQL의 AUTO_INCREMENT에 사용, persist 시점에 즉시 INSERT 실행하고 DB에서 식별자 조회
    // -> DB에 들어가봐야 primary key의 값을 알 수 있기 때문에, IDENTITY만 예외적으로 persist 단계에서 insert 구문을 실행
//    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Oracle에서 사용, sequence를 생성하고 삽입 구문에 따라 nextval 실행, 이름을 지정하지 않으면 hibernate_sequence로 생성된다
    // -> persist 단계에서 sequence의 값을 가져오고, 해당 값을 primary key에 넣으면 되기 때문에 commit 단계에서 insert 구문 실행
    private Long id;
    @Column(name = "name", updatable = false, insertable = false)   // updateable = false : 수정 불가, insertable = false : 삽입 불가
    private String username;

    @Column(nullable = false)
    private Integer age;

    // ORDINAL : enum의 순서에 따라서 값을 넣는다 ex) USER, ADMIN 순서로 있다면 USER를 넣으면 0, ADMIN을 넣으면 1
    // Enumerated의 기본값은 ORDINAL이지만, ORDINAL을 그대로 사용하면 enum의 순서가 변경될 경우 db의 데이터를 업데이트 할 방법이 없다
    // 따라서 ORDINAL을 사용하지 말고, STRING을 사용하자
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
    @Temporal(TemporalType.TIMESTAMP)   // sql에서는 date, time, timestamp로 나눠져 있기 때문에 어떤걸 사용할지 매핑해준다
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    private LocalDate testLocalDate;    // => DB에 date(년/월/일) 타입으로 생성
    private LocalDateTime testLocalDateTime;    // => DB에 timestamp(년/월/일/시간)로 생성

    @Lob
    private String description;

    @Transient
    // 필드를 컬럼에 매핑하지 않음 => DB에 생성하지 않음
    private int temp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
