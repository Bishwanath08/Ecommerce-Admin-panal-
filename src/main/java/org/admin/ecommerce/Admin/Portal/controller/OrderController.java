package org.admin.ecommerce.Admin.Portal.controller;

import org.admin.ecommerce.Admin.Portal.jwt.JwtTokenUtil;
import org.admin.ecommerce.Admin.Portal.model.TblOrder;
import org.admin.ecommerce.Admin.Portal.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/customer/orders")
public class OrderController extends  BaseController {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @GetMapping("/list")
    public String showOrderItems(Model model, @CookieValue(value = "jwtToken", required = false) String jwtToken) {
        getBasicDetails(model, jwtToken);
        List<TblOrder> orderItems = orderItemService.getAllOrderItems();
        model.addAttribute("orderItems", orderItems);
        return "user/order_items";
    }


    @GetMapping("/{orderId}/items")
    @ResponseBody
    public List<Map<String, Object>> getOrderItems(@PathVariable Long orderId) {
        return orderItemService.getOrderItems(orderId);
    }


}
