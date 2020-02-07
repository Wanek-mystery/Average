package wanek.average;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.TooltipCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.Spanned;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;

import static android.content.Context.MODE_PRIVATE;

public class FragmentRuCalculator extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    private MaterialButton button_5;
    private MaterialButton button_4;
    private MaterialButton button_3;
    private MaterialButton button_2;
    private Button btnDel;
    private Button btnDown;
    private ImageView commentBtn;
    private ImageView btnSettings;
    private TextView tvScore;
    private TextView tvCountFiveForFive;
    private TextView tvCountFiveForFour;
    private TextView tvCountFourForFour;
    private TextView tvForFive;
    private TextView tvForFour;
    private TextView tvOr;
    private ImageView viewLeft;
    private TextView viewRight;
    private TextView tvAds21;
    private ConstraintLayout.LayoutParams notesLeftParams;
    private ConstraintLayout.LayoutParams notesRightParams;

    HandleNotes handleNotes;
    SharedPreferences sharedPreferences;
    AppUpdateManager appUpdateManager;

    int width;
    int height;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        appUpdateManager = AppUpdateManagerFactory.create(getActivity());
        double roundFive;
        double roundFour;
        try {
            roundFive = Double.valueOf(sharedPreferences.getString("average_round_five","4.5"));
        } catch (NumberFormatException ex) {
            roundFive = 4.5;
        }try {
            roundFour = Double.valueOf(sharedPreferences.getString("average_round_four","3.5"));
        } catch (NumberFormatException ex) {
            roundFour = 3.5;
        }
        handleNotes = new HandleNotes(roundFive,roundFour);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        sharedPreferences = getActivity().getSharedPreferences("launch", MODE_PRIVATE);
        setRetainInstance(true);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {

        View view = inflater.inflate(R.layout.culculator5_fragment,container,false);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        tvScore = view.findViewById(R.id.tvScore);
        tvCountFiveForFive = view.findViewById(R.id.tvFiveForFive);
        tvCountFiveForFour = view.findViewById(R.id.tvFiveForFour);
        tvCountFourForFour= view.findViewById(R.id.tvFourForFour);
        tvOr = view.findViewById(R.id.tvOr);
        button_5 = view.findViewById(R.id.button_5);
        button_4 = view.findViewById(R.id.button_4);
        button_3 = view.findViewById(R.id.button_3);
        button_2 = view.findViewById(R.id.button_2);
        btnDel = view.findViewById(R.id.btnDel);
        btnDown = view.findViewById(R.id.btnDown);
        tvForFive = view.findViewById(R.id.tvForFive);
        tvForFour = view.findViewById(R.id.tvForFour);
        viewLeft = view.findViewById(R.id.view1);
        viewRight = view.findViewById(R.id.view2);
        commentBtn = view.findViewById(R.id.commentBtn);
        tvAds21 = view.findViewById(R.id.btn21);
        tvAds21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("adsIsPressed",true);
                editor.apply();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "martian.mystery")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "martian.mystery")));
                }
            }
        });
        btnSettings = view.findViewById(R.id.btnSettings);
        TooltipCompat.setTooltipText(tvForFive,"Показывает сколько нужно получить пятерок, чтобы вышла 5ка");

        button_5.setOnTouchListener(btnOnTouchListener);
        button_4.setOnTouchListener(btnOnTouchListener);
        button_3.setOnTouchListener(btnOnTouchListener);
        button_2.setOnTouchListener(btnOnTouchListener);
        btnDel.setOnTouchListener(btnOnTouchListener);
        btnDown.setOnTouchListener(btnOnTouchListener);
        viewLeft.setOnTouchListener(noteOnTouchListener);
        viewRight.setOnTouchListener(noteOnTouchListener);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment commentDialogFragment = new MessageDialog(MessageDialog.REVIEW_DIALOG,"wanek.average");
                commentDialogFragment.show(getFragmentManager(), MainActivity.COMMENT_DIALOG_TAG);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSettings = new Intent(getContext(),SettingsActivity.class);
                startActivityForResult(intentSettings,1);
            }
        });
        notesLeftParams = (ConstraintLayout.LayoutParams) viewLeft.getLayoutParams();
        notesRightParams = (ConstraintLayout.LayoutParams) viewRight.getLayoutParams();

        width = width - 32;
        notesLeftParams.width = width / 9;
        notesLeftParams.height = height / 10;
        notesRightParams.width = width - notesLeftParams.width - 9;
        notesRightParams.height = height / 12;

        viewLeft.animate().translationXBy(0).translationX(notesRightParams.width + 9).start();
        viewRight.animate().translationXBy(0).translationX(notesRightParams.width + 9).start();

        if(sharedPreferences.getBoolean("isComment",false)) { // если пользователь уже оставил отзыв, то скрываем кнопку
            commentBtn.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        appealingToAds();
    }

    private void appealingToAds() {
        sharedPreferences = getActivity().getSharedPreferences("launch",MODE_PRIVATE);
        boolean adsIsShowed = sharedPreferences.getBoolean("adsIsShowed",false);
        boolean adsIsPressed = sharedPreferences.getBoolean("adsIsPressed",false);
        if(!adsIsShowed) {
            MessageDialog messageDialog = new MessageDialog(MessageDialog.ADS_DIALOG);
            messageDialog.show(getFragmentManager(),"ADS");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("adsIsShowed",true);
            editor.apply();
        }
        if(!adsIsPressed) {
            tvAds21.setVisibility(View.VISIBLE);
        }
    }

    private Spanned textToSpannedWithUnderline(String text) {
        return android.text.Html.fromHtml("<u>" + text + "</u>");
    }
    View.OnTouchListener btnOnTouchListener = new View.OnTouchListener() { // обработчик касания для кнопок-оценок
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleXBy(1).scaleX(0.9f).scaleYBy(1).scaleY(0.9f).setDuration(30).start();
                    v.animate().alphaBy(1.0f).alpha(0.9f).setDuration(80).start();
                    if (!(v.getId() == R.id.btnDel || v.getId() == R.id.btnDown)) {
                        v.setBackgroundColor(getResources().getColor(R.color.bottomBackColorPressed));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    v.animate().scaleXBy(0.9f).scaleX(1).scaleYBy(0.9f).scaleY(1).setDuration(80).start();
                    v.animate().alphaBy(0.9f).alpha(1.0f).setDuration(80).start();
                    if (!(v.getId() == R.id.btnDel || v.getId() == R.id.btnDown)) {
                        v.setBackgroundColor(getResources().getColor(R.color.bottomBackColor));
                    }
                    if (v.getId() == R.id.button_5) {
                        tvScore.setText(String.valueOf(handleNotes.clickNote(5)));
                    } else if (v.getId() == R.id.button_4) {
                        tvScore.setText(String.valueOf(handleNotes.clickNote(4)));
                    } else if (v.getId() == R.id.button_3) {
                        tvScore.setText(String.valueOf(handleNotes.clickNote(3)));
                    } else if (v.getId() == R.id.button_2) {
                        tvScore.setText(String.valueOf(handleNotes.clickNote(2)));
                    } else if (v.getId() == R.id.btnDown) {
                        if (handleNotes.getAscoreNotes() > 0) {
                            tvScore.setText(String.valueOf(handleNotes.clickDeleteOne()));
                        }
                    } else if (v.getId() == R.id.btnDel) {
                        tvScore.setText(String.valueOf(handleNotes.clickDeleteAll()));
                    }
                    viewRight.setText(handleNotes.getNotesString());
                    visibilityLayout(handleNotes.getAscoreNotes());
                    break;
            }
            return true;
        }
    };
    private void visibilityLayout(double score) { // анимация
        Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.animfortop);
        if (score == 0) {
            if(tvForFive.getVisibility() == View.VISIBLE) {
                tvForFive.setVisibility(View.INVISIBLE);
                tvCountFiveForFive.setVisibility(View.INVISIBLE);
                tvForFive.startAnimation(animation);
                tvCountFiveForFive.startAnimation(animation);
            }

            animation = AnimationUtils.loadAnimation(getActivity(),R.anim.animforbottom);

            if(tvOr.getVisibility() == View.VISIBLE) {
                tvCountFourForFour.setVisibility(View.INVISIBLE);
                tvOr.setVisibility(View.INVISIBLE);
                tvCountFiveForFour.setVisibility(View.INVISIBLE);
                tvForFour.setVisibility(View.INVISIBLE);
                tvCountFourForFour.startAnimation(animation);
                tvCountFiveForFour.startAnimation(animation);
                tvOr.startAnimation(animation);
                tvForFour.startAnimation(animation);
            }
        } else if(score >= handleNotes.getRoundFive()) {
            tvForFive.setVisibility(View.INVISIBLE);
            tvCountFiveForFive.setVisibility(View.INVISIBLE);
            tvCountFourForFour.setVisibility(View.INVISIBLE);
            tvOr.setVisibility(View.INVISIBLE);
            tvCountFiveForFour.setVisibility(View.INVISIBLE);
            tvForFour.setVisibility(View.INVISIBLE);
        } else if(score < handleNotes.getRoundFive() && score >= handleNotes.getRoundFour()) {
            tvForFour.setVisibility(View.INVISIBLE);
            tvForFour.setVisibility(View.INVISIBLE);
            tvCountFourForFour.setVisibility(View.INVISIBLE);
            tvOr.setVisibility(View.INVISIBLE);
            tvCountFiveForFour.setVisibility(View.INVISIBLE);
            tvForFive.setVisibility(View.VISIBLE);
            tvCountFiveForFive.setVisibility(View.VISIBLE);
            tvCountFiveForFive.setText(textToSpannedWithUnderline(handleNotes.getFiveWithFive()));
        } else if (score < handleNotes.getRoundFour()) {
            tvForFive.setVisibility(View.VISIBLE);
            tvCountFiveForFive.setVisibility(View.VISIBLE);
            tvCountFourForFour.setVisibility(View.VISIBLE);
            tvOr.setVisibility(View.VISIBLE);
            tvCountFiveForFour.setVisibility(View.VISIBLE);
            tvCountFiveForFive.setText(textToSpannedWithUnderline(handleNotes.getFiveWithFive()));
            tvCountFiveForFour.setText(textToSpannedWithUnderline(handleNotes.getFourWithFive()));
            tvCountFourForFour.setText(textToSpannedWithUnderline(handleNotes.getFourWithFour()));
            tvForFour.setVisibility(View.VISIBLE);
        }
    }
    View.OnTouchListener noteOnTouchListener = new View.OnTouchListener() { // обработчик касания для вывода оценок
        boolean flag = false;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_UP:
                    try{
                        if(!flag) {
                            ImageView vNext = (ImageView) v;
                            viewRight.setText(handleNotes.getNotesString());
                            v.animate().translationXBy(0).translationX(-5).start();
                            viewRight.animate().translationXBy(0).translationX(-5).start();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                vNext.setImageDrawable(getResources().getDrawable(R.drawable.right));
                            }
                            flag = true;
                        } else {
                            if(v.getId() == R.id.view2) {
                                v.animate().translationXBy(0).translationX(notesRightParams.width + 9).start();
                                viewLeft.animate().translationXBy(0).translationX(notesRightParams.width + 9).start();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    viewLeft.setImageDrawable(getResources().getDrawable(R.drawable.left));
                                }
                                flag = false;
                                break;
                            }
                            v.animate().translationXBy(0).translationX(notesRightParams.width + 9).start();
                            viewRight.animate().translationXBy(0).translationX(notesRightParams.width + 9).start();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                viewLeft.setImageDrawable(getResources().getDrawable(R.drawable.left));
                            }
                            flag = false;
                        }
                    } catch (ClassCastException ex) {
                    }
                    break;
            }
            return true;
        }
    };
}
