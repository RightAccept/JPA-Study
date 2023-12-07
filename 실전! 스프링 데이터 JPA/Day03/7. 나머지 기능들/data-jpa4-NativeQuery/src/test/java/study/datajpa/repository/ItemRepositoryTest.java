package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    public void save() throws Exception {
        // given
        Item item = new Item("A");
        itemRepository.save(item);
        // @Transactional이 없어도 구현체에서 받아온다
        // save는 ID가 없어야 persist를 하는데, 만약 ID를 자동 생성이 아니라 직접 넣어주는 형식이라면?
        //  - save를 타고 갔을 때 ID가 있는지 확인하고, 없으면 persist, 있으면 merge를 실행한다
        //  - ID를 직접 넣어주는 형태일 때, ID가 있다고 판단하고 merge를 실행한다
        //  - merge를 실행하더라도 DB에 데이터가 없는 것을 확인하면 insert를 해주기는 하지만, 그만큼 성능이 떨어진다
        // 만약 ID를 직접 넣어줘야 한다면 Persistable을 implements하여 사용해야한다
        
        // when


        // then

    }

}