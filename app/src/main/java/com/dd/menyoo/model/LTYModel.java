package com.dd.menyoo.model;

/**
 * Created by Administrator on 4/13/2016.
 */
public class LTYModel {
    private int RewardOnstamps;
    private String  Requirements;
    private String claimMessage;

    public LTYModel(int rewardOnstamps, String requirements,String claimMessage) {
        RewardOnstamps = rewardOnstamps;
        Requirements = requirements;
        this.claimMessage = claimMessage;
    }

    public int getRewardOnstamps() {
        return RewardOnstamps;
    }

    public String getRequirements() {
        return Requirements;
    }

    public String getClaimMessage() {
        return claimMessage;
    }
}
