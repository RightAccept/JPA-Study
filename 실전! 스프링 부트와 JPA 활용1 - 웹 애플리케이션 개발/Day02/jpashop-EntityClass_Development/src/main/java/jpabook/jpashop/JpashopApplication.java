package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

}

// 엔티티 설계시 주의점
/*
	※ 엔티티에는 가급적 Setter를 사용하지 말자
		- Setter가 모두 열려있다면 변경 포인터가 너무 많아서, 유지보수가 어렵다.
		- 나중에 리펙토링으로 Setter 제거
	※ 모든 연관관계는 지연로딩으로 설정
		- 즉시로딩(EAGER)은 예측이 어렵고, 어떤 SQL이 실행될지 추적하기 어렵다. 특히 JPQL을 실행할 때 N+1 문제가 자주 발생한다
		- 실무에서 모든 연관관계는 지연로딩(LAZY)으로 설정해야 한다
		- 연관된 엔티티를 함께 DB에서 조회해야 하면, fetch join 또는 엔티티 그래프 기능을 사용한다
		- @XToOne(OneToOne, ManyToOne) 관계는 기본이 즉시 로딩이므로 직접 지연로딩으로 설정해야 한다
	※ 컬렉션은 필드에서 초기화하자
		- 컬렉션은 필드에서 바로 초기화 하는 것이 안전하다
		- null 문제에서 안전하다
		- 하이버네이트는 엔티티를 영속화 할 때, 컬렉션을 감싸서 하이버네이트가 제공하는 내장 컬렉션으로 변경한다.
			- 만약 getOrders() 처럼 임의의 메서드에서 컬렉션을 잘못 생성하면 하이버네이트 내부 메커니즘에 문제가 발생할 수 있다
			- 따라서 필드 레벨에서 생성하는 것이 가장 안전하고, 코드도 간결하다
 */

// 테이블, 컬럼명 생성 전략
/*
	※ 스프링 부트에서 하이버네이트 기본 매핑 전략을 변경해서 실제 테이블 필드명은 다름
		- 하이버네이트 기존 구현 : 엔티티의 필드명을 그대로 테이블의 컬럼명으로 사용(SpringPhysicalNamingStrategy)
		- 스프링 부트 신규 설정(엔티티(필드) -> 테이블(컬럼))
			1. 카멜 케이스 -> 언더 스코어(memberPoint -> member_point)
			2. .(점) -> _(언더 스코어)
			3. 대문자 -> 소문자
 */
/*
	적용 2단계
	※ 논리명 생성 : 명시적으로 컬럼, 테이블명을 직접 적지 않으면 ImplicitNamingStrategy 사용
		- spring.jpa.hibernate.nameing.implicit-strategy : 테이블이나 컬럼명을 명시하지 않을 때 논리명 적용
	※ 물리명 적용 : spring.jpa.hibernate.naming.physical-strategy : 모든 논리명에 적용됨, 실제 테이블에 적용
		- username -> usernm 등으로 회사 룰로 바꿀 수 있음
	※ 설정을 변경하려면 application.properties 혹은 application.yml에서 수정하면 된다
 */