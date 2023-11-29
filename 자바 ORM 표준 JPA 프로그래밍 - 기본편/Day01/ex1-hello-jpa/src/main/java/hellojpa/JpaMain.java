package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/*
 * JPA 구동 순서
 * 1. Persistence 생성
 * 2. 설정 정보 조회
 * -> META-INF/persistence.xml에서 정보를 읽어온다
 * 3. EntityManagerFactory 생성
 * 4. EntityManger가 필요할 때마다 Factory에서 생성
 * -> EntityManger가 필요할 때 = sql 구문을 사용할 때
 *
 *  ※ EntityMangerFactory는 프로젝트가 실행될 때 프로젝트 전체에 하나만 생성되어야 하고,
 *  insert를 한다거나 update를 하는 등 트랜잭션에서 실행되어야 하는 구문이 있을 경우에는 EntityManger를 생성한다
 *
 *  ※ JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
 */

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        // persistenceUnitName은 persistence.xml에서 <persistence-unit>에 넣었던 name
        // 얘를 만든 순간 db 연결이 완료된다

        // entityManger 생성
        // 엔티티 매니저는 쓰레드간 공유 X (사용하고 버려야 한다)
        EntityManager em = emf.createEntityManager();
        // 이 사이에 코드 작성

        // EntityManger에서 트랜잭션을 받아오는 구문
        // DB에서 작업하기 위해서는 트랜잭션이 반드시 필요하다
        EntityTransaction tx = em.getTransaction();
        tx.begin(); // 트랜잭션을 시작한다

        try {
            // 삽입
//            Member member = new Member();
//            member.setId(1L);
//            member.setName("HelloA");
//            em.persist(member); // JPA에서의 insert 구문 : persist

            // 단일 조회 : primary key를 사용하여 하나의 값을 받아올 수 있다
//            Member findMember = em.find(Member.class, 1L);  // Member.class의 primary key로 찾아온다
//            System.out.println("findMember.getId() = " + findMember.getId());
//            System.out.println("findMember.getId() = " + findMember.getName());

            // 전체 조회 : createQuery를 사용하여 쿼리문을 작성할 수 있다, 이때 작성되는 쿼리문을 JPQL이라고 한다
            // JPQL : 객체를 대상으로 하는 객체제한 쿼리, SQL을 추상화한 객체 지향 쿼리 언어(특정 db의 sql에 의존 X)
            // SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
            // JPA에서는 테이블을 대상으로 쿼리문을 작성하지 않고, 객체를 대상으로 쿼리문을 작성한다
            // => Member 객체를 m이라고 할 때, m을 전부 가져와라
            // 이렇게 작성해두면 persistence.xml의 hibernate.dialect에 따라서 각 DB에 맞게 변환된다
//            List<Member> result = em.createQuery("select m from Member m", Member.class)
////                    .setFirstResult(1).setMaxResults(10)  // 페이징 하는 구문, 1번부터 10번까지 가져오라는 뜻
//                    .getResultList();
//            for (Member memberA : result) {
//                System.out.println("member.getName() = " + memberA.getName());
//            }
            // 삭제
//            em.remove(findMember);    // 조회로 찾아온 객체를 넣어주면 된다

            // 수정
//            findMember.setName("HelloJPA"); // setter를 사용해 값을 변경한 후, 다시 넣어주거나 할 필요 없이 DB에서도 변경된다
            // find를 통해 DB에서 값을 가져오면, commit이 될 때까지 JPA가 해당 값을 관리한다
            // commit이 될 때까지 update, delete 등의 쿼리문을 JPA에 쌓아두다가, commit이 되는 순간 JPA에서 DB로 밀어넣는다
            // commit 전까지 java <-> JPA (<-> JDBC) <-> DB 식으로 연결이 되어 있기 때문에,
            // setter를 사용해 값을 바꾸면 JPA가 변경된 데이터를 가지고 있다가, commit 시 DB를 수정한다

            // 비영속
            Member member = new Member();
            member.setId(101L);
            member.setName("HelloJPA");

            // 영속
//            System.out.println("==== BEFORE ====");
//            em.persist(member);
//            System.out.println("==== AFTER ====");
//
//            // 1차 캐시 & 동일성 보장
//            Member findMember = em.find(Member.class, 101L);
//            // 영속성 컨텍스트에 있는지를 먼저 확인하기 때문에 insert문이 나중에 나간다
//            Member findMember2 = em.find(Member.class, 101L);
//
//            System.out.println("result = " + (findMember == findMember2));
//            // 컬렉션에서 꺼내듯 1차 캐시에서 같은 객체를 꺼내기 때문에 true가 나온다
//
//            System.out.println("findMember.getId() = " + findMember.getId());
//            System.out.println("findMember.getName() = " + findMember.getName());
            
            // 트랜잭션을 지원하는 쓰기 지연
//            Member member1 = new Member(150L, "A");
//            Member member2 = new Member(160L, "B");
//
//            em.persist(member1);
//            em.persist(member2);
//
//            System.out.println("====================");

            // 엔티티 수정
            Member member1 = em.find(Member.class, 150L);
            member1.setName("ZZZZZZ");
            
            // persist 할 필요 없다
//            em.persist(member1);

            // 영속성 컨텍스트에서 저장하고 있다가, 값이 변한걸 감지하면 commit과 함께 SQL문을 전달한다
            // 1. 영속성 컨텍스트에 처음 들어왔을 때, 스냅샷으로 처음 모습을 기록해둔다
            // 2. 새로운 값이 영속성 컨텍스트에 들어가면, 스냅샷과 비교하여 변경된 것이 있으면 쓰기 지연 SQL 저장소에 update 구문을 생성한다
            // 3. commit과 함께 DB에 전송한다

//            if (member.getName().equals("ZZZZZZ")) {
//                em.persist(member1);
//            }
            // JPA는 값을 바꾸면 commit과 함께 update를 전달한다

            System.out.println("====================");
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