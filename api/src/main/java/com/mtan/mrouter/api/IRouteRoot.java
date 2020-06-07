package com.mtan.mrouterapp.api;

import java.util.Map;

public interface IRouteRoot {

    void loadInto(Map<String, Class<?>> routes);
}