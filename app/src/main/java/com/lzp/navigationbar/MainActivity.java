package com.lzp.navigationbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    NavigationBar bar;
    boolean succ = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bar = (NavigationBar) findViewById(R.id.nv);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar.isEnd()) {
                    bar.reset();
                }
                bar.next(new NavigationBar.Step("点头", succ));
                succ = !succ;
            }
        });
    }
}
