package stark.a.is.zhang.tcptest.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import stark.a.is.zhang.tcptest.R;
import stark.a.is.zhang.tcptest.model.ViewModel;

public class DownloadActivity extends AppCompatActivity {
    private String mServerIp;

    private RecyclerView mRecyclerView;
    private List<ViewModel> mViewModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        mServerIp = getIntent().getStringExtra(Constants.SERVER_IP);

        mViewModelList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.download_recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new ViewAdapter());
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private View mItemView;
        private ImageView mImageView;
        private TextView mTextView;
        private CheckBox mCheckBox;

        ItemHolder(View itemView) {
            super(itemView);

            mItemView = itemView;

            mImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
            mTextView = (TextView) itemView.findViewById(R.id.item_text_view);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
        }

        void bind(final ViewModel viewModel) {
            mImageView.setImageBitmap(viewModel.getBitmap());
            mTextView.setText(viewModel.getTitle());
            mCheckBox.setChecked(viewModel.isChecked());

            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean origin = mCheckBox.isChecked();
                    mCheckBox.setChecked(!origin);
                    viewModel.setChecked(!origin);
                }
            });
        }
    }

    private class ViewAdapter extends RecyclerView.Adapter<ItemHolder> {
        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.activity_download_item_view, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.bind(mViewModelList.get(position));
        }

        @Override
        public int getItemCount() {
            return mViewModelList.size();
        }
    }
}