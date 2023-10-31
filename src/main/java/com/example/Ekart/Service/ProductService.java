package com.example.Ekart.Service;

import com.example.Ekart.Entity.ItemsEntity;
import com.example.Ekart.Pojo.ItemsPojo;
import com.example.Ekart.Repository.ItemsRepo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ItemsRepo itemsRepo;
    private final MongoTemplate mongoTemplate;

    public ProductService(MongoTemplate mongoTemplate, ItemsRepo itemsRepo) {
        this.mongoTemplate = mongoTemplate;
        this.itemsRepo = itemsRepo;
    }

    public void addItems(String itemData) {

        try {

            JSONArray productDataArray = new JSONArray(itemData);

            for (int i = 0; i < productDataArray.length(); i++) {

                JSONObject productData = productDataArray.getJSONObject(i);

                ItemsEntity itemsEntity = new ItemsEntity();
                itemsEntity.setItemId(productData.getInt("itemId"));
                itemsEntity.setGid(UUID.randomUUID());
                itemsEntity.setDate(productData.getString("date"));
                itemsEntity.setItemName(productData.getString("itemName"));
                itemsEntity.setItemCost(productData.getInt("itemCost"));
                itemsEntity.setItemDescription(productData.getString("itemDescription"));

                itemsRepo.save(itemsEntity);

            }


        } catch (JSONException e) {

            System.out.println(e);
            throw new RuntimeException(e);
        }


    }

    public List<ItemsPojo> getProductDetails() {

        List<ItemsEntity> itemsEntities = itemsRepo.findAll();

        List<ItemsPojo> itemsPojos = new ArrayList<>();
        for (ItemsEntity itemsEntity : itemsEntities) {

            ItemsPojo itemsPojo = new ItemsPojo();
            itemsPojo.setItemId(itemsEntity.getItemId());
            itemsPojo.setDate(itemsEntity.getDate());
            itemsPojo.setGid(itemsEntity.getGid());
            itemsPojo.setItemName(itemsEntity.getItemName());
            itemsPojo.setItemDescription(itemsEntity.getItemDescription());
            itemsPojo.setItemCost(itemsEntity.getItemCost());
            itemsPojos.add(itemsPojo);

        }

        return itemsPojos;
    }

    public String deleteProduct(int productId) {

        try {

            itemsRepo.deleteById(productId);
            return "Deleted Successfully";
        } catch (Exception e) {

            return e.getMessage();
        }

    }

    public ItemsPojo searchInMongo(String startDate, String endDate) {

        List<ItemsEntity> itemsEntity = itemsRepo.getSearchData(startDate, endDate);

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


    public ItemsEntity getSearch(int id) {

        Query query = new Query();
        query.addCriteria(Criteria.where("itemId").is(id));
        query.fields().include("itemName");
        return mongoTemplate.findOne(query, ItemsEntity.class);
    }

    public ItemsPojo filterUsingMongoTemplate(int id) {

        ItemsEntity filteredData = this.getSearch(id);

        ItemsPojo itemsPojo = new ItemsPojo();
        itemsPojo.setItemName(filteredData.getItemName());

        return itemsPojo;
    }

    public List<ItemsPojo> filteringUsingDateRange(String startDate, String endDate) {

        List<ItemsEntity> searchUsingDate = this.getSearchUsingDate(startDate, endDate);

        List<ItemsPojo> itemsPojoList = new ArrayList<>();

        for (ItemsEntity obj : searchUsingDate) {

            ItemsPojo itemsPojos = new ItemsPojo();
            itemsPojos.setItemName(obj.getItemName());
            itemsPojos.setDate(obj.getDate());
            itemsPojos.setItemDescription(obj.getItemDescription());
            itemsPojos.setItemCost(obj.getItemCost());
            itemsPojoList.add(itemsPojos);
        }

        return itemsPojoList;

    }

    private List<ItemsEntity> getSearchUsingDate(String startDate, String endDate) {

        Query query = new Query();
        query.addCriteria(Criteria.where("date")
                .gte(startDate)
                .lte(endDate));
        query.fields().exclude("gid", "id");
        query.with(Sort.by(Sort.Order.desc("itemCost")));
        return mongoTemplate.find(query, ItemsEntity.class);
    }

    private ItemsPojo getAggregationOfItemCost(String startDate, String endDate) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("date").gte(startDate).lte(endDate)),
                Aggregation.group("product_details").sum("itemCost").as("totalCost")
        );

        AggregationResults<ItemsPojo> result = mongoTemplate.aggregate(aggregation, ItemsEntity.class, ItemsPojo.class);
        return result.getUniqueMappedResult();
    }

    public String getSum(String startDate, String endDate) {

        ItemsPojo sum = this.getAggregationOfItemCost(startDate, endDate);

        return "Total Cost " + sum.getTotalCost();

    }

    public void updateDocument(int id, String itemName) {
        Query query = new Query(Criteria.where("itemId").is(id));
        Update update = new Update().set("itemName", itemName);
        mongoTemplate.updateFirst(query, update, ItemsEntity.class);
    }


}
