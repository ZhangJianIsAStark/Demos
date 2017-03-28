package stark.a.is.zhang.tcptest;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import stark.a.is.zhang.tcptest.client.ClientActivity;
import stark.a.is.zhang.tcptest.server.ServerActivity;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {
    private Button mClientButton;
    private Button mServerButton;

    private static int PERMISSION_REQUEST_CODE = 0;
    private static String[] NEEDED_PERMISSION = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int DIALOG_FRAGMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findChildViews();
        configChildViews();

        requestPermissions();
    }

    private void findChildViews() {
        mClientButton = (Button) findViewById(R.id.client);
        mServerButton = (Button) findViewById(R.id.server);
    }

    private void configChildViews() {
        mClientButton.setOnClickListener(this);
        mServerButton.setOnClickListener(this);

        toggleButtonState(false);
    }

    private void toggleButtonState(boolean enable) {
        mClientButton.setEnabled(enable);
        mServerButton.setEnabled(enable);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                NEEDED_PERMISSION, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!checkPermissions()) {
            if (shouldRequestPermission()) {
                requestPermissions();
            } else {
                showDialogFragment();
            }
        } else {
            toggleButtonState(true);
            hideDialogFragment();
        }
    }

    private boolean checkPermissions() {
        for (String str : NEEDED_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, str)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private boolean shouldRequestPermission() {
        boolean rst = true;

        for (String str : NEEDED_PERMISSION) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, str)) {
                rst = false;
                break;
            }
        }

        return rst;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean hasAllPermission = true;

            for (int rst : grantResults) {
                if (rst != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false;
                    break;
                }
            }

            if (hasAllPermission) {
                toggleButtonState(true);
            }
        }
    }

    private void showDialogFragment() {
        DialogFragment fragment = PermissionDialogFragment.newInstance();
        fragment.setCancelable(false);
        getSupportFragmentManager()
                .beginTransaction()
                .add(DIALOG_FRAGMENT, fragment)
                .commit();
    }

    private void hideDialogFragment() {
        DialogFragment dialogFragment = (DialogFragment)
                getSupportFragmentManager().findFragmentById(DIALOG_FRAGMENT);
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.client:
                startAnimator(mClientButton);
                break;

            case R.id.server:
                startAnimator(mServerButton);
                break;
        }
    }

    private void startAnimator(View view) {
        PropertyValuesHolder pvhX =
                PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.8f, 1.0f);

        PropertyValuesHolder pvhY =
                PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.8f, 1.0f);

        final ObjectAnimator animator = ObjectAnimator
                .ofPropertyValuesHolder(view, pvhX, pvhY)
                .setDuration(100);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Object object = animator.getTarget();
                if (object != null) {
                    if (object.equals(mClientButton)) {
                        startActivity(new Intent(
                                getApplicationContext(), ClientActivity.class));
                    } else {
                        startActivity(new Intent(
                                getApplicationContext(), ServerActivity.class));
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.start();
    }
}