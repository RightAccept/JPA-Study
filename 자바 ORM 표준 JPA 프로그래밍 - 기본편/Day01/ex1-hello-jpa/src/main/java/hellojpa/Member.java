package hellojpa;

import javax.persistence.Entity;
import javax.persistence.Id;

// @Entity : JPA를 사용하는 클래스라는 것을 명시
// -> @Entity가 붙어 있어야 JPA가 관리한다

// 관례적으로 현재 DTO와 같은 이름의 테이블에 작업을 시행한다
// 만약 DTO와 테이블의 이름이 다를 경우 @Table을 사용하여 테이블 이름을 매핑해준다
@Entity
//@Table(name = "USER")
public class Member {

    // @Id : JPA에게 어떤 필드가 primary key인지 알려 주는 어노테이션
    @Id
    private Long id;

    // 테이블 뿐 아니라 컬럼의 이름이 다를 경우에도 @Column을 사용하여 컬럼 이름을 매핑해준다
//    @Column(name = "username")
//    @Column(unique = true, length = 10) // 제약조건 : 유니크, varchar(10), DDL 생성 기능 : DDL을 생성할 때만 실행되고, JPA의 실행 로직에는 영향을 주지 않는다
    private String name;

    public Member() {}

    // JPA는 동적으로 객체를 생성해야하기 때문에 예외 발생 : 기본 생성자를 생성해야 한다
    public Member(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}