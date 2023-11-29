import jpabook.Child;
import jpabook.Member;
import jpabook.Parent;
import jpabook.Team;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션을 시작한다

        try {
            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChild(child1);
            parent.addChild(child2);

            em.persist(parent);
            // 1번
            em.persist(child1);
            em.persist(child2);

            em.flush();
            em.clear();

            Parent findParent = em.find(Parent.class, parent.getId());
            // 2번
//            findParent.getChildList().remove(0);
            // 컬렉션에서 제거된 엔티티를 DB에서 제거한다
            
            // 3번
            em.remove(findParent);
//            findParent.getChildList().remove(0);

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

    private static void printMember(Member findMember) {
        System.out.println("findMember = " + findMember.getName());
    }

    private static void printMemberAndTeam(Member findMember) {
        String username = findMember.getName();
        System.out.println("username = " + username);

        Team team = findMember.getTeam();
        System.out.println("team = " + team.getName());
    }
}

// 영속성 전이: CASCADE
// ※ 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶을 때
// ex) 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장

// 주의!!
// - 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음
// -> 엔티티를 영속화 할 때 연관된 엔티티도 함께 영속화하는 편리함을 제공할 뿐

// 이럴 때만 사용
// 1. 패런트와 차일드의 라이프 사이클이 동일할 때
// 2. 단일 소유자(소유자가 하나)일 때
// Child가 Parent만 연결되어 있으면 사용해도 되지만, 만약 Member도 Child를 알고 있다면 Parent에서 CASCADE를 사용하면 안된다

// 고아 객체
// ※ 고아 객체 제거 : 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
// orphanRemoval = true

// 고아 객체 - 주의
// - 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능
// - 참조하는 곳이 하나일 때 사용해야 함!
// - 특정 엔티티가 개인 소유할 때 사용
// - @OneToOne, @OneToMany만 가능

// 영속성 전이 + 고아 객체, 생명주기
// - CascadeType.ALL + orphanRemoval=true
// - 스스로 생명주기를 관리하는 엔티티는 em.persist()로 영속화, em.remove()로 제거
// - 두 옵션을 모두 활성화 하면 부모 엔티티를 통해서 자식의 생명 주기를 관리할 수 있음
// - 도메인 주도 설계(DDD)의 Aggregate Root 개념을 구현할 때 유용