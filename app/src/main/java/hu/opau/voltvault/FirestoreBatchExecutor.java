package hu.opau.voltvault;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A class that executes Firestore queries in sequential order.
 * TODOs:
 * - Replace Object with the corresponding Firestore class
*/
 

public class FirestoreBatchExecutor {
  private Map<Query, FirestoreBatchItemCallback> batch = new LinkedHashMap<>();
  private int completedTasks = 0;
  private int currentTask =-1;
  private OnFirestoreBatchCompleteListener completionListener;
  private ArrayList<Map.Entry<Query, FirestoreBatchItemCallback>> queue;
  public FirestoreBatchExecutor add(Query task, FirestoreBatchItemCallback cb) {
    batch.put(task, cb);
    return this;
  }
  public void runBatch(RunOptions options)  {
    queue = new ArrayList<>();
    queue.addAll(batch.entrySet());
    next();
  }
  private void next() {
    currentTask++;
    if (currentTask==queue.size()) return;
    queue.get(currentTask).getKey().get().addOnCompleteListener(e->{
      if (e.isSuccessful()) {
        completedTasks++;
        queue.get(currentTask).getValue().cb(e);
        if (completedTasks == queue.size() && completionListener != null) {
          completionListener.onEvent();
        }
        next();
      }

    });
  }
  public static enum RunOptions {ABORT_ON_ERROR}
  public void setOnFirestoreBatchCompleteListener(OnFirestoreBatchCompleteListener newListener) {
    this.completionListener = newListener;
  }
  
  public static interface FirestoreBatchItemCallback {
    public void cb(Task<QuerySnapshot> e);
  }
  public static interface OnFirestoreBatchCompleteListener {
    public void onEvent();
  }
}