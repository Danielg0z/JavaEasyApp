package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);

    void addItem(int userId, ShoppingCart item);

    void updateItemQuantity(int userId, int productId, int quantity);

    void removeItem(int userId, int productId);

    void clearCart(int userId);

    List<ShoppingCartItem> getItems(int userId);
}
