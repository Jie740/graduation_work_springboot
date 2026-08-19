package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.LandAllocation;
import com.clj.domain.dto.LandAllocationDto;
import com.clj.domain.vo.ContractorInfoVo;
import com.clj.domain.vo.ContractorLandVo;
import com.clj.domain.vo.LandAllocationVo;
import com.clj.service.LandAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/landAllocation")
public class LandAllocationController {
    private final LandAllocationService landAllocationService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(@RequestBody LandAllocationDto landAllocationDto) {
        landAllocationService.addLandAllocation(landAllocationDto);
        return Result.success();
    }

    @DeleteMapping("/delete/{landAllocationId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("landAllocationId") Long landAllocationId) {
        landAllocationService.deletelandAllocation(landAllocationId) ;
        return Result.success();
    }

    @PutMapping("/updateContractor")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody LandAllocationDto landAllocationDto) {
        landAllocationService.updateLandAllocation(landAllocationDto);
        return Result.success();
    }

    @GetMapping("/getLandAllocationByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<LandAllocationVo>> getLandAllocationByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(landAllocationService.getLandAllocationByPage(pageNum, pageSize));
    }
    @GetMapping("/searchLandAllocationInfoByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<LandAllocationVo>> searchLandAllocationInfoByPage(@PathVariable("keyword") String keyword,
            @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(landAllocationService.searchLandAllocationByPage(keyword,pageNum, pageSize));
    }

    @GetMapping("/getContractorInfoByLandId/{landId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<ContractorInfoVo> getContractorInfoByLandId(@PathVariable("landId") Long landId) {
        return Result.success(landAllocationService.getContractorInfoByLandId(landId));
    }

    @GetMapping("/getMyLands")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<List<ContractorLandVo>> getMyLands() {
        return Result.success(landAllocationService.getMyLands());
    }

}
