package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaEx06_detach {
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
            //영속상태
            // Dirty Checking을 통해 변경사항 확인
            Member member = em.find(Member.class, 150L);
            member.setName("AAAAAA");

            //JAP에서 관리하지 않는 객체
            //준영속상태(특정 엔티티를 준영속상태로 변환)
            // 영속성 컨텍스트에서 빠졌기 때문에, commit해도 수정 사항이 DB로 flush되지 않는다
            em.detach(member);

            //em안의 영속성 컨텍스트를 모두 초기화 시켜버린다.
            em.clear();

            Member member2 = em.find(Member.class, 150L);
            // 영속성 컨텍스트를 초기화했기 때문에, 위에서 select가 한 번, 여기서 한 번 더 나간다

            System.out.println("=====================");
            Member member1 = em.find(Member.class, 150L);


            //DB에 저장되는 단계
            // 비영속상태이므로 관리대상 제외 이다. 그래서 모든 영속성 컨텍스트에서 빠진다.
            tx.commit();
            }
        catch (Exception e){
            tx.rollback();
        }finally {
            //영속성 컨텍스트를 종료
            em.close();

        }

        emf.close();
    }
}
