package com.example.demo.api.unsubscribe;

import com.example.demo.entity.PushDevice;
import com.example.demo.service.PushDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** Löscht die Push-Anmeldung inkl. aller Categories */
@Controller
@RequestMapping(path = "/push")
public class UnsubscribeApi {

    @Autowired private PushDeviceService pushDeviceService;

    @PostMapping(path = "/unsubscribe")
    public @ResponseBody String subscribe(@RequestBody UnsubscribeRequest unsubscribeRequest) {

        PushDevice pushDeviceDesUsers = pushDeviceService.findByUserid(unsubscribeRequest.userid);
        if (pushDeviceDesUsers != null) {
            pushDeviceService.delete(pushDeviceDesUsers);
            return "unsubscribe with delete";
        }
        return "unsubscribe without delete";
    }
}
