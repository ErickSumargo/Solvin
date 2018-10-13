package id.solvin.dev.realm.helper;

import java.util.ArrayList;
import java.util.List;

import id.solvin.dev.model.basic.DraftSolution;
import id.solvin.dev.realm.table.TableDraftSolution;
import id.solvin.dev.realm.table.TableImage;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Erick Sumargo on 4/6/2017.
 */

public class RealmDraftSolution {
    //    HELPER
    private Realm realm;

    //    OBJECT
    private RealmResults<TableDraftSolution> records;
    private RealmResults<TableImage> tableImageRecords;
    private List<DraftSolution> draftSolutionList;

    public RealmDraftSolution(Realm realm) {
        this.realm = realm;
    }

    public int getId() {
        if (realm.where(TableDraftSolution.class).max("id") == null) {
            return 1;
        } else {
            return realm.where(TableDraftSolution.class).max("id").intValue() + 1;
        }
    }

    public void saveNew(final DraftSolution draftSolution) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                final TableDraftSolution record = new TableDraftSolution();
                record.setId(draftSolution.getId());
                record.setTitle(draftSolution.getTitle());
                record.setContent(draftSolution.getContent());
                record.setImages(draftSolution.getImages());
                record.setUpdated_at(draftSolution.getUpdated_at());

                bgRealm.copyToRealm(record);
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    public void save(final DraftSolution draftSolution) {
        final RealmResults<TableImage> recordImages = realm.where(TableImage.class).equalTo("draft_id", draftSolution.getId()).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                recordImages.clear();
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final TableDraftSolution record = realm.where(TableDraftSolution.class).equalTo("id", draftSolution.getId()).findFirst();
                record.setTitle(draftSolution.getTitle());
                record.setContent(draftSolution.getContent());

                List<TableImage> realmFriends = realm.copyToRealmOrUpdate(draftSolution.getImages());
                RealmList<TableImage> realmListFriends = new RealmList<>(realmFriends.toArray(new TableImage[draftSolution.getImages().size()]));
                record.setImages(realmListFriends);
                record.setUpdated_at(draftSolution.getUpdated_at());
            }
        });
    }

    public List<DraftSolution> retrieve() {
        records = realm.where(TableDraftSolution.class).findAll();
        draftSolutionList = new ArrayList<>();

        for (TableDraftSolution record : records) {
            DraftSolution draftSolution = new DraftSolution();
            draftSolution.setId(record.getId());
            draftSolution.setTitle(record.getTitle());
            draftSolution.setContent(record.getContent());
            draftSolution.setImages(record.getImages());
            draftSolution.setUpdated_at(record.getUpdated_at());

            draftSolutionList.add(draftSolution);
        }
        return draftSolutionList;
    }

    public String getEncodedImage(int id, int index) {
        return realm.where(TableDraftSolution.class).equalTo("id", id).findFirst().getImages().get(index).getImage();
    }

    public DraftSolution find(int id) {
        records = realm.where(TableDraftSolution.class).findAll();
        final TableDraftSolution record = records.where().equalTo("id", id).findFirst();

        final DraftSolution draftSolution = new DraftSolution();
        draftSolution.setId(record.getId());
        draftSolution.setTitle(record.getTitle());
        draftSolution.setContent(record.getContent());
        draftSolution.setImages(record.getImages());
        draftSolution.setUpdated_at(record.getUpdated_at());

        return draftSolution;
    }

    public void rename(final int id, final String title) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final TableDraftSolution record = realm.where(TableDraftSolution.class).equalTo("id", id).findFirst();
                record.setTitle(title);
            }
        });
    }

    public void remove(int id) {
        records = realm.where(TableDraftSolution.class).findAll();
        final TableDraftSolution record = realm.where(TableDraftSolution.class).equalTo("id", id).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                record.removeFromRealm();
            }
        });

        final RealmResults<TableImage> recordImages = realm.where(TableImage.class).equalTo("draft_id", id).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                recordImages.clear();
            }
        });
    }
}