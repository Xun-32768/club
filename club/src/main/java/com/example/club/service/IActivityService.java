package com.example.club.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.club.entity.Activity;
import com.baomidou.mybatisplus.extension.service.IService;

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

    Page<Activity> listActivities(int page, int size, String title);

    List<Activity> getActivitiesByClubId(Long clubId);

    Activity checkActivityForSignup(Long activityId);
}
