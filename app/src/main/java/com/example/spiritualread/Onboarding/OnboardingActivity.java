package com.example.spiritualread.Onboarding;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.spiritualread.Auth.loginOnboard;
import com.example.spiritualread.R;


public class    OnboardingActivity extends AppCompatActivity {
    int SPLASH_TIME = 2000;
    private ViewPager viewPager;
    private OnboardingAdapter onboardingAdapter;
Button  Button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        makeStatusbarTransparent();

        viewPager = findViewById(R.id.onboarding_view_pager);

        onboardingAdapter = new OnboardingAdapter(this);
        viewPager.setAdapter(onboardingAdapter);
        viewPager.setPageTransformer(false, new OnboardingPageTransformer());


    }


    // Listener for next button press
    public void nextPage(View view) {
        if (view.getId() == R.id.button2) {
            if (viewPager.getCurrentItem() < onboardingAdapter.getCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
            else {
                new Handler(getMainLooper()).postDelayed(() -> {
                    //Do any action here. Now we are moving to next page
                    Intent mySuperIntent = new Intent(OnboardingActivity.this, loginOnboard.class);
                    startActivity(mySuperIntent);

                    //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
                    finish();

                }, SPLASH_TIME);
            }
            }

        }
    private void makeStatusbarTransparent() {

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
