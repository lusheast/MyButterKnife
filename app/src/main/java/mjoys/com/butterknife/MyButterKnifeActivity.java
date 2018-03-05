package mjoys.com.butterknife;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.butterknife.ButterKnife;
import com.butterknife.Unbinder;
import com.butterknife.annotations.BindView;


/**
 * Created by zsd on 2018/2/23 18:25
 * desc:
 */

public class MyButterKnifeActivity extends AppCompatActivity {


    @BindView(R.id.text222)
    TextView text22;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_butterknife);
        mUnbinder = ButterKnife.bind(this);
        text22.setText("我的butterknife");
    }


    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }
}
