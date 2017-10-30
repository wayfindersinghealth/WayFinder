package sg.com.singhealth.wayfinder;

import android.os.AsyncTask;

/**
 * File Name: GHAsyncTask.java
 * Created By: AY17 P3 FYPJ NYP SIT
 * Description: -
 */

public abstract class GHAsyncTask<A, B, C> extends AsyncTask<A, B, C> {
    private Throwable error;

    protected abstract C saveDoInBackground(A... params) throws Exception;

    protected C doInBackground(A... params) {
        try {
            return saveDoInBackground(params);
        } catch (Throwable t) {
            error = t;
            return null;
        }
    }

    public boolean hasError() {
        return error != null;
    }

    public Throwable getError() {
        return error;
    }

    public String getErrorMessage() {
        if (hasError()) {
            return error.getMessage();
        }
        return "No Error";
    }
}