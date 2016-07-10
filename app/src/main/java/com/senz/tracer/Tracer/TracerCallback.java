package com.senz.tracer.Tracer;

import com.senz.tracer.Recognizable;

import java.util.List;

/**
 * Created by fengxiaoping on 4/25/16.
 */
public interface TracerCallback {

    public void onUpdate(List<Recognizable> recognizable);

}
