package com.example.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.entity.Activity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.club.entity.vo.ActivityVO;
import com.example.club.entity.vo.IndexStatisticsVO;

import java.util.List;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author Xun
 * @since 2025-12-22
 */
public interface IActivityService extends IService<Activity> {

    void createActivity(Activity activity);

    List<Activity> getActivitiesByClubId(Long clubId);

    Activity checkActivityForSignup(Long activityId);

    Page<ActivityVO> listActivitiesWithStatus(int pageNum, int size, String title);

    IndexStatisticsVO getIndexStatistics();


}
