package hellojpa;

import javax.persistence.*;

public class JpaEx05_flush {
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
            Member member = new Member(200L, "member200");
            //영속성 컨텍스트에 담김
            em.persist(member);

            //강제 호출로 DB에 insert쿼리가 즉시 나가고 DB Transaction이 커밋된다.
            //1차 캐시는 유지가 된다. 오직 변경 사항 등의 쓰기 지연 sql저장소에 있는 쿼리들이 DB에 반영이 되는 과정
            em.flush();

            //Flush Mode Option
//            em.setFlushMode(FlushModeType.AUTO);  커밋이나 쿼리를 실행할 떄 플러시(기본값) 가급적 기본값으로 사용하기
//            em.setFlushMode(FlushModeType.COMMIT); 커밋할 떄 플러시



            System.out.println("====================");
            tx.commit();


            System.out.println("=====================");


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
