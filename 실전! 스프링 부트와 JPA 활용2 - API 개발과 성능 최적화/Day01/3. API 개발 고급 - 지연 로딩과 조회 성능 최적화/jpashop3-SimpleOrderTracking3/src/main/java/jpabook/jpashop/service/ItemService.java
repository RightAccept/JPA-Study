package jpabook.jpashop.service;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Item;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuntity) {
        Item findItem = itemRepository.findOne(itemId); // db에서 가져오므로 영속 엔티티
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuntity);
        // 실무에서 set을 사용하는 것은 좋지 않다
        // OrderItem 엔티티에서 addStock을 만들었던 것 처럼 의미 있는 메서드를 생성하여 값을 수정하는 것이 좋다
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
