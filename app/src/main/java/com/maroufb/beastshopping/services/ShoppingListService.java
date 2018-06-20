package com.maroufb.beastshopping.services;

import com.maroufb.beastshopping.infrastructure.ServiceResponse;

public class ShoppingListService {

    private ShoppingListService() {
    }

    public static class AddShoppingListRequest{
        public String shoppingListName;
        public String ownerName;
        public String ownerEmail;

        public AddShoppingListRequest(String shoppingListName, String ownerName, String ownerEmail) {
            this.shoppingListName = shoppingListName;
            this.ownerName = ownerName;
            this.ownerEmail = ownerEmail;
        }
    }

    public static class AddShoppingListRsponse extends ServiceResponse {

    }
}
