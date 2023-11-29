import jpabook.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션을 시작한다

        try {
            // 값 타입 컬렉션
            Member member = new Member();
            member.setName("member1");
            member.setHomeAddress(new Address("homeCity", "street1", "10000")); // 얘는 Member 객체에 삽입된다

            member.getFavoriteFoods().add("치킨");    // DB에 컬렉션을 한 번에 넣을 방법이 없기 때문에,
            member.getFavoriteFoods().add("피자");    // 별도의 테이블이 생성된다
            member.getFavoriteFoods().add("족발");    // Member 확인

//            member.getAddressHistory().add(new Address("old1", "street1", "10000"));    // 얘도 별도의 테이블이 생성되고,
//            member.getAddressHistory().add(new Address("old2", "street1", "10000"));    // 해당 테이블에 값이 들어간다
            
            // 값 타입 컬렉션 대안
            member.getAddressHistory().add(new AddressEntity("old1", "street1", "10000"));  // 이제 얘네는 값타입이 아니라 Entity
            member.getAddressHistory().add(new AddressEntity("old2", "street1", "10000"));

            em.persist(member);
            // member만 persist 했는데 favorit_food와 address라는 별도의 테이블이 생성된다
            // -> 값 타입 컬렉션은 별도의 라이프 사이클이 없고, Member에 의존한다
            // -> 영속성 전이의 고아객체 제거를 필수로 가진다고 생각하면 된다

            em.flush();
            em.clear();

            System.out.println("============= start ===============");
            Member findMember = em.find(Member.class, member.getId());  // 얘를 조회하면 Member만 가져온다 => 컬렉션들은 지연로딩이다
            System.out.println("============= end ===============");
            
//            List<Address> addressHistory = findMember.getAddressHistory(); // 여기서 ADDRESS로 select가 날아간다
//            for (Address address : addressHistory) {
//                System.out.println("address = " + address.getCity());
//            }
//            Set<String> favoriteFoods = findMember.getFavoriteFoods();  // 여기서 FAVORITEFOOD로 select가 날아간다
//            for (String favoriteFood : favoriteFoods) {
//                System.out.println("favoriteFood = " + favoriteFood);
//            }

            // 값 타입 수정
            // 값 타입을 수정하려면 전체를 다 변경해야한다
            // homeCity -> newCity
//            findMember.getHomeAddress().setCity("newCity");

            Address a = findMember.getHomeAddress();
            findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));
            
            // 값 타입 컬렉션 수정
            // 치킨 -> 한식
            // FavoriteFood는 단순 String 타입이기 때문에 remove로 지우고 add로 넣어줘야 한다
            findMember.getFavoriteFoods().remove("치킨"); // delete
            findMember.getFavoriteFoods().add("한식");    // insert
            
            // 주소 변경
            // old1 -> newCity1
//            findMember.getAddressHistory().remove(new Address("old1", "street1", "10000")); // 1. 얘를 delete 하고
            // 컬렉션은 기본적으로 eqauls를 통해 값을 비교한다
            // 따라서 삽입할 때 값을 그대로 가져와야 비교가 된다
            // -> equals와 hashCode의 중요성
//            findMember.getAddressHistory().add(new Address("newCity1", "street1", "10000"));    // 2. 얘를 insert 하는 것이 아니고
            // 3. 해당 Member의 모든 Address를 날리고, old2와 newCity1을 새로 insert 한다
            // -> 아래의 값 타입 컬렉션의 제약사항 읽어보기

            tx.commit();    // 현재 트랜잭션을 commit
        } catch (Exception e) {
            tx.rollback();
        } finally {
            // entityManger 닫기
            em.close();
        }
        // entityMangerFactory 닫기
        emf.close();

    }
}

// 값 타입 컬렉션
// ※ 값 타입을 하나 이상 저장할 때 사용
// ※ @ElementCollection, @CollectionTable 사용
// ※ 데이터베이스는 컬렉션을 같은 테이블에 저장할 수 없다
// -> 1 : N으로 풀어서 별도의 테이블을 만들어야 한다
// -> 컬렉션을 저장하기 위한 별도의 테이블이 필요함

// 값 타입 컬렉션의 제약사항
/*
    ※ 값 타입은 엔티티와 다르게 식별자 개념이 없다
    -> 값은 변경하면 추적이 어렵다
    ※ 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다
    => 영한 왈 : 값 타입 컬렉션 쓰면 안됩니다
    -> OrderColumn이란거 쓰면 되는데 얘도 엄청 위험하다고 쓰지 말라고 함
    -> 결론 : 이렇게 복잡하게 쓸 거면 이거는 다르게 풀어야 한다.
    => 컬렉션 대신에 그냥 일대다 관계를 쓰는 것을 고려
    ※ 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본 키를 구성해야 함
    -> null 입력 X, 중복 저장 X
 */

// 값 타입 컬렉션 대안
/*
    ※ 실무에서는 상황에 따라 값 타입 컬렉션 대신에 일대다 관계를 고려
    ※ 일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입을 사용
    ※ 영속성 전이(Cascade) + 고아 객체 제거를 사용해서 값 타입 컬렉션처럼 사용
    ex) AddressEntity
 */

// 정리
// 엔티티 타입의 특징
/*
    - 식별자 O
    - 생명 주기 관리
    - 공유
 */

// 값 타입의 특징
/*
    - 식별자 X
    - 생명 주기를 엔티티에 의존
    - 공유하지 않는 것이 안전(복사해서 사용)
    - 불변 객체로 만드는 것이 안전
    - 값 타입은 정말 값 타입이라 판단될 때만 사용
 */

/*
    - 엔티티와 값 타입을 혼동해서 엔티티를 값 타입으로 만들면 안됨
    - 식별자가 필요하고, 지속해서 값을 추적, 변경해야 한다면 그것은 값 타입이 아닌 엔티티
 */
