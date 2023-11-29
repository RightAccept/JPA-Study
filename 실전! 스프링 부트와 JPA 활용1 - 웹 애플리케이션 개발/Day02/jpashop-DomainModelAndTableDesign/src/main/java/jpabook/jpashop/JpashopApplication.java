package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

}

// 회원, 주문, 상품의 관계
/*
	※ 회원은 여러 상품을 주문할 수 있다
	※ 한 번 주문할 때 여러 상품을 선택할 수 있다
		-> 주문과 상품은 다대다 관계다 
		=> 다대다 관계는 관계형 DB는 물론이고 엔티티에서도 거의 사용하지 않는다
			==> 따라서 주문상품이라는 엔티티를 추가해서 다대다 관계를 일대다, 다대다 관계로 풀어낸다
 */

// 상품 분류
/*
	※ 상품은 도서, 음반, 영화로 구분된다
		- 상품이라는 공통 속성을 사용하므로 상속 구조로 표현
 */