package hu.opau.voltvault;

import android.os.AsyncTask;

import hu.opau.voltvault.controller.BasketController;

public class WaitForBasketTask extends AsyncTask<Void, Void, WaitForBasketTask.BasketAsyncResult> {

    private BasketAsyncCallback cb;

    public WaitForBasketTask(BasketAsyncCallback cb) {
        this.cb = cb;
    }

    @Override
    protected BasketAsyncResult doInBackground(Void... voids) {
        long start = System.currentTimeMillis();
        while (!BasketController.getInstance().isLocalListReady()) {
            if (System.currentTimeMillis()-start > 5000) {
                return BasketAsyncResult.TIMEOUT;
            }
        };
        return BasketAsyncResult.UPDATED;
    }

    @Override
    protected void onPostExecute(BasketAsyncResult r) {
        cb.onCallback(r);
    }

    public static interface BasketAsyncCallback {
        public void onCallback(BasketAsyncResult result);
    }

    public static enum BasketAsyncResult {
        UPDATED,TIMEOUT
    }
}