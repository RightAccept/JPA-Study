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