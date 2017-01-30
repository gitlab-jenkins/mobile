package xyz.homapay.hampay.mobile.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.friendsinvitation.AdapterFriendsInvitation;
import xyz.homapay.hampay.mobile.android.common.friendsinvitation.FriendsObject;
import xyz.homapay.hampay.mobile.android.component.MyTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;

/**
 * Created by mohammad on 1/30/17.
 */

public class ActivityFriendsInvitation extends ActivityParent implements View.OnClickListener {

    @BindView(R.id.etSearchPhraseText)
    FacedEditText etSearchPhraseText;

    @BindView(R.id.lst)
    RecyclerView lst;

    AdapterFriendsInvitation adapterFriendsInvitation;
    HamPayDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_invitation);
        ButterKnife.bind(this);
        dialog = new HamPayDialog(this);
        dialog.showWaitingDialog("");
        LinearLayoutManager manager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        lst.setLayoutManager(manager);
        load();
        etSearchPhraseText.addTextChangedListener(new MyTextWatcher(text -> {
            if (text.length() == 0) {
                load();
            }
        }));
    }

    private void load() {
        Observable.create((ObservableOnSubscribe<List<FriendsObject>>) observableEmitter -> observableEmitter.onNext(regularLoad()))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> dialog.showWaitingDialog(""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(friendsObjects -> {
                    dialog.dismisWaitingDialog();
                    makeAdapter(friendsObjects);
                }, throwable -> onError());
    }

    private void onError() {
        dialog.dismisWaitingDialog();
        Toast.makeText(ctx, R.string.err_general_text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
            case R.id.imgSearchImage:
                String searchText = etSearchPhraseText.getText().toString().trim();
                if (searchText.equals(""))
                    return;
                Observable.create((ObservableOnSubscribe<List<FriendsObject>>) observableEmitter -> observableEmitter.onNext(searchLoad(searchText)))
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(disposable -> dialog.showWaitingDialog(""))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(friendsObjects -> {
                            dialog.dismisWaitingDialog();
                            makeAdapter(friendsObjects);
                        }, throwable -> onError());
                break;
            case R.id.tvSend:
                // TODO call network
                break;
        }
    }

    private List<FriendsObject> regularLoad() {
        List<FriendsObject> items = new ArrayList<>();
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        List<Contact> contacts = q.find();
        initList(items, contacts);
        return items;
    }

    private List<FriendsObject> searchLoad(String searchTerm) {
        List<FriendsObject> items = new ArrayList<>();
        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        q.whereContains(Contact.Field.DisplayName, searchTerm);
        List<Contact> contacts = q.find();
        initList(items, contacts);
        return items;
    }

    private void initList(List<FriendsObject> items, List<Contact> contacts) {
        for (Contact item : contacts) {
            if (item.getPhoneNumbers() != null && item.getPhoneNumbers().size() > 0) {
                String rawPhone = item.getPhoneNumbers().get(0).getNumber();
                String fixedPhone = TelephonyUtils.fixPhoneNumber(ctx, rawPhone);
                if (TelephonyUtils.isIranValidNumber(fixedPhone)) {
                    items.add(new FriendsObject(item, fixedPhone, false));
                }
            }
        }
    }

    private void makeAdapter(List<FriendsObject> items) {
        if (adapterFriendsInvitation != null) {
            adapterFriendsInvitation.clear();
            adapterFriendsInvitation.addAll(items);
        } else {
            adapterFriendsInvitation = new AdapterFriendsInvitation(ctx, items);
        }
        lst.setAdapter(adapterFriendsInvitation);
    }

}
