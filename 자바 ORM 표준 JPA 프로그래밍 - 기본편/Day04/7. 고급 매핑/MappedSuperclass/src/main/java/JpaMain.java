import jpabook.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션을 시작한다

        try {
            Member member = new Member();
            member.setName("user1");
            member.setCreatedBy("kim");
            member.setCreatedDate(LocalDateTime.now());

            em.persist(member);

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

// @MappedSuperclass
// - 공통 매핑 정보가 필요할 때 사용(id, name)
// -> DB는 서로 다른데, 객체 만들 때 편하게 하려고 사용
// ex) Member 테이블과 Seller 테이블은 서로 관계가 없지만, 공통 컬럼 ID와 Name이 있다.
// -> 객체를 생성할 때, 여러 번 작성하기 싫어서 id와 name을 가지는 BaseEnity 클래스를 생성하고, Member와 Seller가 해당 클래스를 가져다 쓴다
// - 상속관계 매핑 X
// - 엔티티X, 테이블과 매핑X
// - 부모 클래스를 상속받는 자식 클래스에 매핑 정보만 제공
// - 조회, 검색 불가(em.find(BaseEntity) 불가)
// - 직접 생성해서 사용할 일이 없으므로 추상 클래스 권장
// - 테이블과 관계 없고, 단순히 엔티티가 공통으로 사용하는 매핑 정보를 모으는 역할
// - 주로 등록일, 수정일, 등록자, 수정자 같은 전체 엔티티에서 공통으로 적용하는 정보를 모을 때 사용
// - 참고 : @Entity 클래스는 엔티티나 @MappedSuperclass로 지정한 클래스만 상속 가능