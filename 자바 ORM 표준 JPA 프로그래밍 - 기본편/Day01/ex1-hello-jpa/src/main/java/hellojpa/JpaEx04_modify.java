package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaEx04_modify {
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
            Member member = em.find(Member.class, 150L);
            member.setName("ZZZZZ");



            //member를 자바컬렉션에서 꺼내고 값을 변경했을때 다시 집어넣어야하나? 아니다
            // 그러므로 persist메서드를 사용하여 다시 저장할 필요가 없다.
            //JPA는 변경감지가 가능하다. upadte메서드를 사용하지 않아도 됨
            //해당 값의 최초 영속성 컨텍스트에 들어온 상태를 저장(스냅샷)해둔다.
            //그리고 커밋되는 시점에 flush란 메서드가 호출되면서 JPA가 1차 캐시의 엔티티와 비교를 한다.
            //만약 변경된 점이 있으면 UpdateSQL구문을 생성하여 쓰기지연 저장소에 넣어둔다.
            // 마지막으로 flush가 이루어질 때 쿼리문을 DB에 보내어 수정되도록 한다.
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
