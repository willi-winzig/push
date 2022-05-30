package com.example.demo.api.order;

import com.example.demo.entity.Kategorie;
import com.example.demo.entity.PushDevice;
import com.example.demo.entity.PushOrder;
import com.example.demo.service.PushDeviceRepository;
import com.example.demo.service.PushOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/push")
public class OrderApi {

    @Autowired
    private PushOrderRepository pushOrderRepository;
    @Autowired
    private PushDeviceRepository pushDeviceRepository;

    @PostMapping(path = "/order")
    public @ResponseBody String order(@RequestBody OrderRequest orderRequest) {

        PushDevice pushDevice = pushDeviceRepository.findByUserid(orderRequest.userid);
        if (pushDevice != null) {
            if (!pushDevice.getCategoriesAsStringSet().contains(orderRequest.category)) {
                return "User not subcribed to category";
            }
        } else {
            return "user not found";
        }

        PushOrder pushOrder =
                new PushOrder(
                        orderRequest.userid,
                        Kategorie.valueOf(orderRequest.category),
                        orderRequest.param_name);
        pushOrderRepository.save(pushOrder);
        return "order erstellt";
    }
}
