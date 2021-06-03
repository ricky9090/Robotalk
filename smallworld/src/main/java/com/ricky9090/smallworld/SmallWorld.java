package com.ricky9090.smallworld;

import android.app.Application;

import com.ricky9090.smallworld.display.IScreenClient;
import com.ricky9090.smallworld.obj.SmallByteArray;
import com.ricky9090.smallworld.obj.SmallObject;

import java.io.InputStream;

/**
 * I am the entry point for the SmallWorld system.
 * <p>
 * All I do is to set up the SmallInterpreter, tell it
 * to load the image from the jar file and then to find
 * an entry point to start an initial doIt of
 * "SmallWorld startUp".
 */

public class SmallWorld extends Thread {
    public static final String DEFAULT_IMAGE_FILE = "image";

    public static final Object RENDER_LOCK = new Object();

    private final Application app;
    private final SmallInterpreter theInterpreter;
    public boolean running = true;

    public static SmallWorld init(Application application) {
        return new SmallWorld(application);
    }

    public SmallWorld(Application application) {
        app = application;
        theInterpreter = new SmallInterpreter();
    }

    @Override
    public void run() {
        boot();
    }

    public void connectScreenClient(IScreenClient client) {
        theInterpreter.screen.bindClient(client);
    }

    public void boot() {
        boolean done = false;

        try {
            InputStream inputStream = app.getAssets().open(DEFAULT_IMAGE_FILE);
            done = theInterpreter.loadImageFromInputStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            Logger.LOG_E("Exception: " + e.toString(), e);
        }


        if (done) {
            doIt("SmallWorld startUp");
        }
    }

    private void doIt(String task) {
        // start from the basics
        SmallObject TrueClass = theInterpreter.trueObject.objClass;
        SmallObject name = TrueClass.data[0]; // a known string
        SmallObject StringClass = name.objClass;
        // now look for the method
        SmallObject methods = StringClass.data[2];
        SmallObject doItMeth = null;
        for (int i = 0; i < methods.data.length; i++) {
            SmallObject aMethod = methods.data[i];
            if ("doIt".equals(aMethod.data[0].toString()))
                doItMeth = aMethod;
        }
        if (doItMeth == null) {
            Logger.LOG_D("can't find do it!!");
        } else {
            SmallByteArray rec = new SmallByteArray(StringClass, task);
            SmallObject args = new SmallObject(theInterpreter.ArrayClass, 1);
            args.data[0] = rec;
            SmallObject ctx = theInterpreter.buildContext(theInterpreter.nilObject, args, doItMeth);
            try {
                SmallInterpreter.ActionContext actionTask = new SmallInterpreter.ActionContext(theInterpreter, ctx);
                //theInterpreter.execute(ctx, null, null);
                theInterpreter.taskManager.postTask(actionTask);
            } catch (Exception ex) {
                Logger.LOG_E("caught exeception " + ex.toString(), ex);
            }
        }

    }
}
