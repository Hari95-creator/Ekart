package com.example.Ekart.Controller;

import com.example.Ekart.Entity.ItemsEntity;
import com.example.Ekart.Pojo.ItemsPojo;
import com.example.Ekart.Service.ProductService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value="/product")
public class ProductController {


    @Autowired
    ProductService productService;


    @RequestMapping(value="/add",method = RequestMethod.POST)
    @ResponseBody
    public String addProduct(@RequestBody String productData,Model model, Locale locale){


        productService.addItems(productData);


        return "Ok";


    }

    @RequestMapping(value="/read",method=RequestMethod.GET)
    public ResponseEntity<List<ItemsPojo>> getProductData(){

        List<ItemsPojo> getDetails=productService.getProductDetails();
        return ResponseEntity.ok(getDetails);
    }

    @RequestMapping(value="/delete/{productId}",method =RequestMethod.POST)
    public ResponseEntity<String> deleteProduct(@PathVariable int productId){

        String productDelete=productService.deleteProduct(productId);
        return ResponseEntity.ok(productDelete);

    }

    @RequestMapping(value="/search/{startDate}/{endDate}",method =RequestMethod.GET)
    public ResponseEntity<ItemsPojo> searchInDb(@PathVariable String startDate,@PathVariable String endDate){

        ItemsPojo searchResult=productService.searchInMongo(startDate,endDate);
        return ResponseEntity.ok(searchResult);

    }



}
