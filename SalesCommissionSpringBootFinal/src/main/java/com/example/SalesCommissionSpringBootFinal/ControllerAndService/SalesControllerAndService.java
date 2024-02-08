package com.example.SalesCommissionSpringBootFinal.ControllerAndService;

import com.example.SalesCommissionSpringBootFinal.*;
import com.example.SalesCommissionSpringBootFinal.Model.Commission;
import com.example.SalesCommissionSpringBootFinal.Model.Product;
import com.example.SalesCommissionSpringBootFinal.Model.Sales;
import com.example.SalesCommissionSpringBootFinal.Model.Salesman;
import com.example.SalesCommissionSpringBootFinal.repository.CommissionRepository;
import com.example.SalesCommissionSpringBootFinal.repository.ProductRepository;
import com.example.SalesCommissionSpringBootFinal.repository.SalesmanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
@RequestMapping("/sales")
@RestController
public class SalesControllerAndService {
    @Autowired
    private CommissionRepository commissionRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SalesmanRepository salesmanRepository;


    @TrackExecutionTime
    @PostMapping("/sales_post")
    public ResponseEntity<List<Commission>> computeSalesAndSaveToDB(@RequestBody Sales s) {
        List<Product> products = s.getProducts();
        List<Salesman> salesmans = s.getSalesman();
        TreeMap<Long,Salesman> salesmanHashMap = new TreeMap<>();
        for(Salesman sm:salesmans){
            salesmanHashMap.put(sm.getSalesmanId(),sm);
        }
        List<Commission> commissions = new ArrayList<>();
        for (Product product : products) {

            Salesman salesman = salesmanHashMap.get(product.getSalesmanId());
            salesmanRepository.save(salesman);
            productRepository.save(product);
            Double salesAmount = product.getQuantity() * product.getMrpperUnit();
            Double commissionAmount = salesAmount * (salesman.getCommissionRate() / 100.0);
            Commission commission = new Commission();
            commission.setProductName(product.getProductName());
            commission.setSalesmanName(salesman.getSalesmanName());
            commission.setQuantity(product.getQuantity());
            commission.setSalesAmount(salesAmount);
            commission.setSalesmanArea(salesman.getSalesmanArea());
            commission.setSalesmanCommission(commissionAmount);
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String strDate= formatter.format(date);
            commission.setCreatedDate(strDate);
            commissions.add(commission);}

        commissionRepository.saveAll(commissions);
        return new ResponseEntity<>(commissions, HttpStatus.OK);
    }

    @TrackExecutionTime
    @GetMapping("/sales_get")
    public ResponseEntity<List<Commission>> getAllCommissions() {
        List<Commission> sales = commissionRepository.findAll();
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }
    @TrackExecutionTime
    @GetMapping("/sales/date")
    public ResponseEntity<List<Commission>> getAllCommissionsByDate(@RequestParam("date") String date) {
        List<Commission> sales = commissionRepository.findByCreatedDateIs(date);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

}
