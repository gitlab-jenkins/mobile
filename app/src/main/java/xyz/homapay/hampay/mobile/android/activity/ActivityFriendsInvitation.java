package xyz.homapay.hampay.mobile.android.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.response.FriendsInvitationResponse;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.friendsinvitation.AdapterFriendsInvitation;
import xyz.homapay.hampay.mobile.android.common.friendsinvitation.FriendsObject;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.MyTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionContactDialog;
import xyz.homapay.hampay.mobile.android.p.invitation.FriendsInvitation;
import xyz.homapay.hampay.mobile.android.p.invitation.FriendsInvitationImpl;
import xyz.homapay.hampay.mobile.android.p.invitation.FriendsInvitationView;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;

/**
 * Created by mohammad on 1/30/17.
 */

public class ActivityFriendsInvitation extends ActivityParent implements View.OnClickListener, FriendsInvitationView, PermissionContactDialog.PermissionContactDialogListener {

    private static final int RC_CONTACTS = 5000;
    @BindView(R.id.etSearchPhraseText)
    FacedEditText etSearchPhraseText;
    @BindView(R.id.lst)
    RecyclerView lst;
    @BindView(R.id.tvSend)
    FacedTextView tvSend;
    AdapterFriendsInvitation adapterFriendsInvitation;
    HamPayDialog dialog;
    private FriendsInvitation friendsInvitation;
    private boolean showRationale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_invitation);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            innerInit();
        } else {
            String[] perms = {Manifest.permission.READ_CONTACTS};
            ActivityCompat.requestPermissions(this, perms, RC_CONTACTS);
        }
    }

    private void innerInit() {
        tvSend.setOnClickListener(this);
        friendsInvitation = new FriendsInvitationImpl(new ModelLayerImpl(ctx), this);
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
        etSearchPhraseText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (etSearchPhraseText.getText().toString().trim().length() == 0) {
                    load();
                }
                if (etSearchPhraseText.getText().toString().trim().length() != 0) {
                    String searchText = etSearchPhraseText.getText().toString().trim();
                    if (!searchText.equals("")) {
                        Observable.create((ObservableOnSubscribe<List<FriendsObject>>) observableEmitter -> observableEmitter.onNext(searchLoad(searchText)))
                                .subscribeOn(Schedulers.io())
                                .doOnSubscribe(disposable -> dialog.showWaitingDialog(""))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(friendsObjects -> {
                                    dialog.dismisWaitingDialog();
                                    makeAdapter(friendsObjects);
                                }, throwable -> onError());
                    }
                }
                return true;
            }
            return false;
        });
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

    @Override
    public void showProgress() {
        dialog.showWaitingDialog("");
    }

    @Override
    public void cancelProgress() {
        dialog.dismisWaitingDialog();
    }

    @Override
    public void onError() {
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
                if (adapterFriendsInvitation != null && adapterFriendsInvitation.getItemCount() > 0) {
                    List<String> items = AdapterFriendsInvitation.getSelected();
                    if (items.size() == 0) {
                        Toast.makeText(ctx, R.string.err_no_contact_selected, Toast.LENGTH_LONG).show();
                        return;
                    }
                    friendsInvitation.invite(items);
                }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        AdapterFriendsInvitation.invalidateSelected();
        super.onDestroy();
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

    @Override
    public void onSendInvitation(boolean state, ResponseMessage<FriendsInvitationResponse> data, String message) {
        if (state) {
            if (data.getService() != null && data.getService().getResultStatus() == ResultStatus.SUCCESS) {
                dialog.showSuccessFriendsInvitation(() -> onBackPressed());
            }
        } else {
            onError();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_CONTACTS) {
            showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS);
            if (!showRationale) {
                // Do nothing
            } else {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(new PermissionContactDialog(), null);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        }
    }

    @Override
    public void onFinishEditDialog(ActionPermission actionPermission) {
        if (showRationale) {
            if (actionPermission == ActionPermission.GRANT) {
                init();
            } else {
                finish();
            }
        }
    }
}
