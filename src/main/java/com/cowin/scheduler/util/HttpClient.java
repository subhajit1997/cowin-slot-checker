package com.cowin.scheduler.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component
public class HttpClient {

	private static final Logger LOGGER=LoggerFactory.getLogger(HttpClient.class);
	private static CloseableHttpClient httpClient;

	@Value("${http.maxHttpConnections}")
    private static int maxConnections = 1000;
    
    @Value("${http.maxHttpConnectionsPerRoute}")
    private static int maxConnectionsPerRoute = 500;

    @Value("${response.content.type}")
    private String responseContentType;

    @Value("${request.socket.timeout}")
    private int socketTimeout;

    @Value("${request.accepts}")
    private String requestAccepts;

    @Value("${request.connection.timeout}")
    private int connectionTimeout;

    @Value("${request.content.type}")
    private String requestContentType;

    public HttpClient() {
        httpClient = getHttpClientInstance();
    }

    private static CloseableHttpClient getHttpClientInstance() {
        if (httpClient == null) {
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
            poolingHttpClientConnectionManager.setMaxTotal(maxConnections);
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
            httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
        }
        return httpClient;
    }

    
	public String httpGet(URL url) {
		HttpGet httpGet=new HttpGet(url.toString());
		httpGet.setHeader(HttpHeaders.ACCEPT, requestAccepts);
		httpGet.setHeader(HttpHeaders.CONTENT_TYPE,requestContentType);		
		String stringResponse=null;
		try {
			HttpResponse httpResponse=httpClient.execute(httpGet);

			
			if(httpResponse.getStatusLine().getStatusCode()==200) {
				stringResponse=parseResponse(httpResponse);
			}
		} catch(Exception e) {
			LOGGER.error("Error processing the request for the URL"+e);
		}
		return stringResponse;
		
	}


	private String parseResponse(HttpResponse httpResponse) throws IOException {
        String result = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        result = sb.toString();
        reader.close();
        return result;
	}
	
}
