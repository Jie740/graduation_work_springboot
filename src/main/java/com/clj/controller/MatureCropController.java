package com.clj.controller;

import com.clj.domain.MatureCrop;
import com.clj.service.CropService;
import com.clj.service.LandService;
import com.clj.service.MatureCropService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matureCrop")
public class MatureCropController {
    final MatureCropService matureCropService;


    @GetMapping("/getMatureCropsByPage/{pageNum}/{pageSize}")
    public Result getMatureCropsByPage(@PathVariable("pageNum") Integer pageNum,
                                       @PathVariable("pageSize") Integer pageSize){
        return matureCropService.getMatureCropsByPage(pageNum, pageSize);
    }

    @PutMapping("/update")
    public Result update(@RequestBody MatureCrop matureCrop){
        return matureCropService.updateById(matureCrop)?Result.ok():Result.error("更新失败");
    }

    @DeleteMapping("/delete/{matureCropId}")
    public Result delete(@PathVariable("matureCropId") Integer id){
        return matureCropService.removeById(id)?Result.ok():Result.error("删除失败");
    }
}
