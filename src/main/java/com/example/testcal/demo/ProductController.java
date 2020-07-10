package com.example.testcal.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private final ProductRepository repository;
    
    @Autowired private final AppSettingsRepository appRepository;

    List<Appsettings> appsettings;

    ProductController(final ProductRepository repository, final AppSettingsRepository appRepository) {
        this.repository = repository;
        this.appRepository = appRepository;

        appsettings = this.appRepository.findAll();
    }

    @GetMapping("/products")
    List<Product> all() {
        final List<Product> products = new ArrayList<Product>();
        Long LaborCostPercentage = (appsettings.stream()
                                    .filter(s -> "LaborCostPercentage".equals(s.getKeyname()))
                                    .findAny()
                                    .orElse(null)).getKeyvalue();

        repository.findAll().forEach(x -> { 
            Double cartonPrice = (double)x.getCartonPrice();
            Double unitPriceWithoutTax = cartonPrice / x.getCartonSize();
            Double unitPrice = unitPriceWithoutTax * ( 1 + (LaborCostPercentage/100.0));
            x.setUnitPrice(unitPrice);
            products.add(x)
        ;});
        
        return products;
    }

    @PostMapping("/calculate-price")
    PriceCalculateModel single(@RequestBody PriceCalculateModel data) {

        int productId = data.productId;
        int noOfUnits = data.noOfUnits;
        int noOfCartoons = data.noOfCartons;
        PriceCalculateModel calculatedObject = new PriceCalculateModel();
        appsettings = this.appRepository.findAll();
        List<Product> products = new ArrayList<Product>();
        repository.findAll().forEach(products::add);
        
             
        Product selectedItem = products.stream()
                        .filter(product -> productId == (product.getProductId()))
                        .findAny()
                        .orElse(null);

        Long noOfCatronsForDiscount = (appsettings.stream()
                                    .filter(s -> "NoOfCartonsPerDiscount".equals(s.getKeyname()))
                                    .findAny()
                                    .orElse(null)).getKeyvalue();

        Long LaborCostPercentage = (appsettings.stream()
                                    .filter(s -> "LaborCostPercentage".equals(s.getKeyname()))
                                    .findAny()
                                    .orElse(null)).getKeyvalue();

        
        int cartonSize = selectedItem.getCartonSize();
        

        int totalUnits = noOfUnits + (noOfCartoons * cartonSize);

        

        int noOfCartoonsReturn = (int) Math.ceil(totalUnits / cartonSize);
        int noOfUnitsReturn = totalUnits - (noOfCartoonsReturn * cartonSize);
        
        Double cartonPrice = (double)selectedItem.getCartonPrice();

        if(noOfCartoonsReturn > noOfCatronsForDiscount)
        {
            cartonPrice = (cartonPrice - (cartonPrice*0.1));
        }
        Double unitPriceWithoutTax = cartonPrice / cartonSize;
        Double unitPrice = unitPriceWithoutTax * ( 1 + (LaborCostPercentage/100.0));
        Double totalPrice = 0.0;

        if(noOfCartoonsReturn > 0)
            totalPrice += (noOfCartoonsReturn * cartonPrice);
        
        if(noOfUnitsReturn > 0)
            totalPrice += (noOfUnitsReturn * unitPrice);
            


        calculatedObject.productId = selectedItem.getProductId();
        calculatedObject.noOfCartons = noOfCartoonsReturn;
        calculatedObject.noOfUnits = noOfUnitsReturn;
        calculatedObject.totalPrice = totalPrice;

        return calculatedObject;
    }


}
