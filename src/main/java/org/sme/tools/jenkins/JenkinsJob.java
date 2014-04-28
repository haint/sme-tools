/**
 * 
 */
package org.sme.tools.jenkins;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.sme.tools.http.HttpClientFactory;
import org.sme.tools.http.HttpClientUtil;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 26, 2014
 */
public class JenkinsJob {
  
  /** .*/
  private String name;
  
  /** .*/
  private String assigned;
  
  /** .*/
  private String gitURL;
  
  /** .*/
  private String goals;
  
  /** .*/
  private String mavenOpts;
  
  /** .*/
  JenkinsMaster master;
  
  /**
   * 
   * @param name the job name
   * @param assigned the slave label
   * @param gitURL the http url of git project
   * @param goals empty string for not specified.
   * @param mavenOpts empty string for not specified.
   */
  public JenkinsJob(JenkinsMaster master, String name, String assigned, String gitURL, String goals, String mavenOpts) {
    this.name = name;
    this.assigned = assigned;
    this.gitURL = gitURL;
    this.goals = goals;
    this.mavenOpts = mavenOpts;
    this.master = master;
  }
  
  public byte[] getConsoleOutput(int buildNumber, int start) throws IOException {
    String url = master.buildURL("job/" + name + "/" + buildNumber + "/logText/progressiveHtml");
    DefaultHttpClient client = HttpClientFactory.getInstance();
    HttpContext httpContext = new BasicHttpContext();
    HttpPost post = new HttpPost(url);
    List<NameValuePair> list = new ArrayList<NameValuePair>();
    list.add(new BasicNameValuePair("start", String.valueOf(start)));
    HttpResponse res = client.execute(post, httpContext);
    return HttpClientUtil.getContentBodyAsByteArray(res);
  }
  
  public boolean isBuilding(int buildNumber) throws IOException {
    String url = master.buildURL("job/" + name + "/" + buildNumber + "/api/json");
    DefaultHttpClient client = HttpClientFactory.getInstance();
    String response = HttpClientUtil.fetch(client, url);
    JSONObject json = new JSONObject(response);
    return json.getBoolean("building");
  }
  
  public String getStatus(int buildNumber) throws IOException {
    String url = master.buildURL("job/" + name + "/" + buildNumber + "/api/json");
    DefaultHttpClient client = HttpClientFactory.getInstance();
    String response = HttpClientUtil.fetch(client, url);
    JSONObject json = new JSONObject(response);
    return json.getString("result");
  }
  
  public boolean delete() throws IOException {
    String url = master.buildURL("job/" + name + "/doDelete");
    DefaultHttpClient client = HttpClientFactory.getInstance();
    HttpContext httpContext = new BasicHttpContext();
    HttpPost post = new HttpPost(url);
    HttpResponse res = client.execute(post, httpContext);
    String body = HttpClientUtil.getContentBodyAsString(res);
    return body.length() == 0;
  }
  
  public HttpEntity buildFormData() throws IOException {
    List<NameValuePair> list = new ArrayList<NameValuePair>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("jenkins-job-template")));
    String line = null;
    while ((line = reader.readLine()) != null) {
      String[] arrays = line.split("\t");
      if (arrays.length == 2) {
        String key = arrays[0].trim();
        String value = arrays[1].trim();
        list.add(new BasicNameValuePair(key, value));
      } else {
        arrays = line.split(" ");
        if (arrays.length == 2) {
          String key = arrays[0].trim();
          String value = arrays[1].trim();
          list.add(new BasicNameValuePair(key, value));
        } else {
          System.err.println(line);
        }
      }
    }
    list.add(new BasicNameValuePair("_.assignedLabelString", assigned));
    list.add(new BasicNameValuePair("name", name));
    list.add(new BasicNameValuePair("_.url", gitURL));
    list.add(new BasicNameValuePair("goals", goals));
    list.add(new BasicNameValuePair("mavenOpts", mavenOpts));
    BufferedInputStream is = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("jenkins-job-json-template"));
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] buff = new byte[1024];
    for (int i = is.read(buff); i != -1; i = is.read(buff)) {
      bos.write(buff, 0, i);
    }
    String json = new String(bos.toByteArray());
    json = String.format(json, name, assigned, gitURL, goals);
    list.add(new BasicNameValuePair("json", json));
    return new UrlEncodedFormEntity(list);
  }
  
  public String getName() {
    return name;
  }

  public String getAssigned() {
    return assigned;
  }

  public String getGitURL() {
    return gitURL;
  }
  
  public String getGoals() {
    return goals;
  }
  
  public String getMavenOpts() {
    return mavenOpts;
  }
}
