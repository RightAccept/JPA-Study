package JpaEntityMapping;

public class Mapping2_ObjectToTable {
    // @Entity
    // - @Entity가 붙은 클래스는 JPA가 관리, 엔티티라 한다
    // - JPA를 사용해서 테이블과 매핑할 클래스는 @Entity 필수
    // 주의
    // 1. 기본 생성자 필수(파라미터가 없는 public 또는 protected 생성자)
    // 2. final 클래스, enum, interface, inner 클래스 사용X
    // 3. 저장할 필드에 final 사용 X
}
