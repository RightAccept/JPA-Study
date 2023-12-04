package jpabook.jpashop.service;

import jpabook.jpashop.domain.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith((SpringRunner.class))
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void 변경감지() throws Exception {
        // given
        Book book = em.find(Book.class, 1L);
        // DB에서 데이터를 가져오고
        
        // when
        book.setName("asdfasdf");
        // 가져온 데이터의 이름을 변경한다
        
        // DirtyChecking은 영속성 컨텍스트에 저장된 값에서 변경된 값이 있으면, 해당 변화를 감지하고, UPDATE SQL문을 자동으로 작성,
        // 트랜잭션이 commit 될 때 자동으로 flush된다
    }
}
