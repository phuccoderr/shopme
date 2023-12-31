package com.shopme.shoppingcart;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderReturnRequest;
import com.shopme.order.OrderReturnResponse;
import com.shopme.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShoppingCartRestController {
    @Autowired private ShoppingCartService service;
    @Autowired private OrderService orderService;
    @Autowired private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable(name = "productId") Integer productId,
                                   @PathVariable(name = "quantity") Integer quantity,
                                   HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = service.addProduct(productId,quantity,customer);

            return updatedQuantity + " sản phẩm này đã được thêm vào giỏ hàng của bạn.";
        } catch (CustomerNotFoundException e) {
            return "Bạn Cần Đăng nhập để thêm vào giỏ hàng!";
        } catch (ShoppingCartException e) {
            return e.getMessage();
        }

    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);

        if (email == null) {
            throw new CustomerNotFoundException("No Authenticated Customer");
        }
        return customerService.getCustomerByEmail(email);
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable(name = "productId")Integer productId,
                                HttpServletRequest request) {
        Customer customer = null;
        try {
            customer = getAuthenticatedCustomer(request);
            service.removeProduct(customer.getId(),productId);
            return "Sản phẩm của bạn đã được xóa!";
        } catch (CustomerNotFoundException e) {
            return "Bạn Cần Đăng nhập để xóa giỏ hàng!";
        }

    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable(name = "productId") Integer productId,
                                 @PathVariable(name = "quantity") Integer quantity,HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            float subtotal = service.updateQuantity(productId, quantity, customer);
            return String.valueOf(subtotal);
        } catch (CustomerNotFoundException e) {
            return "You must login to change quantity of cart";
        }
    }

    @PostMapping("/orders/return")
    public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
                                                      HttpServletRequest request) {
        Customer customer = null;
        try {
            customer = getAuthenticatedCustomer(request);

        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>("Authenticated required", HttpStatus.BAD_REQUEST);
        }

        orderService.setOrderReturnRequest(returnRequest,customer);
        return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()),HttpStatus.OK);
    }
}
