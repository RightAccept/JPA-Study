package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

}

// 준영속 엔티티
// - 영속 컨텍스트가 더는 관리하지 않는 엔티티
// 	- 여기서는 itemService.saveItem(book)에서 수정을 시도하는 Book 객체다
//	- Book 객체는 이미 DB에 한 번 저장되어서 식별자가 존재한다.
//	- 이렇게 임의로 만들어낸 엔티티도 기존 식별자를 가지고 있으면 준영속 엔티티로 볼 수 있다
// -> new로 새로운 객체를 생성했기 때문에 JPA에서 관리되지 않는다
// 	-> 하지만 DB에 들어갔다 나와야 가질 수 있는 ID를 가지고 있다
//		-> DB에 들어갔다 나왔지만 JPA가 관리하지 않는 엔티티 == 준영속 엔티티

// 준영속 엔티티를 수정하는 2가지 방법
//	- 변경 감지 기능 사용	=> test에서 ItemUpdateTest 확인
//	- 병합(merge) 사용

// 변경 감지 기능 사용
// 영속성 컨텍스트에서 엔티티를 다시 조회한 후에 데이터를 수정하는 방법
// 트랜잭션 안에서 엔티티를 다시 조회, 변경할 값 선택
// => 트랜잭션 커밋 시점에 변경 감지(Dirty Checking)이 동작해서 데이터베이스에 UPDATE SQL 실행

// 병합 사용
// 병합은 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용하는 기능이다

// 병합 동작 방식(ItemRepository의 save 메서드 확인)
// 1. merge(item)를 실행한다
// 2. 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회한다
//	2-1. 만약 1차 캐시에 엔티티가 없으면 데이터베이스에서 엔티티를 조회하고, 1차 캐시에 저장한다
// 3. 조회한 영속 엔티티(mergeItem)에 item 엔티티의 값을 채워넣는다
//	- item 엔티티의 모든 값을 mergeItem에 밀어 넣는다
//	- 이때 mergeItem의 "제품1"이라는 이름의 "제품명 변경"으로 바뀐다
// 4. 영속 상태인 mergeItem을 반환한다

// 정리
// Item mergeItem = em.merge(item);
// mergeItem != item
// 1. 준영속 엔티티의 식별자 값으로 영속 엔티티를 조회한다
// 2. 영속 엔티티의 값을 준영속 엔티티의 값으로 모두 교체한다(병합한다)
// 3. 트랜잭션 커밋 시점에 변경 감지 기능이 동작해서 데이터베이스에 UPDATE SQL이 실행

// 주의
// - 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만, 병합을 사용하면 모든 속성이 변경된다
// - 병합 시 값이 없으면 null로 업데이트할 위험도 있다(병합은 모든 필드를 교체한다)
// ex) book의 가격이 10000원인데, 이 가격은 그대로 두고 이름만 변경하고 싶어서 setName만 실행 후 merge를 사용
//	- setName에 의해 이름은 설정 되었지만, 가격은 따로 설정되지 않음
//		- 가격이 null
//	- merge 실행 시 이름은 변경 되지만 가격이 null로 들어감

// 결론
// ※ 엔티티를 변경할 때는 항상 변경 감지를 사용하자
// - 컨트롤러에서 어설프게 엔티티를 생성하지 말자
// - 트랜잭션이 있는 서비스 계층에 식별자(id)와 변경할 데이터를 명확하게 전달하자(파라미터 or DTO)
// - 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경하세요
// - 트랜잭션 커밋 시점에 변경 감지가 실행됩니다
// => ItemController과 ItemService 확인

// 작성
// 1. ItemUpdateTest
// 2. ItemService의 updateItem
// => db에서 엔티티를 가져와서 수정 => 변경 감지 기능
// 3. ItemController