import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.List;

public class MyDatacenter extends Datacenter {
    /**
     * Allocates a new PowerDatacenter object.
     *
     * @param name               the name to be associated with this entity (as required by Sim_entity class from
     *                           simjava package)
     * @param characteristics    an object of DatacenterCharacteristics
     * @param vmAllocationPolicy the vmAllocationPolicy
     * @param storageList        a LinkedList of storage elements, for data simulation
     * @param schedulingInterval
     * @throws Exception This happens when one of the following scenarios occur:
     *                   <ul>
     *                   <li>creating this entity before initializing CloudSim package
     *                   <li>this entity name is <tt>null</tt> or empty
     *                   <li>this entity has <tt>zero</tt> number of PEs (Processing Elements). <br>
     *                   No PEs mean the Cloudlets can't be processed. A CloudResource must contain one or
     *                   more Machines. A Machine must contain one or more PEs.
     *                   </ul>
     * @pre name != null
     * @pre resource != null
     * @post $none
     */
    public MyDatacenter(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
    }
}
