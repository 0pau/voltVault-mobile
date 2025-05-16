package hu.opau.voltvault.controller;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import hu.opau.voltvault.models.UserBasketItem;

public class BasketController {

    private static BasketController basketController;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private List<UserBasketItem> localList = new ArrayList<>();
    private boolean localListReady = false;
    private boolean viewRefreshNeeded = true;

    private BasketController() {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static BasketController getInstance() {
        if (basketController == null) {
            basketController = new BasketController();
        }
        return basketController;
    }

    public List<UserBasketItem> getBasketItems() {
        return localList;
    }

    public void refreshBasketFromDatabase() {
        localListReady = false;
        if (firebaseAuth.getUid() == null) {
            return;
        }
        firestore.collection("userBaskets").document(firebaseAuth.getUid()).get().addOnSuccessListener(e->{

            if (!e.exists() || e.get("items") == null) {
                commitChanges();
                viewRefreshNeeded = true;
                return;
            }

            localList = new ArrayList<>();
            for (HashMap<String, Object> map: (ArrayList<HashMap<String, Object>>)e.get("items")) {
                localList.add(
                        new UserBasketItem(
                                (String) map.get("id"),
                                ((Long) map.get("price")).intValue(),
                                ((Long) map.get("quantity")).intValue()
                ));
            }
            localListReady = true;
            viewRefreshNeeded = true;
        });
    }

    public boolean isLocalListReady() {
        return localListReady;
    }

    public boolean isViewRefreshNeeded() {
        return viewRefreshNeeded;
    }

    public void setViewRefreshNeeded(boolean viewRefreshNeeded) {
        this.viewRefreshNeeded = viewRefreshNeeded;
    }

    public void commitChanges() {
        if (firebaseAuth.getUid() == null) {
            return;
        }
        firestore.collection("userBaskets").document(firebaseAuth.getUid()).update("items", localList);
    }

    public void addItem(UserBasketItem item) {
        for (int i = 0; i < localList.size(); i++) {
            if (Objects.equals(localList.get(i).getId(), item.getId())) {
                localList.get(i).setQuantity(localList.get(i).getQuantity()+item.getQuantity());
                viewRefreshNeeded = true;
                return;
            }
        }

        localList.add(item);
        viewRefreshNeeded = true;
        commitChanges();
    }

    public void deleteItem(String id) {

        for (int i = 0; i < localList.size(); i++) {
            if (localList.get(i).getId().equals(id)) {
                localList.remove(i);
                break;
            }
        }

        commitChanges();
    }

    public int getWholePrice() {
        int r = 0;

        for (var i: getBasketItems()) {
            r += i.getPrice()*i.getQuantity();
        }

        return r;
    }

    public void clearAndCommit() {
        localList = new ArrayList<>();
        commitChanges();
        viewRefreshNeeded = true;
    }
}
