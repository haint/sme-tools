/**
 * 
 */
package org.sme.tools.cloudstack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sme.tools.cloudstack.model.AbstractModel;
import org.sme.tools.http.HttpClientFactory;
import org.sme.tools.http.HttpClientUtil;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Nguyen Thanh Hai</a>
 *
 * Apr 24, 2014
 */
public abstract class CloudStackAPI {
  
  protected static String request(String command) throws IOException {
    CloudStackClient client = CloudStackClient.getInstance();
    String url = client.buildCommandURL(command);
    DefaultHttpClient http = HttpClientFactory.getInstance();
    return HttpClientUtil.fetch(http, url.replaceAll(" ", "%20"));
  }
  
  protected static <T extends AbstractModel> List<T> buildModels(Class<T> clazz, String response, String rootNode, String arrayNode, ModelFilter<T> filter) throws JsonProcessingException, JSONException, IOException {
    JSONObject json = new JSONObject(response).getJSONObject(rootNode);
    
    if (json.has("count")) {
      int size = json.getInt("count");
      JSONArray array = json.getJSONArray(arrayNode);
      List<T> models = new ArrayList<T>();
      for (int i = 0; i < size; i++) {
        T model = CloudStackAPI.<T>buildModel(clazz, array.getJSONObject(i));
        if (filter == null)
          models.add(model);
        else 
          filter.doFilter(models, model);
      }
      return models;
    }
    
    return Collections.<T>emptyList();
  }
  
  protected static <T extends AbstractModel> List<T> buildModels(Class<T> clazz, String response, String rootNode, String arrayNode) throws JsonProcessingException, JSONException, IOException {
    return buildModels(clazz, response, rootNode, arrayNode, null);
  }
  
  protected static <T extends AbstractModel> T buildModel(Class<T> clazz, JSONObject json) throws JsonProcessingException, IOException {
    ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    ObjectReader reader = mapper.reader(clazz);
    T obj = reader.readValue(json.toString());
    return obj;
  }
}
