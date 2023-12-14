package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class QuerydslApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuerydslApplication.class, args);
	}

//	@Bean
//	JPAQueryFactory jpaQueryFactory(EntityManager em) {
//		return new JPAQueryFactory(em);
//	}
}

// Querydsl Web 지원
// 순서
// 	1. Controller에서 @QuerydslPredicate(root = 엔티티.class) Predicate predicate를 파라미터로 받으면
// 	2. ?username=Dave&age=20 처럼 파라미터로 넘어온게 Predicate 객체로 들어간다
// 	3. Predicate 객체에서는 파라미터를 QMember.member.username.eq("Dave").and(QMember.member.age.eq(20)으로 바꿔준다
// 	4. Predicate 객체를 repository에 매개변수로 전달하여 검색을 수행할 수 있다
// => 간단하게 DTO 대신 쓸 수 있는데, Q 클래스 변환도 해준다는 의미
// 한계점
//	- 단순한 조건만 가능
//		- eq, contains(like 같은 거), in 만 사용 가능
//	- 조건을 커스텀하는 기능이 복잡하고 명시적이지 않음
//	- 컨트롤러가 QueryDSL에 의존
// 	- 복잡한 실무환경에서 사용하기에는 한계가 명확

// 리포지토리 지원 - QuerydslRepositorySupport
// 사용법
// 	- MemberRepositoryImpl 같은 데에 extends하면 된다. super를 가져오면 EntityManger가 자동으로 인젝션 됨
//	- queryFactory 못 쓰고, from부터 시작해서 select를 마지막에 붙인다
// 장점
//	- getQuerydsl().applyPagination() 스프링 데이터가 제공하는 페이징을 QueryDSL로 편리하게 변환 가능(단! Sort는 오류 발생)
//	- from()으로 시작 가능(최근에는 QueryFactory를 사용해서 select()로 시작하는 것이 더 명시적)
//	- EntityManager 제공
// 단점
//	- Querydsl 3.x 버전을 대상으로 만듬
//	- Querydsl 4.x에 나온 JPAQueryFactory로 시작할 수 없음
//		- select로 시작할 수 없음(from으로 시작해야 함)
//	- QueryFactory를 제공하지 않음
//	- 스프링 데이터 Sort 기능이 정상 동작하지 않음
