package study.datajpa.repository;


import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    // Close Projection
//    String getUsername();
    // 조회할 엔티티의 필드를 getter 형식으로 지정하면 해당 필드만 선택해서 조회(Projection)
    // 프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공

    // 다음과 같이 스프링의 SpEL문법도 지원
    // Open Projection
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
    // 단! 이렇게 SpEL 문법을 사용하면, DB에서 엔티티 필드를 다 조회해온 다음에 계산한다
    //  - 따라서 JPQL SELECT 절 최적화가 안된다
}
