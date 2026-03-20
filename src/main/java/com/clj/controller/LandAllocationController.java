package com.clj.controller;

import com.clj.domain.LandAllocation;
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
    public Result add(@RequestBody LandAllocation landAllocation) {
        return landAllocationService.addLandAllocation(landAllocation);
    }

    @DeleteMapping("/delete/{landAllocationId}")
    public Result delete(@PathVariable("landAllocationId") Long landAllocationId) {
        return landAllocationService.deletelandAllocation(landAllocationId) ;
    }

    @PutMapping("/updateContractor")
    public Result update(@RequestBody LandAllocation landAllocation) {
        return landAllocationService.updateLandAllocation(landAllocation);
    }

//    @GetMapping("/getLandAllocationByPage/{pageNum}/{pageSize}")
//    public Result getLandAllocationByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
//        return landAllocationService.getLandAllocationByPage(pageNum, pageSize);
//    }

    @GetMapping("/getContractorInfoByLandId/{landId}")
    public Result getContractorInfoByLandId(@PathVariable("landId") Long landId) {
        return landAllocationService.getContractorInfoByLandId(landId);
    }

}
