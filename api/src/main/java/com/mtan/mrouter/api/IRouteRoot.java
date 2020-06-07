package com.mtan.mrouter.api;

import java.util.Map;

public interface IRouteRoot {

    void loadInto(Map<String, Class<?>> routes);
}