package hu.opau.voltvault.controller;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import hu.opau.voltvault.logic.Condition;
import hu.opau.voltvault.models.Product;

public class ProductController {

    private static ProductController productController;
    private FirebaseFirestore firestore;

    private ExecutorService executor
            = Executors.newSingleThreadExecutor();

    private ProductController() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static ProductController getInstance() {
        if (productController == null) {
            productController = new ProductController();
        }
        return productController;
    }

    public Query query() {
        return query(25, 1, null);
    }

    public Query query(Integer limit) {
        return query(limit, 1, null);
    }

    public Query query(Integer limit, Integer page) {
        return query(limit, page, null);
    }

    public Query query(Integer limit, Integer page, ArrayList<Condition> query) {

        var qs = firestore.collection("products");
        Query q = qs.limit(25);

        if (limit != null) {
            q = q.limit(limit);
        }

        if (query != null && query.size() > 0) {
            for (int i = 0; i < query.size(); i++) {
                Condition c = query.get(i);
                switch (c.operator) {
                    case EQ:
                        q = q.whereEqualTo(c.leftOperand, c.rightOperand);
                        break;
                    case NE:
                        q = q.whereNotEqualTo(c.leftOperand, c.rightOperand);
                        break;
                    case LT:
                        q = q.whereLessThan(c.leftOperand, c.rightOperand);
                        break;
                    case GT:
                        q = q.whereGreaterThan(c.leftOperand, c.rightOperand);
                        break;
                    case LE:
                        q = q.whereLessThanOrEqualTo(c.leftOperand, c.rightOperand);
                        break;
                    case GE:
                        q = q.whereGreaterThanOrEqualTo(c.leftOperand, c.rightOperand);
                        break;
                    case AC:
                        q = q.whereArrayContains(c.leftOperand, c.rightOperand);
                        break;
                }
            }
        }
        return q;

    }
}
