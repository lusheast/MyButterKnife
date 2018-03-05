//package mjoys.com.butterknife;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.Button;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.Unbinder;
//
//public class MainActivity extends AppCompatActivity {
//
//    @BindView(R.id.goto_login)
//    Button gotoLogin;
//    private Unbinder mUnbinder;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mUnbinder = ButterKnife.bind(this);
//
//        gotoLogin.setText("ButterKnife");
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        mUnbinder.unbind();
//        super.onDestroy();
//    }
//}
