package alphavantage;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class AlphaVantageApi {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final String API_KEY = "AZ35ESNS50ESUG75";
    private static final String BASE_URL = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&interval=1min&symbol=%s&apikey=" + API_KEY;

    private static final String[] SYM_LIST = {"MSFT", "GOOG", "TM", "BAC", "VOD", "CSCO", "KO", "WFC", "UBS", "MTU"};


    public HttpEntity getSymListData(String symbol) throws IOException {
        HttpGet request = new HttpGet(String.format(BASE_URL, symbol));
        // add request headers
        CloseableHttpResponse response = httpClient.execute(request);
        // Get HttpResponse Status
        System.out.println(response.getStatusLine().toString());

        HttpEntity entity = response.getEntity();
        Header headers = entity.getContentType();
        System.out.println(headers);

        return entity;


    }

    public Map<String, Map<String,Object>> getStoredSymbolResults() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        Map<String, Map<String,Object>> map = new HashMap<String, Map<String,Object>>();
        for (String sym : SYM_LIST) {
            HttpEntity entity = getSymListData(sym);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(
                    new InputStreamReader(entity.getContent(), "UTF-8"));

            Map dateMap = (Map) jsonObject.get("Time Series (1min)");

            if (dateMap != null) {
                Set<Object> objectSet = dateMap.keySet();
                List<Object> objectList = objectSet.stream().collect(Collectors.toList());
                Map<String, Object> newData = new HashMap<>();
                for (int i = objectList.size() - 3; i < objectList.size(); i++) {
                    newData.put((String) objectList.get(i),dateMap.get(objectList.get(i)));
                }
                map.put(sym,newData);



            }


        }
        return map;


    }

}
