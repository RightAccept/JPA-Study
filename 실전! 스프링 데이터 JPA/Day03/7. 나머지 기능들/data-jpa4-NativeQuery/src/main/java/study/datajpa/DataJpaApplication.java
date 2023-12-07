package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
@EnableJpaAuditing
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	
	// 등록자, 수정자를 처리해주는 AuditorAware 스프링 빈 등록
	// 실무에서는 세션 정보나, 스프링 시큐리티 로그인 정보에서 ID를 받음
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}

// Specifications (명세)
// 책 도메인 주도 설계(Domain Driven Design)는 SPECIFICATION(명세)라는 개념을 소개
// 스프링 데이터 JPA는 JPA Criteria를 활용해서 이 개념을 사용할 수 있도록 지원
// ! 실무에서는 사용하지 않는다고 함
//	- JPA Criteria를 사용하는 만큼 코드의 가독성이 안좋고, 유지보수가 어렵다고 함

// 술어(predicate)
// - 참 또는 거짓으로 평가
// - AND OR 같은 연산자로 조합해서 다양한 검색조건을 쉽게 생성(컴포지트 패턴)
// 	- ex) 검색 조건 하나하나
// - 스프링 데이터 JPA는 org.springframework.data.jpa.domain.Specification 클래스로 정의
// 예시는 MemberRepository와 MemberRepositoryTest 확인
//	- repository 밑에 MemberSpec 생성

// Projections
// ※ 엔티티 대신에 DTO를 편리하게 조회할 때 사용
// ※ 전체 엔티티가 아니라 만약 회원 이름만 딱 조회하고 싶을때
// - repository 아래에 UsernameOnly 인터페이스와 UsernameOnlyDto 클래스 생성, MemberRepository에 findProjectionsByUsername 생성, MemberRepositoryTest 작성
//	- repository 아래에 NestedClosedProjection 인터페이스 생성
// ※ 주의
//	- 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
//	- 프로젝션 대상이 ROOT가 아니면
//		- LEFT OUTER JOIN 처리
//		- 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
// ※ 정리
//	- 프로젝션 대상이 root 엔티티면 유용하다
//	- 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다!
//	- 실무의 복잡한 쿼리를 해결하기에는 한계가 있다
//	- 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용하자

// 네이티브 쿼리
// ※ 가급적 네이티브 쿼리는 사용하지 않는게 좋음, 정말 어쩔 수 없을 때 사용
// ※ 최근에 나온 궁극의 방법 -> 스프링 데이터 Projections 활용

// 스프링 데이터 JPA 기반 네이티브 쿼리
// - 페이징 지원
// - 반환 타입
//	- Object[]
//	- Tuple
//	- DTO(스프링 데이터 인터페이스 Projections 지원)
// - 제약
//	- Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음(믿지 말고 직접 처리)
//	- JPQL처럼 애플리케이션 로딩 시점에 문법 확인 불가
//	- 동적 쿼리 불가

// - MemberRepository에 findByNativeQuery와 findByNativeProjection 생성, MemberRepositoryTest 작성