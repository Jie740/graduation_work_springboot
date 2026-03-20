package com.clj.controller;

import com.clj.domain.Land;
import com.clj.service.LandService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/land")
public class LandController {
    private final LandService landService;

    @PostMapping("/addLand")
    public Result addLand(@RequestBody Land land) {
        System.out.println(land);
        return landService.addLand(land);
    }

    @DeleteMapping("/deleteLand/{landId}")
    public Result deleteLand(@PathVariable("landId") Long landId) {
        return landService.deleteLand(landId);
    }

    @PutMapping("/updateLand")
    public Result updateLand(@RequestBody Land land) {
        return landService.updateLand(land);
    }

    @GetMapping("/getLandsByPage/{pageNum}/{pageSize}")
    public Result getLandsByPage(@PathVariable("pageNum") Integer pageNum,@PathVariable("pageSize") Integer pageSize) {
        return landService.getLandsByPage(pageNum, pageSize);
    }
    @GetMapping("/searchLandsByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchLandsByPage(@PathVariable("keyword") String keyword
            ,@PathVariable("pageNum") Integer pageNum,@PathVariable("pageSize") Integer pageSize) {
        return landService.searchLandsByPage(keyword, pageNum, pageSize);
    }

    @GetMapping("/getAll")
    public Result getAll() {
        return landService.getAll();
    }

}
