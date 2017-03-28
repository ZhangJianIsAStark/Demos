package stark.a.is.zhang.tcptest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class PermissionDialogFragment extends DialogFragment{
    private static final String ACTION_NAME
            = "android.settings.APPLICATION_DETAILS_SETTINGS";

    static PermissionDialogFragment newInstance() {
        return new PermissionDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_permission_dialog, container, false);

        configChildViews(view);

        return view;
    }

    private void configChildViews(View view) {
        TextView goSettingsTextView = (TextView) view.findViewById(R.id.go_settings);
        goSettingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent()
                        .setAction(ACTION_NAME)
                        .setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                startActivity(intent);
            }
        });

        TextView cancelTextView = (TextView) view.findViewById(R.id.cancel);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();

        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();

            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;

            window.setAttributes(params);
        }
    }
}