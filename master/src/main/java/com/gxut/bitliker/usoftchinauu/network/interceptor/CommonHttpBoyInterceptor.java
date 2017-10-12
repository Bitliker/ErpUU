package com.gxut.bitliker.usoftchinauu.network.interceptor;

import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.model.User;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.code.network.util.JSONUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 公共请求头
 * Created by Bitliker on 2017/6/28.
 */

public class CommonHttpBoyInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        User user = AppConfig.api().getLoginUser();
        String url = request.url().toString();
        if (user != null && !user.isEmpty()) {
            String method = request.method();
            String emCode = user.getEmCode();
            String emMaster = user.getMaster();
            String emSessionId = user.getSessionId();
            Map<String, Object> params = new HashMap<>();
            if (!Utils.isEmpty(emCode)) {
                params.put("sessionUser", emCode);
                requestBuilder.addHeader("sessionUser", emCode);
            }
            if (!Utils.isEmpty(emMaster)) {
                params.put("master", emMaster);
                requestBuilder.addHeader("sessionUser", emMaster);
            }
            if (!Utils.isEmpty(emSessionId)) {
                params.put("sessionId", emSessionId);
                requestBuilder.addHeader("Cookie", "JSESSIONID=" + emSessionId);
            }
            if (!Utils.isEmpty(params)) {
                if (method.equals("GET")) {
                    url = JSONUtil.param2Url(url, params);
                    requestBuilder.url(url);
                } else if ("POST".equals(method)) {
                    String postBodyString = bodyToString(request.body());
                    FormBody.Builder formBodyBuilder = new FormBody.Builder();
                    if (!Utils.isEmpty(params)) {
                        for (Map.Entry<String, Object> entry : params.entrySet()) {
                            if (!postBodyString.contains(entry.getKey())) {
                                formBodyBuilder.add(entry.getKey(), (String) entry.getValue());
                            }
                        }
                    }
                    RequestBody formBody = formBodyBuilder.build();
                    postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
                    requestBuilder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), postBodyString));
                }
            }
        }
        request = requestBuilder.build();
        return chain.proceed(request);
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }


}
