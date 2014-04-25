/**
 * 
 */
package org.sme.tools.cloudstack;

import java.io.IOException;

import org.json.JSONObject;
import org.sme.tools.cloudstack.model.Job;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 25, 2014
 */
public class AsyncJobAPI extends CloudStackAPI {

  public static Job queryAsyncJobResult(String jobId) throws IOException   {
    StringBuilder sb = new StringBuilder("command=queryAsyncJobResult&response=json&jobid=").append(jobId);
    String response = request(sb.toString());
    JSONObject json = new JSONObject(response).getJSONObject("queryasyncjobresultresponse");
    return buildModel(Job.class, json);
  }
}
