package com.lwa.shop.lwa_product_service.service;

import static org.junit.jupiter.api.Assertions.*;

import com.lwa.shop.lwa_product_service.entity.Item;
import com.lwa.shop.lwa_product_service.exception.CustomException;
import com.lwa.shop.lwa_product_service.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class ItemServiceTest {

    private ItemRepository itemRepository;
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemService = new ItemService(itemRepository);
    }

    @Test
    void testGetAllItems() {
        Item item1 = new Item();
        item1.setName("Item 1");
        Item item2 = new Item();
        item2.setName("Item 2");

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<Item> result = itemService.getAllItems();

        assertEquals(2, result.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testGetItem_Found() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.getItem(1L);

        assertEquals("Item 1", result.getName());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testGetItem_NotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> itemService.getItem(1L));
        assertEquals("Item not found with id: 1", exception.getMessage());
    }

    @Test
    void testSaveItem() {
        Item item = new Item();
        item.setName("New Item");

        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.saveItem(item);

        assertEquals("New Item", result.getName());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testDeleteItem() {
        doNothing().when(itemRepository).deleteById(1L);

        itemService.deleteItem(1L);

        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSaveItem_WithVariants_SetsParent() {
        Item item = new Item();
        item.setName("Item with variants");

        var variant = new com.lwa.shop.lwa_product_service.entity.Variant();
        variant.setColor("Red");
        item.setVariants(Arrays.asList(variant));

        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item savedItem = itemService.saveItem(item);

        assertEquals(savedItem, variant.getItem());
        verify(itemRepository, times(1)).save(item);
    }
}
