package com.abarag4;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBroker extends DatacenterBroker {
    
    private Logger LOG = LoggerFactory.getLogger(MyBroker.class);
    private ArrayList<MyCloudlet> reducerArrayList = null;
    private int countRet = 0;
    private int countRetMax = 0;

    /*
    * This Map contains the Mappers that have finished executing, but their result still needs to be processed by a reducer.
    * */
    private Map<Integer, MyCloudlet> finishedMappers = new HashMap<>();

    /*
     * This Map contains the Mappers that have finished executing and their result has been already processed by a reducer.
     * */
    private Map<Integer, MyCloudlet> handledMappers = new HashMap<>();

    public MyBroker(String name) throws Exception {
        super(name);
        reducerArrayList = new ArrayList<>();
    }

    public void submitMapperList(List<? extends MyCloudlet> list) {
        super.submitCloudletList(list);
        countRetMax = list.size();
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

            double diskSpeed = 0;

            //todo: finish documenting what this does
            if (vm.getHost() instanceof MyHost) {
                diskSpeed = ((MyHost)vm.getHost()).getDiskSpeed();
            }

            /*
            * Keep track inside cloudlets of which Host they run on. This will be useful later when scheduling them.
            * */
            if (cloudlet instanceof MyCloudlet) {

                // This cloudlet is a reducer, therefore we will now schedule it on the same host as its mappers
                if (((MyCloudlet) cloudlet).getType()== MyCloudlet.Type.REDUCER) {
                    List<Integer> mapperIds = ((MyCloudlet) cloudlet).getAssociatedMappers();

                    Host mpHost = handledMappers.get(mapperIds.get(0)).getHost();
                    ((MyCloudlet) cloudlet).setHost(mpHost);
                    cloudlet.setVmId(mpHost.getVmList().get(0).getId());

                    /*
                    * If all mappers are on the same host, then there is no delay in starting the reducers because no data needs to be transferred.
                    * */
                    if (haveSameHost(mapperIds)) {
                        diskSpeed = 0;
                    }

                } else {
                    ((MyCloudlet) cloudlet).setHost(vm.getHost());
                    cloudlet.setVmId(vm.getId());
                }

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

    /*
    * This method checks that all mappers in this list run on the same host
    * */
    private boolean haveSameHost(List<Integer> mapperIds) {
        Host host = null;
        for (Integer id : mapperIds) {

            Host currentHost = handledMappers.get(id).getHost();

            if (host==null) {
                host = currentHost;
            } else if (host!=currentHost) {
                return false;
            }
        }
        return true;
    }

    private Map<Host, List<Integer>> toAllocationMap(Map<Integer, MyCloudlet> cloudlets) {

        Map<Host, List<Integer>> allocHost = new HashMap<>();

        for (MyCloudlet myCloudlet : cloudlets.values()) {
            Host host = myCloudlet.getHost();
            /*
             * Count the number of mappers allocated on a certain host.
             * */
            if (allocHost.containsKey(host)) {
                List<Integer> myCloudletList = allocHost.get(host);
                allocHost.get(host).add(myCloudlet.getCloudletId());
                allocHost.replace(host, myCloudletList);
            } else {
                List<Integer> myCloudletList = new ArrayList<>();
                myCloudletList.add(myCloudlet.getCloudletId());
                allocHost.put(host, myCloudletList);
            }
        }

        return allocHost;
    }

    /**
     * @param ev SimEvent ev
     *
     * This method receives events which outline the different stages of the simulation.
     *
     * The changes made here allow for the simulation of a map/reduce architecture; in particular this method takes into account 2 different types
     * of cloudlets (Mappers and Reducers).
     * The scheduling policy assured data locality which prevents delay in loading cloudlets due to limited disk speed.
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
                        countRet++; //When a Mapper cloudlet finishes, keep track of this.
                        finishedMappers.put(current.getCloudletId(), current); //Add this mapper to list of finished mappers
                    }

                    //There are still reducers ready to submit. Submit one and remove from pending list.
                        if (reducerArrayList.size()>0) {

                            /*
                            * Now we need to decide where to submit this reducer.
                            *
                            * Let's cycle over all finishedMappers to find out where they were allocated.
                            *
                            * */

                            /*
                            * Generate an allocation Map of mappers that have finished executing
                            * */
                            Map<Host, List<Integer>> allocHost = toAllocationMap(finishedMappers);
                            for (Host host : allocHost.keySet()) {

                                MyCloudlet readyReducer = reducerArrayList.get(0);

                                List<Integer> mapperIds = allocHost.get(host);

                                //LOG.debug("AllocationMap | hostId: "+host.getId()+", numAlloc: "+allocHost.get(host));

                                /*
                                * If at least 2 mappers are done and on the *same* host, we can submit a reducer for them.
                                * */
                                if (allocHost.get(host).size()>=2) {
                                    LOG.debug("--> Now submitting reducer for MAPPERS: "+mapperIds + ", countRet is now: "+ countRet);
                                    readyReducer.setAssociatedMappers(mapperIds);

                                    /*
                                    * Remove mappers with already processed output from finishedMappers list.
                                    * Add them to handedMappers so later they can be retrieved by id.
                                    * */
                                    for (Integer id : mapperIds) {
                                        handledMappers.put(id, finishedMappers.get(id));
                                        finishedMappers.remove(id);
                                    }

                                    LOG.debug("Submitted cloudlet with ID: "+readyReducer.getCloudletId());
                                    getCloudletList().add(readyReducer); //Add to pending list
                                    reducerArrayList.remove(0); //Remove from waiting list
                                }

                                LOG.debug("Mappers n. waiting to be reduced is now: "+finishedMappers.keySet());
                            }

                            /*
                            * This means all mappers have been returned, but some are mismatched
                            * -> they run on different hosts, so we now need to move data and submit the remaining reducers
                            * */
                            if (countRet==countRetMax && finishedMappers.size()>0 && reducerArrayList.size()>0) {

                                MyCloudlet readyReducer = reducerArrayList.get(0);

                                LOG.debug("--> Submitting remaining reducers ("+finishedMappers.keySet()+")");
                                List<Integer> leftOverMappers = new ArrayList<>(finishedMappers.keySet());
                                readyReducer.setAssociatedMappers(leftOverMappers);

                                handledMappers.putAll(finishedMappers);
                                finishedMappers.clear();

                                LOG.debug("Submitted cloudlet with ID: "+readyReducer.getCloudletId());
                                getCloudletList().add(readyReducer); //Add to pending list
                                reducerArrayList.remove(0); //Remove from waiting list
                            }
                        }

                        submitCloudlets();

                    processCloudletReturn(ev);

                }

                break;
            default:
                super.processEvent(ev);
        }
    }
}
