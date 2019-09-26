package com.abarag4;

import org.cloudbus.cloudsim.UtilizationModel;

public class MyCPUUtilizationModel implements UtilizationModel {

    private Long taskLen = null;

    public MyCPUUtilizationModel(Long taskLen) {
        this.taskLen = taskLen;
    }

    @Override
    public double getUtilization(double time) {

        if (taskLen!=null && time < taskLen) {

        }

        return 1;
    }
}
