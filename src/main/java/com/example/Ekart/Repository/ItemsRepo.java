package com.example.Ekart.Repository;

import com.example.Ekart.Entity.ItemsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemsRepo extends MongoRepository<ItemsEntity,Integer> {

    @Query("{date:?0}")
    ItemsEntity getSearch(String startDate);

    @Query("{'date': { $gte: ?0, $lte: ?1 }}")
    List<ItemsEntity> getSearchData(String startDate,String endDate);

    @Query("{'itemName': ?0}")
    List<ItemsEntity> getExactData(String exactString);
}
