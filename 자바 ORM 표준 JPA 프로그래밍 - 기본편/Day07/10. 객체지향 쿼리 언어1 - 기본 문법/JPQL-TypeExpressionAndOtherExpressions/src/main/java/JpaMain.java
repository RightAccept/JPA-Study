import jpql.*;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
//            String query = "select m.username, 'HELLO', true From Member m where m.type = jpql.MemberType.ADMIN";
            String query = "select m.username, 'HELLO', true From Member m where m.type = :userType";
//            String query = "select m.username, 'HELLO', true From Member m where m.age between 0 and 10";
//            List<Object[]> resultList = em.createQuery(query).setParameter("userType", MemberType.ADMIN).getResultList();
//
//            for (Object[] objects : resultList) {
//                System.out.println("objects[0] = " + objects[0]);
//                System.out.println("objects[0] = " + objects[1]);
//                System.out.println("objects[0] = " + objects[2]);
//            }

            // Book이랑 Item 가져와서 쿼리 작성(jpashop에서)
//            em.createQuery("select i from Item i where type(i) = Book", Item.class);

            tx.commit();
        } catch (Exception e) {
            System.err.println("예외 발생 : " + e);
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}

// JPQL 타입 표현
/*
    ※ 문자는 싱글 쿼테이션(') 안에 넣으면 된다
    -> 싱글 쿼테이션(')을 표현해야 하면 싱글 쿼테이션을 2개('') 넣는다
    ※ 숫자 : 10L(Long), 10D(Double), 10F(Float)
    ※ boolean : TRUE, FALSE
    ※ ENUM : 패키지명을 포함하여 작성해야 한다
    ex) jpabook.MemberType.Admin
    ※ 엔티티 타입 : Type(m) = Member(상속 관계에서 사용)
 */