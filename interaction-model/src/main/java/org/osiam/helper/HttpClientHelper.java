/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.helper;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Named("httpClientHelper")
public class HttpClientHelper {

    private HttpClient client; //NOSONAR : need to mock therefore the final identifier was removed

    public HttpClientHelper() {
        PoolingClientConnectionManager poolingClientConnectionManager = new PoolingClientConnectionManager();
        client = new DefaultHttpClient(poolingClientConnectionManager);
    }

    public HttpClientRequestResult executeHttpGet(String url) {
        HttpClientRequestResult result;
        final HttpGet request = new HttpGet(url);

        try {
            HttpResponse response = client.execute(request);
            String responseBody = getResponseBody(response);
            int statusCode = response.getStatusLine().getStatusCode();
            result = new HttpClientRequestResult(responseBody, statusCode);
        } catch (IOException e) {
            throw new RuntimeException(e); //NOSONAR : Need only wrapping to a runtime exception
        }
        return result;
    }

    public HttpClientRequestResult executeHttpPut(String url, String parameterName, String parameterValue) {
        HttpClientRequestResult result;
        final HttpPut request = new HttpPut(url);
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair(parameterName, parameterValue));

        try {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            String responseBody = getResponseBody(response);
            int statusCode = response.getStatusLine().getStatusCode();
            result = new HttpClientRequestResult(responseBody, statusCode);
        } catch (IOException e) {
            throw new RuntimeException(e); //NOSONAR : Need only wrapping to a runtime exception
        }
        return result;
    }

    private String getResponseBody(HttpResponse response) throws IOException {
        BufferedReader rd = null;
        final StringBuffer stringBuffer = new StringBuffer("");

        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
        } finally {
            IOUtils.closeQuietly(rd);
        }

        return stringBuffer.toString();
    }
}