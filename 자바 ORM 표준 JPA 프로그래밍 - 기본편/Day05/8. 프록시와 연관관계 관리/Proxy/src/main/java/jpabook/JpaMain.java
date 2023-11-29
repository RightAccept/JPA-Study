package jpabook;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션을 시작한다

        try {
//            Member findMember = em.find(Member.class, 1L);
//            // A1. 해당 메서드를 사용할 때는 팀과 멤버를 동시에 가져오는 것이 좋다
//            printMemberAndTeam(findMember);
//
//            // A1. 메서드가 printMember로 Member만 출력하게 된다면 굳이 team을 가져올 필요가 없다
//            printMember(findMember);
            
            // em.find()와 em.getReference()
            Member member = new Member();
            member.setName("hello");

            em.persist(member);

            em.flush();
            em.clear();
            
//            Member findMember = em.find(Member.class, member.getId());
//            System.out.println("findMember.getId() = " + findMember.getId());
//            System.out.println("findMember.getName() = " + findMember.getName());

            // 호출하는 시점에는 쿼리가 나가지 않지만, 사용하는 시점에서 쿼리가 나간다
//            Member referenceMember = em.getReference(Member.class, member.getId());
//            System.out.println("referenceMember.getClass() = " + referenceMember.getClass());
//            System.out.println("referenceMember.getId() = " + referenceMember.getId());
//            System.out.println("referenceMember.getName() = " + referenceMember.getName());

            // 원본 객체와 프록시 객체의 타입 비교
            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            Member member2 = new Member();
            member2.setName("member2");
            em.persist(member2);

            Member member3 = new Member();
            member3.setName("member3");
            em.persist(member3);

            em.flush();
            em.clear();

            Member m1 = em.find(Member.class, member1.getId());
            Member m2 = em.find(Member.class, member2.getId());
            Member m3 = em.getReference(Member.class, member3.getId());
            System.out.println("(m1.getClass() == m2.getClass()) = " + (m1.getClass() == m2.getClass()));
            System.out.println("(m1.getClass() == m3.getClass()) = " + (m1.getClass() == m3.getClass()));
            
            // instance of 사용
            System.out.println("(m3 instanceof Member) = " + (m3 instanceof Member));
            
//            em.detach(m1);
//            em.clear();

//            m1.getId();

            // 프록시 확인
            // 1. 프록시 인스턴스의 초기화 여부 확인
            System.out.println("emf.getPersistenceUnitUtil().isLoaded(m1) = " + emf.getPersistenceUnitUtil().isLoaded(m3));
            System.out.println(m3.getName());
            System.out.println("emf.getPersistenceUnitUtil().isLoaded(m3) = " + emf.getPersistenceUnitUtil().isLoaded(m3));

            // 2. 프록시 클래스 확인 방법
            System.out.println("m1.getClass() = " + m1.getClass().getName());
            System.out.println("m3.getClass() = " + m3.getClass().getName());

            // 3. 프록시 강제 초기화
            org.hibernate.Hibernate.initialize(m3);

            tx.commit();    // 현재 트랜잭션을 commit
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
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

// 프록시
// Q1. Member를 조회할 때 Team도 함께 조회해야 할까?

// 프록시 기초
// JPA에서는 em.find 말고도 em.getReference()로 참조를 가져올 수 있다
// - em.find : 데이터베이스를 통해서 실제 엔티티 객체 조회
// - em.getReference() : 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회
// -> 호출하는 시점에는 쿼리가 나가지 않지만, 사용하는 시점에서 쿼리가 나간다

// 프록시 특징
// ※ 실제 클래스를 상속 받아서 만들어짐
// -> 실제 클래스와 겉 모양이 같다
// ※ 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 됨(이론상)
// ※ 프록시 객체는 실제 객체의 참조(target)를 보관
// -> 메서드를 사용하면 target에 있는 참조의 메서드를 호출한다
// -> 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드 호출
// ※ 프록시 객체는 처음 사용할 때 한 번만 초기화
// ※ 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님
// -> 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근 가능
// ※ 프록시 객체는 원본 엔티티를 상속받음
// -> 따라서 타입 체크시 주의해야 함(== 비교 실패, 대신 instace of 사용)
// ※ 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티 반환
// ※ 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 문제 발생
// -> 하이버네이트는 org.hibernate.LazyInitializationException 예외를 터트림
// 중요!!!!!
// ※ 프록시가 먼저 초기화되면, em.find를 사용해도 Proxy를 반환한다


// 프록시 객체의 초기화
// 1. Client가 프록시 객체에 getName() 요청
// 2. 프록시 객체가 영속성 컨텍스트에 초기화 요청
// 3. 영속성 컨텍스트는 DB를 조회
// 4. DB에서 받은 값으로 영속성 컨텍스트가 실제 Entity 생성
// 5. 프록시 객체의 target에 실제 생성된 Entity 연결
// 6. 프록시 객체에서 target의 getName() 호출