package com.example.club.controller;

import com.example.club.common.Result;
import com.example.club.entity.vo.IndexStatisticsVO;
import com.example.club.service.IActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexController {
    @Autowired
    private IActivityService activityService;

    @GetMapping("/statistics")
    public Result<IndexStatisticsVO> getStatistics() {
        return Result.success(activityService.getIndexStatistics());
    }
}
