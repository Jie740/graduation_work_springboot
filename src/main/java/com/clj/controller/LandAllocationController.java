package com.clj.controller;

import com.clj.domain.LandAllocation;
import com.clj.domain.dto.LandAllocationDto;
import com.clj.service.LandAllocationService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/landAllocation")
public class LandAllocationController {
    private final LandAllocationService landAllocationService;

    @PostMapping("/add")
    public Result add(@RequestBody LandAllocationDto landAllocationDto) {
        return landAllocationService.addLandAllocation(landAllocationDto);
    }

    @DeleteMapping("/delete/{landAllocationId}")
    public Result delete(@PathVariable("landAllocationId") Long landAllocationId) {
        return landAllocationService.deletelandAllocation(landAllocationId) ;
    }

    @PutMapping("/updateContractor")
    public Result update(@RequestBody LandAllocationDto landAllocationDto) {
        return landAllocationService.updateLandAllocation(landAllocationDto);
    }

    @GetMapping("/getLandAllocationByPage/{pageNum}/{pageSize}")
    public Result getLandAllocationByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return landAllocationService.getLandAllocationByPage(pageNum, pageSize);
    }
    @GetMapping("/searchLandAllocationInfoByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchLandAllocationInfoByPage(@PathVariable("keyword") String keyword,
            @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return landAllocationService.searchLandAllocationByPage(keyword,pageNum, pageSize);
    }

    @GetMapping("/getContractorInfoByLandId/{landId}")
    public Result getContractorInfoByLandId(@PathVariable("landId") Long landId) {
        return landAllocationService.getContractorInfoByLandId(landId);
    }

    @GetMapping("/getMyLands")
    public Result getMyLands() {
        return landAllocationService.getMyLands();
    }

}
