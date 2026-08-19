package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.Land;
import com.clj.service.LandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/land")
public class LandController {
    private final LandService landService;

    @PostMapping("/addLand")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> addLand(@RequestBody Land land) {
        landService.addLand(land);
        return Result.success();
    }

    @DeleteMapping("/deleteLand/{landId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> deleteLand(@PathVariable("landId") Long landId) {
        landService.deleteLand(landId);
        return Result.success();
    }

    @PutMapping("/updateLand")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> updateLand(@RequestBody Land land) {
        landService.updateLand(land);
        return Result.success();
    }

    @GetMapping("/getLandsByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<Land>> getLandsByPage(@PathVariable("pageNum") Integer pageNum,@PathVariable("pageSize") Integer pageSize) {
        return Result.success(landService.getLandsByPage(pageNum, pageSize));
    }
    @GetMapping("/searchLandsByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<Land>> searchLandsByPage(@PathVariable("keyword") String keyword
            ,@PathVariable("pageNum") Integer pageNum,@PathVariable("pageSize") Integer pageSize) {
        return Result.success(landService.searchLandsByPage(keyword, pageNum, pageSize));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<List<Land>> getAll() {
        return Result.success(landService.getAll());
    }

}
