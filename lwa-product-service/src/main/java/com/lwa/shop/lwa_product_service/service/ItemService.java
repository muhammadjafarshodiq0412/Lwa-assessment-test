package com.lwa.shop.lwa_product_service.service;

import com.lwa.shop.lwa_product_service.entity.Item;
import com.lwa.shop.lwa_product_service.exception.CustomException;
import com.lwa.shop.lwa_product_service.repository.ItemRepository;
import com.lwa.shop.lwa_product_service.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        try {
            List<Item> items = itemRepository.findAll();
            log.info("Fetched all items: {}", items.size());
            return items;
        } catch (Exception e) {
            log.error("Error fetching all items", e);
            throw new RuntimeException("Failed to fetch items", e);
        }
    }

    public Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new CustomException("Item not found with id: " + id));
    }

    public Item saveItem(Item item) {
        try {
            if (item.getVariants() != null) {
                item.getVariants().forEach(variant -> variant.setItem(item));
            }
            Item savedItem = itemRepository.save(item);
            log.info("Saved item: {}", JsonUtil.toJson(savedItem));
            return savedItem;
        } catch (Exception e) {
            log.error("Error saving item: {}", item, e);
            throw new RuntimeException("Failed to save item", e);
        }
    }

    public void deleteItem(Long id) {
        try {
            itemRepository.deleteById(id);
            log.info("Deleted item with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting item with id {}", id, e);
            throw new RuntimeException("Failed to delete item with id: " + id, e);
        }
    }
}


