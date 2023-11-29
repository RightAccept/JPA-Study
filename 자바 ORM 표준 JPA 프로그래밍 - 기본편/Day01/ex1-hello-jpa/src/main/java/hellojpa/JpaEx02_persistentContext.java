package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaEx02_persistentContext {
    public static void main(String[] args) {
        //로딩 시점에 딱 하나만 만들어야 한다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        //커넥션 객체
        EntityManager em = emf.createEntityManager();
        //jpa가 이루어지는 작업은 모두 transaction이 필요하다.
        EntityTransaction tx = em.getTransaction();
        //Transaction시작
        tx.begin();

        //try-catch-finally로 코드가 정상적으로 마지막까지 수행되게 처리해준다.
        //나중에는 spring이 알아서 해줌
        try{
        Member member = new Member();
           //현재 캐시에는 내용이 없으므로 처음 조회할때에는 DB에서 가져오고 영속성 컨텍스트에 올린다.
            Member findMember = em.find(Member.class, 101L);

            //두 번째 수행시에는 1차 캐시(영속성 컨텍스트)에서 가져온다.
            //복잡한 비즈니스 로직에서는 조금 도움이 된다.
            Member findMember1 = em.find(Member.class, 101L);
            System.out.println("findMember = " + findMember.getName());
            System.out.println("findMember = " + findMember.getId());

//            영속 엔티티의 동일성 보장
//             1차 캐시로 반복 가능한 읽기등급의 트랜잭션 격리 수준을 데이터베이스가 아닌
//            애플리케이션 차원에서 제공
            System.out.println("result = "+ (findMember==findMember1) );
            //DB로 데이터가 날아감
            tx.commit();
            }
        catch (Exception e){
            tx.rollback();
        }finally {
            em.close();

        }

        emf.close();
    }
}
