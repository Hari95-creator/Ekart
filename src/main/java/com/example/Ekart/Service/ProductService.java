package com.example.Ekart.Service;

import com.example.Ekart.Entity.ItemsEntity;
import com.example.Ekart.Pojo.ItemsPojo;
import com.example.Ekart.Repository.ItemsRepo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.DateUtils;

import java.util.*;

@Service
public class ProductService {

    @Autowired
    ItemsRepo itemsRepo;

    public void addItems(String itemData) {

        try{

            JSONArray productDataArray=new JSONArray(itemData);

            for(int i=0;i<productDataArray.length();i++){

                JSONObject productData =productDataArray.getJSONObject(i);

                ItemsEntity itemsEntity=new ItemsEntity();
                itemsEntity.setItemId(productData.getInt("itemId"));
                itemsEntity.setGid(UUID.randomUUID());
                itemsEntity.setDate(productData.getString("date"));
                itemsEntity.setItemName(productData.getString("itemName"));
                itemsEntity.setItemCost(productData.getInt("itemCost"));
                itemsEntity.setItemDescription(productData.getString("itemDescription"));

                itemsRepo.save(itemsEntity);

            }




        }catch (JSONException e){

            System.out.println(e);
            throw new RuntimeException(e);
        }


    }

    public List<ItemsPojo> getProductDetails() {

        List<ItemsEntity> itemsEntities=itemsRepo.findAll();

        List<ItemsPojo> itemsPojos=new ArrayList<>();
        for(ItemsEntity itemsEntity :itemsEntities){

            ItemsPojo itemsPojo=new ItemsPojo();
            itemsPojo.setItemId(itemsEntity.getItemId());
            itemsPojo.setDate(itemsEntity.getDate());
            itemsPojo.setGid(itemsEntity.getGid());
            itemsPojo.setItemName(itemsEntity.getItemName());
            itemsPojo.setItemDescription(itemsEntity.getItemDescription());
            itemsPojo.setItemCost(itemsEntity.getItemCost());
            itemsPojos.add(itemsPojo);

        }

        return itemsPojos ;
    }

    public String deleteProduct(int productId) {

        try{

            itemsRepo.deleteById(productId);
            return "Deleted Successfully";
        }catch (Exception e){

            return e.getMessage();
        }

    }

    public ItemsPojo searchInMongo(String startDate,String endDate) {

       List<ItemsEntity> itemsEntity=itemsRepo.getSearchData(startDate,endDate);

        double totalCost = 0.0;

        for (ItemsEntity obj : itemsEntity) {
            double itemCost = obj.getItemCost();
            totalCost += itemCost;
        }
        ItemsPojo itemsPojo = new ItemsPojo();
        itemsPojo.setTotalCost(totalCost);

//       List<ItemsPojo> itemsPojoList=new ArrayList<>();
//
//        for(ItemsEntity obj : itemsEntity){
//
//            ItemsPojo itemsPojos=new ItemsPojo();
//            double totalCost=obj.getItemCost()*2;
//            itemsPojos.setTotalCost(totalCost);
//            itemsPojoList.add(itemsPojos);
//        }
        return itemsPojo;
    }
}
