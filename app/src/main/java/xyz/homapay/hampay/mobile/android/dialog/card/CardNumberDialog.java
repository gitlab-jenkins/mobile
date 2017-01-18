package xyz.homapay.hampay.mobile.android.dialog.card;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import xyz.homapay.hampay.common.core.model.response.dto.CardDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.CardAdapter;
import xyz.homapay.hampay.mobile.android.util.Constants;


/**
 * Created by amir on 9/13/16.
 */
public class CardNumberDialog extends DialogFragment implements TextView.OnEditorActionListener, View.OnClickListener {

    private Bundle bundle;
    private List<CardDTO> cardList;
    private ListView cardListView;
    private LinearLayout addNewCard;
    private CardAdapter cardAdapter;

    public interface SelectCardDialogListener {
        void onFinishEditDialog(CardAction cardAction, int position);
    }

    SelectCardDialogListener activity;
    private Rect rect = new Rect();


    @Override
    public void onClick(View view) {
        SelectCardDialogListener activity = (SelectCardDialogListener) getActivity();
        activity.onFinishEditDialog(CardAction.NOPE, -1);
        this.dismiss();
    }



    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        SelectCardDialogListener activity = (SelectCardDialogListener) getActivity();
        activity.onFinishEditDialog(CardAction.NOPE, -1);
        this.dismiss();
        return true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bundle = getArguments();
        cardList = (List<CardDTO>) bundle.getSerializable(Constants.CARD_LIST);

        View view = inflater.inflate(R.layout.dialog_card_selection, container);

        cardAdapter = new CardAdapter(getContext(), cardList);
        cardListView = (ListView)view.findViewById(R.id.cardListView);
        cardListView.setAdapter(cardAdapter);
        addNewCard = (LinearLayout) view.findViewById(R.id.addNewCard);
        addNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onFinishEditDialog(CardAction.ADD, -1);
                dismiss();
            }
        });

        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                activity.onFinishEditDialog(CardAction.SELECT, position);
                dismiss();
            }
        });

        activity = (SelectCardDialogListener) getActivity();

        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        view.setMinimumWidth((int) (rect.width() * 0.85f));

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return view;
    }
}
