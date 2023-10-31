package com.example.Ekart.Controller;

import com.example.Ekart.Pojo.ItemsPojo;
import com.example.Ekart.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value = "/product")
public class ProductController {


    @Autowired
    ProductService productService;


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addProduct(@RequestBody String productData, Model model, Locale locale) {


        productService.addItems(productData);


        return "Ok";


    }

    @RequestMapping(value = "/read", method = RequestMethod.GET)
    public ResponseEntity<List<ItemsPojo>> getProductData() {

        List<ItemsPojo> getDetails = productService.getProductDetails();
        return ResponseEntity.ok(getDetails);
    }

    @RequestMapping(value = "/delete/{productId}", method = RequestMethod.POST)
    public ResponseEntity<String> deleteProduct(@PathVariable int productId) {

        String productDelete = productService.deleteProduct(productId);
        return ResponseEntity.ok(productDelete);

    }

    @RequestMapping(value = "/search/{startDate}/{endDate}", method = RequestMethod.GET)
    public ResponseEntity<ItemsPojo> searchInDb(@PathVariable String startDate, @PathVariable String endDate) {

        ItemsPojo searchResult = productService.searchInMongo(startDate, endDate);
        return ResponseEntity.ok(searchResult);

    }

    @RequestMapping(value = "/filter/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> searchUsingMongotemplate(@PathVariable int id) {

        ItemsPojo searchResult = productService.filterUsingMongoTemplate(id);
        return ResponseEntity.ok(searchResult.getItemName());

    }

    @RequestMapping(value = "/filter/range/{startDate}/{endDate}", method = RequestMethod.GET)
    public ResponseEntity<List<ItemsPojo>> searchUsingRange(@PathVariable String startDate, @PathVariable String endDate) {

        List<ItemsPojo> searchResult = productService.filteringUsingDateRange(startDate, endDate);
        return ResponseEntity.ok(searchResult);

    }

    @RequestMapping(value = "/sum/{startDate}/{endDate}", method = RequestMethod.GET)
    public ResponseEntity<String> sum(@PathVariable String startDate, @PathVariable String endDate) {

        String searchResult = productService.getSum(startDate, endDate);
        return ResponseEntity.ok(searchResult);

    }

    @RequestMapping(value = "/update/{id}/{itemName}", method = RequestMethod.GET)
    @ResponseBody
    public String update(@PathVariable int id, @PathVariable String itemName) {

        productService.updateDocument(id, itemName);
        return "Updated Succesfully";

    }


}
