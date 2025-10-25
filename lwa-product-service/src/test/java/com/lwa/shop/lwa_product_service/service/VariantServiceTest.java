package com.lwa.shop.lwa_product_service.service;

import static org.junit.jupiter.api.Assertions.*;

import com.lwa.shop.lwa_product_service.entity.Item;
import com.lwa.shop.lwa_product_service.entity.Variant;
import com.lwa.shop.lwa_product_service.exception.CustomException;
import com.lwa.shop.lwa_product_service.model.request.SaveVariant;
import com.lwa.shop.lwa_product_service.repository.ItemRepository;
import com.lwa.shop.lwa_product_service.repository.VariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;
import static org.mockito.Mockito.*;

class VariantServiceTest {

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private VariantService variantService;

    private Variant variant;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        item = new Item();
        item.setId(1L);
        item.setName("T-Shirt");

        variant = new Variant();
        variant.setId(10L);
        variant.setColor("Black");
        variant.setSize("M");
        variant.setPrice(150000D);
        variant.setStock(100);
        variant.setItem(item);
    }

    @Test
    void testGetAllVariants() {
        when(variantRepository.findAll()).thenReturn(List.of(variant));

        List<Variant> result = variantService.getAllVariants();

        assertEquals(1, result.size());
        assertEquals("Black", result.get(0).getColor());
        verify(variantRepository, times(1)).findAll();
    }

    @Test
    void testGetVariant_Success() {
        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));

        Variant result = variantService.getVariant(10L);

        assertNotNull(result);
        assertEquals("M", result.getSize());
        verify(variantRepository).findById(10L);
    }

    @Test
    void testGetVariant_NotFound() {
        when(variantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> variantService.getVariant(99L));
    }

    @Test
    void testSaveVariant_NewVariant() {
        SaveVariant request = new SaveVariant();
        request.setItemId(1L);
        request.setColor("Blue");
        request.setSize("L");
        request.setPrice(180000D);
        request.setStock(50);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(variantRepository.save(any(Variant.class))).thenAnswer(invocation -> {
            Variant saved = invocation.getArgument(0);
            saved.setId(20L);
            return saved;
        });

        Variant saved = variantService.saveVariant(request, null);

        assertNotNull(saved);
        assertEquals("Blue", saved.getColor());
        assertEquals(20L, saved.getId());
        verify(itemRepository).findById(1L);
        verify(variantRepository).save(any(Variant.class));
    }

    @Test
    void testSaveVariant_UpdateExisting() {
        SaveVariant request = new SaveVariant();
        request.setItemId(1L);
        request.setColor("Red");
        request.setSize("S");
        request.setPrice(120000D);
        request.setStock(70);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenReturn(variant);

        Variant updated = variantService.saveVariant(request, 10L);

        assertEquals("Red", updated.getColor());
        assertEquals("S", updated.getSize());
        verify(variantRepository).save(any(Variant.class));
    }

    @Test
    void testReduceStock_Success() {
        when(variantRepository.reduceStock(10L, 2)).thenReturn(1);
        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));

        Variant result = variantService.reduceStock(10L, 2);

        assertNotNull(result);
        verify(variantRepository).reduceStock(10L, 2);
    }

    @Test
    void testReduceStock_Insufficient() {
        when(variantRepository.reduceStock(10L, 2)).thenReturn(0);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                variantService.reduceStock(10L, 2));
        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }

    @Test
    void testIncreaseStock_Success() {
        when(variantRepository.increaseStock(10L, 5)).thenReturn(1);
        when(variantRepository.findById(10L)).thenReturn(Optional.of(variant));

        Variant result = variantService.increaseStock(10L, 5);

        assertEquals("M", result.getSize());
        verify(variantRepository).increaseStock(10L, 5);
    }

    @Test
    void testIncreaseStock_NotFound() {
        when(variantRepository.increaseStock(99L, 5)).thenReturn(0);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                variantService.increaseStock(99L, 5));
        assertTrue(ex.getMessage().contains("Variant not found"));
    }

    @Test
    void testDeleteVariant_NotFound() {
        when(variantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> variantService.deleteVariant(99L));
    }
}
