package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaEx01_persistentContext {
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
            //비영속
            Member member = new Member();
            member.setId(111L);
            member.setName("helloJPA");

            //영속(DB에 아직 저장되지 않음)
            System.out.println("===Before===");
            //1차 캐시에 저장
            em.persist(member);
//            em.detach(member); //회원 엔티티를 영속성 컨텍스트에서 분리, 준영속 상태
            System.out.println("===After ===");

            Member findMember = em.find(Member.class, 101L);
            Member findMember2 = em.find(Member.class, 101L);
            //DB에 있는 정보를 가져오는게 아닌 영속성Context에서 1차 캐시를 통해 조회함
            System.out.println("findMember1 = " + findMember.getName());
            System.out.println("findMember2 = " + findMember2.getId());
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
