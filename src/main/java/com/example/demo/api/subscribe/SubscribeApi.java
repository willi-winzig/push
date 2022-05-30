package com.example.demo.api.subscribe;

import com.example.demo.entity.Kategorie;
import com.example.demo.entity.Platform;
import com.example.demo.entity.PushCategory;
import com.example.demo.entity.PushDevice;
import com.example.demo.service.PushDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Set;

/** Meldet sich zum Push für mindestens eine Category an */
@Controller
@RequestMapping(path = "/push")
public class SubscribeApi {

    @Autowired private PushDeviceRepository pushDeviceRepository;

    @PostMapping(path = "/subscribe")
    public @ResponseBody String subscribe(@RequestBody SubscribeRequest subscribeRequest) {

        Platform platform = Platform.valueOf(subscribeRequest.platform);

        PushDevice pushDeviceMitTokenAndPlatform =
                pushDeviceRepository.findByTokenAndPlatform(subscribeRequest.token, platform);

        if (pushDeviceMitTokenAndPlatform != null) {
            if (!pushDeviceMitTokenAndPlatform.getUserid().equals(subscribeRequest.userid)) {
                pushDeviceRepository.delete(pushDeviceMitTokenAndPlatform);
            }
        }

        PushDevice pushDevice = pushDeviceRepository.findByUserid(subscribeRequest.userid);

        if (pushDevice == null) {
            pushDevice = new PushDevice(subscribeRequest.userid);
        }

        pushDevice.setPublic_key(subscribeRequest.public_key);
        pushDevice.setToken(subscribeRequest.token);
        pushDevice.setPlatform(platform);
        pushDevice.getCategories().clear();
        pushDevice.getCategories().addAll(getCategories(subscribeRequest.kategorien, pushDevice));

        pushDeviceRepository.save(pushDevice);
        return "PushDevice inserted/updated";
    }

    private Set<PushCategory> getCategories(Set<String> categories, PushDevice pushDevice) {
        Set<PushCategory> pushCategories = new HashSet<>();
        for (String c : categories) {
            Kategorie kategorie = Kategorie.valueOf(c);
            PushCategory p = new PushCategory(kategorie, pushDevice);
            pushCategories.add(p);
        }
        return pushCategories;
    }
}
