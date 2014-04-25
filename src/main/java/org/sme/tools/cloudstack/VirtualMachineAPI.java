/**
 * 
 */
package org.sme.tools.cloudstack;

import java.io.IOException;
import java.util.List;

import org.apache.cloudstack.api.ApiConstants;
import org.json.JSONObject;
import org.sme.tools.cloudstack.model.DiskOffering;
import org.sme.tools.cloudstack.model.VirtualMachine;

import com.cloud.template.VirtualMachineTemplate.TemplateFilter;
import com.cloud.vm.VirtualMachine.State;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 24, 2014
 */
public class VirtualMachineAPI extends CloudStackAPI {
  
  public static String[] quickDeployVirtualMachine(String name, String service, String template, String disk) throws IOException {
    String zoneId = ZoneAPI.listAvailableZones().get(0).id;
    String templateId = TemplateAPI.listTemplates(TemplateFilter.all, null, template, zoneId).get(0).id;
    String serviceOfferingId = ServiceOfferingAPI.listServiceOfferings(null, service).get(0).id;
    DiskOffering dof = DiskOfferingAPI.listDiskOfferings(null, disk).get(0);
    
    StringBuilder sb = new StringBuilder("command=deployVirtualMachine&response=json");
    sb.append("&zoneid=").append(zoneId);
    sb.append("&templateid=").append(templateId);
    sb.append("&serviceofferingid=").append(serviceOfferingId);
    sb.append("&diskofferingid=").append(dof.id);
    sb.append("&size=").append(dof.diskSize);
    
    if (name != null && !name.isEmpty()) {
      sb.append("&name=").append(name);
      sb.append("&displayname=").append(name);
    }
    String response = request(sb.toString());
    JSONObject json = new JSONObject(response).getJSONObject("deployvirtualmachineresponse");
    String vmId = json.getString("id");
    String jobId = json.getString("jobid");
    return new String[] { vmId, jobId };
  }
  
  public static String  destroyVM(String id, boolean expunge) throws IOException {
    StringBuilder sb = new StringBuilder("command=destroyVirtualMachine&response=json");
    sb.append("&id=").append(id);
    sb.append("&expunge=").append(expunge);
    String response = request(sb.toString());
    JSONObject json = new JSONObject(response).getJSONObject("destroyvirtualmachineresponse");
    return json.getString("jobid");
  }

  public static List<VirtualMachine> listVirtualMachines(String name, State state, String templateId, ApiConstants.VMDetails details) throws IOException {
    StringBuilder sb = new StringBuilder("command=listVirtualMachines&response=json");
    
    if (name != null && !name.isEmpty()) 
      sb.append("&name=").append(name);
    
    if (state != null)
      sb.append("&state=").append(state);
    
    if(templateId != null && !templateId.isEmpty())
      sb.append("&templateid=").append(templateId);
    
    if (details != null)
      sb.append("&details=").append(details);
    
    String command = sb.toString();
    String response = request(command);
    return buildModels(VirtualMachine.class, response, "listvirtualmachinesresponse", "virtualmachine");
  }
}
