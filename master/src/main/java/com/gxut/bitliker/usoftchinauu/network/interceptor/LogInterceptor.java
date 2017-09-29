package com.gxut.bitliker.usoftchinauu.network.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gxut.bitliker.baseutil.util.JSONUtil;
import com.gxut.bitliker.baseutil.util.LogUtil;
import com.gxut.bitliker.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.db.dao.UserDao;
import com.gxut.bitliker.usoftchinauu.model.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 公共请求头
 * Created by Bitliker on 2017/6/28.
 */
public class LogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        MediaType mediaType = response.body().contentType();
        String message = response.body().string();
        Response resultResponse = response.newBuilder().body(ResponseBody.create(mediaType, message)).build();
        if (Utils.showLogAble())
            showLog(request, message);
        saveSessionid(message);
        return resultResponse;
    }

    private void showLog(Request request, String message) {
        String url= request.url().toString();
        LogUtil.i("http",request.method() + ":" +getMessage(url));
        if ("POST".equals(request.method())) {
            String postBodyString = null;
            if (request.body() instanceof FormBody) {
                FormBody requestBody = (FormBody) request.body();
                Map<String, Object> body = new HashMap<>();
                for (int i = 0; i < requestBody.size(); i++) {
                    body.put(requestBody.encodedName(i), requestBody.encodedValue(i));
                }
                postBodyString = JSONUtil.mapToJson(body);
            } else {
                postBodyString = bodyToString(request.body());
            }
            LogUtil.i("http","body=" + getMessage(postBodyString));
        }
        Headers headers = request.headers();
        if (headers != null && headers.size() > 0) {
            Map<String, Object> header = new HashMap<>();
            for (String name : headers.names()) {
                header.put(name, headers.get(name));
            }
            LogUtil.i("http","header=" + JSONUtil.mapToJson(header));
        }
        LogUtil.i("http",message);
    }

    private void saveSessionid(String message) {
        try {
            if (!Utils.isEmpty(message)) {
                if (JSONUtil.validatorJson(message)) {
                    Object o = JSON.parse(message);
                    if (o instanceof JSONObject) {
                        String sessionId = JSONUtil.getText((JSONObject) o, "sessionId");
                        if (!Utils.isEmpty(sessionId)) {
                            User user = AppConfig.api().getLoginUser();
                            if (user != null) {
                                if (!user.getSessionId().equals(sessionId)) {
                                    user.setSessionId(sessionId);
                                    UserDao.api().updataUser(user);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (e != null)
                LogUtil.i("http","saveSessionid=" + e.getMessage());
        }
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


    private String getMessage(String message) {
        try {
            return URLDecoder.decode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtil.i("http","showMessage=" + e.getMessage());
            e.printStackTrace();
            if (e != null)
                return e.getMessage();
            else return "";
        }
    }
}
