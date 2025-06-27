package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController {

    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;
    private final ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    @GetMapping
    public ShoppingCart getCart(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            return shoppingCartDao.getByUserId(user.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve cart");
        }
    }

    @PostMapping("/products/{productId}")
    public void addToCart(@PathVariable int productId, Principal principal) {
        try {
            int userId = userDao.getByUserName(principal.getName()).getId();
            shoppingCartDao.addProduct(userId, productId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to add product to cart");
        }
    }

    @PutMapping("/products/{productId}")
    public void updateCartItem(@PathVariable int productId,
                               @RequestBody ShoppingCartItem item,
                               Principal principal) {
        try {
            int userId = userDao.getByUserName(principal.getName()).getId();
            shoppingCartDao.updateItemQuantity(userId, productId, item.getQuantity());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update item in cart");
        }
    }

    @DeleteMapping
    public void clearCart(Principal principal) {
        try {
            int userId = userDao.getByUserName(principal.getName()).getId();
            shoppingCartDao.clearCart(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to clear cart");
        }
    }
}

