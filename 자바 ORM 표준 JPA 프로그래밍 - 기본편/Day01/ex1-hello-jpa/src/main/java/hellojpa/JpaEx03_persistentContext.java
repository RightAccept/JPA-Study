package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaEx03_persistentContext {
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
            //영속
            Member member = new Member(150L, "A");
            Member member2 = new Member(160L, "B");
            //영속성 컨텍스트의 1차 캐시에 쌓임
            em.persist(member);
            em.persist(member2);
            System.out.println("=====================");
            //persistent.xml 에서 설정한 jdbc.bacth_size에서 지정한 값이 10이라면
            //10개만큼 batch를 모아서 한 번에 커밋할 수 있게 된다.


            //DB에 저장되는 단계
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
