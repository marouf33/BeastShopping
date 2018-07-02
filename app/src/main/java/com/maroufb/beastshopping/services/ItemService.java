package com.maroufb.beastshopping.services;

import com.maroufb.beastshopping.infrastructure.ServiceResponse;

public class ItemService {

    public static class  AddShoppingItemRequest{

        public String itemName;
        public String ownerEmail;
        public String shoppingList;

        public AddShoppingItemRequest(String itemName, String ownerEmail, String shoppingList) {

            this.itemName = itemName;
            this.ownerEmail = ownerEmail;
            this.shoppingList = shoppingList;
        }
    }

    public static class AddShoppingItemResponse extends ServiceResponse {}

    public static class DeleteItemRequest{
        public String ownerEmail;
        public String listId;
        public String itemId;

        public DeleteItemRequest(String ownerEmail, String listId, String itemId) {
            this.ownerEmail = ownerEmail;
            this.listId = listId;
            this.itemId = itemId;
        }
    }

    public static class ChangeItemNameRequest {
        public String itemId;
        public String shoppingListId;
        public String userEmail;
        public String newItemName;

        public ChangeItemNameRequest(String itemId, String shoppingListId, String userEmail, String newItemName) {
            this.itemId = itemId;
            this.shoppingListId = shoppingListId;
            this.userEmail = userEmail;
            this.newItemName = newItemName;
        }


    }

    public static class ChangeItemNameResponse extends ServiceResponse{}

    public static class ChangeItemBoughtStatusRequest{
        public String itemId;
        public String shoppingListId;
        public String userEmail;
        public String buyerEmail;
        public boolean isBought;

        public ChangeItemBoughtStatusRequest(String itemId, String shoppingListId, String userEmail, String buyerEmail, boolean isBought) {
            this.itemId = itemId;
            this.shoppingListId = shoppingListId;
            this.userEmail = userEmail;
            this.buyerEmail = buyerEmail;
            this.isBought = isBought;
        }
    }


}
