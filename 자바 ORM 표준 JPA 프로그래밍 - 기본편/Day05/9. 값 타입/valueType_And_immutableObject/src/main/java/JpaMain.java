import jpabook.*;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션을 시작한다

        try {
            // side effect 예시
//            Address address = new Address("city", "street", "zipcode");
//
//            Member member = new Member();
//            member.setName("member1");
//            member.setHomeAddress(address);
//            em.persist(member);
//
//            Member member2 = new Member();
//            member2.setName("member2");
//            member2.setHomeAddress(address);
//            em.persist(member2);
//
//            member.getHomeAddress().setCity("newCity");
            
            // 값 타입 복사 예시
//            Address address = new Address("city", "street", "zipcode");
//
//            Member member = new Member();
//            member.setName("member1");
//            member.setHomeAddress(address);
//            em.persist(member);
//
//            Address copyAddress = new Address(address.getCity(), address.getStreet(), address.getZipcode());
//
//            Member member2 = new Member();
//            member2.setName("member2");
//            member2.setHomeAddress(copyAddress);
//            em.persist(member2);
//
//            member.getHomeAddress().setCity("newCity");

            // 불변 객체로 만든 후 값을 바꾸고 싶을 때
            Address address = new Address("city", "street", "zipcode");

            Member member = new Member();
            member.setName("member1");
            member.setHomeAddress(address);
            em.persist(member);
            
            // 값 타입을 복사하면서 생성자 레벨에서 값을 변경한다
            Address copyAddress = new Address("NewCity", address.getStreet(), address.getZipcode());

            Member member2 = new Member();
            member2.setName("member2");
            member2.setHomeAddress(copyAddress);
            em.persist(member2);
            
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

// 값 타입과 불변 객체
// - 값 타입은 복잡한 객체 세상을 조금이라도 단순화하려고 만든 개념이다
// - 따라서 값 타입은 안전하게 다룰 수 있어야 한다

// 값 타입 공유 참조
// ※ 임베디드 타입 같은 값은 값 타입을 여러 엔티티에서 공유하면 위험함
// ※ 부작용(side effect) 발생

// 값 타입 복사
// ※ 값 타입의 실제 인스턴스인 값을 공유하는 것은 위험
// ※ 대신 값(인스턴스)를 복사해서 사용

// 객체 타입의 한계
// ※ 항상 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다
// ※ 문제는 임베디드 타입처럼 직접 정의한 값 타입은 자바의 기본 타입이 아니라 객체 타입이다
// ※ 자바 기본 타입에 값을 대입하면 값을 복사한다
// -> 객체 타입은 참조 값을 직접 대입하는 것을 막을 방법이 없다
// -> 객체의 공유 참조는 피할 수 없다
// Address copyAddress = address;    // 이렇게 복사하면 참조값이 넘어가기 때문에 copyAddress와 address는 같은 객체를 가리킨다

// 불변 객체 : 생성 시점 이후 절대 값을 변경할 수 없는 객체
// ※ 객체 타입을 수정할 수 없게 만들면 부작용을 원천 차단
// ※ 값 타입은 불변 객체(immutable object)로 설계해야 함
// ※ 생성자로만 값을 설정하고, 수정자(Setter)를 만들지 않으면 됨
// ※ 참고 : Integer, String은 자바가 제공하는 대표적인 불변 객체

// => 불변이라는 작은 제약으로 부작용이라는 큰 재앙을 막을 수 있다