package com.abarag4;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class DefaultPolicyBroker extends DatacenterBroker {

    private Logger LOG = LoggerFactory.getLogger(MyBroker.class);
    private ArrayList<MyCloudlet> reducerArrayList = null;
    private int countRet = 0;
    private static final int MAP_RED_RATIO = 3;

    public DefaultPolicyBroker(String name) throws Exception {
        super(name);
        reducerArrayList = new ArrayList<>();
    }

    public void submitMapperList(List<? extends MyCloudlet> list) {
        super.submitCloudletList(list);
    }

    public void submitReducerList(List<? extends MyCloudlet> list) {
        reducerArrayList.addAll(list);
    }

    public ArrayList<MyCloudlet> getReducerArrayList() {
        return reducerArrayList;
    }

    //todo: added cloudlets get added to wrong VM
    private int vmIndex = 0;

    /**
     * submitCloudlets()
     *
     * This method is responsible for assigning cloudlets to VMs and submitting them for execution.
     *
     * The following changes were made w.r.t. the original implementation by cloudsim:
     * - The time taken to copy the chunk of data to the local storage of the host is taken into account by delaying the cloudlet startup by:
     * cloudlet.getCloudletFileSize()*diskSpeed
     * - vmIndex has been extracted from the method and made private -> this allows for multiple calls to submitCloudlets() on the same broker.
     *
     */
    @Override
    protected void submitCloudlets() {
        List<Cloudlet> successfullySubmitted = new ArrayList<Cloudlet>();
        for (Cloudlet cloudlet : getCloudletList()) {

            Vm vm;
            // if user didn't bind this cloudlet and it has not been executed yet
            if (cloudlet.getVmId() == -1) {
                vm = getVmsCreatedList().get(vmIndex);
            } else { // submit to the specific vm
                vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
                if (vm == null) { // vm was not created
                    if(!Log.isDisabled()) {
                        Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Postponing execution of cloudlet ",
                                cloudlet.getCloudletId(), ": bount VM not available");
                    }
                    continue;
                }
            }

            if (!Log.isDisabled()) {
                Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Sending cloudlet ",
                        cloudlet.getCloudletId(), " to VM #", vm.getId());
            }

            //MyCPUUtilizationModel cpuUtilizationModel = new MyCPUUtilizationModel(cloudlet.getCloudletFileSize(), cloudlet.get)
            //Set CPU utilization model, this simulates the CPU being idle while data are loaded into the local storage disk.
            //cloudlet.setUtilizationModelCpu(cpuUtilizationModel);

            ((MyCloudlet) cloudlet).setHost(vm.getHost());
            cloudlet.setVmId(vm.getId());
            double diskSpeed = 0;

            //todo: finish documenting what this does
            if (vm.getHost() instanceof MyHost) {
                diskSpeed = ((MyHost)vm.getHost()).getDiskSpeed();
            }

            if (cloudlet instanceof MyCloudlet) {
                ((MyCloudlet) cloudlet).setDelay(cloudlet.getCloudletFileSize()*diskSpeed);
            }

            send(getVmsToDatacentersMap().get(vm.getId()), cloudlet.getCloudletFileSize()*diskSpeed, CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            cloudletsSubmitted++;
            vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
            getCloudletSubmittedList().add(cloudlet);
            successfullySubmitted.add(cloudlet);
        }

        // remove submitted cloudlets from waiting list
        getCloudletList().removeAll(successfullySubmitted);
    }

    /**
     * @param ev SimEvent ev
     *
     * This method receives events which outline the different stages of the simulation.
     *
     * The changes made here allow for the simulation of a map/reduce architecture; in particular this method takes into account 2 different types
     * of cloudlets (Mappers and Reducers). The scheduling policy is changed, in particular, every 3 mappers that finish the execution a new reducer is started
     * that operates on the results of the mappers. (This provides some sort of data dependency)
     *
     */
    @Override
    public void processEvent(SimEvent ev) {

        switch (ev.getTag()) {
            case CloudSimTags.CLOUDLET_RETURN:

                Object obj = ev.getData();
                if (obj!=null && obj instanceof MyCloudlet) {
                    MyCloudlet current = (MyCloudlet) ev.getData();
                    if (current.getType() == MyCloudlet.Type.MAPPER) {
                        countRet++; //If a Mapper cloudlet finishes, keep track of this.
                    }

                    //now submit reducers
                    if (countRet==MAP_RED_RATIO) {
                        LOG.debug("countRet reached. Submitting new reducers.");
                        //There are still reducers ready to submit. Submit one and remove from pending list.
                        if (reducerArrayList.size()>0) {
                            LOG.debug("Submitted cloudlet with ID: "+reducerArrayList.get(0).getCloudletId());
                            getCloudletList().add(reducerArrayList.get(0)); //Add to pending list
                            reducerArrayList.remove(0); //Remove from waiting list
                        }

                        submitCloudlets();
                        countRet=0; //Reset mapper counter
                    }

                    processCloudletReturn(ev);

                }

                break;
            default:
                super.processEvent(ev);
        }
    }
}
