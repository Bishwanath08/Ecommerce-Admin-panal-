package org.admin.ecommerce.Admin.Portal.service;

import jakarta.transaction.Transactional;
import org.admin.ecommerce.Admin.Portal.model.TblOrder;
import org.admin.ecommerce.Admin.Portal.model.TblOrderItem;
import org.admin.ecommerce.Admin.Portal.repository.OrderItemRepository;
import org.admin.ecommerce.Admin.Portal.repository.OrderProductDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderProductDetailsRepository orderProductDetailsRepository;


    public List<TblOrder> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    @Transactional
    public List<Map<String, Object>> getOrderItems(Long orderId) {
        TblOrderItem order = orderProductDetailsRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        List<Map<String, Object>> items = new ArrayList<>();

        for (TblOrderItem item : order.getOrder().getOrderItems()) {
            Map<String, Object> map = new HashMap<>();
            map.put("productName", item.getProduct().getProductName());
            map.put("quantity", item.getQuantity());
            map.put("price", item.getPrice());
            items.add(map);
        }

        return items;
    }

}
