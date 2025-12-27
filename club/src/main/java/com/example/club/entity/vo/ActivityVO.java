package com.example.club.entity.vo;

import com.example.club.entity.Activity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityVO extends Activity {


    private Boolean isSignedUp;


    private String clubName;


    private Integer currentPeople;
}