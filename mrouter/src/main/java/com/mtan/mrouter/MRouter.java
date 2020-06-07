package com.mtan.mrouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MRouter {

    private static Map<String, Class<?>> sRouteMap = new HashMap<>();

    public static void init(Context context) {
        try {
            Set<String> names = ClassUtils.getFileNameByPackageName(context, "com.mtan.mrouter.apt");
            for (String name : names) {
                Class<?> clazz = Class.forName(name);
                Object obj = clazz.newInstance();
                Method method = clazz.getDeclaredMethod("loadInto", Map.class);
                method.invoke(obj, sRouteMap);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Context mContext;

    public MRouter(Context context) {
        mContext = context;
    }

    public void navigation(String path) {
        Class<?> clazz = sRouteMap.get(path);
        if (clazz == null) {
            throw new RuntimeException("can't find class for path:" + path);
        }
        Intent intent = new Intent(mContext, clazz);
        if (mContext instanceof Activity) {
            mContext.startActivity(intent);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }
}
