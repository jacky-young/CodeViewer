package com.jack.codeviewer.ui;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jack.codeviewer.AppConfig;
import com.jack.codeviewer.DownloadService;
import com.jack.codeviewer.DownloadService.DownloadBinder;
import com.jack.codeviewer.utils.DownloadState;
import com.jack.codeviewer.utils.FileUtils;
import com.jack.codeviewer.GitListAdapter;
import com.jack.codeviewer.interf.ICallbackResult;
import com.jack.codeviewer.R;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements ICallbackResult{
    private Context context;

    private EditText editUrl;
    private Button fetch;
    private TextView downloadInfo;
    private RelativeLayout progress_layout;
    private ProgressBar progressBar;
    private TextView progressCount;
    private ListView list;

    private ServiceConnection conn;
    private Intent downloadIntent;

    private List<DownloadState> gitList;
    private GitListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editUrl = (EditText) findViewById(R.id.editurl);
        fetch = (Button) findViewById(R.id.fetch);
        downloadInfo = (TextView) findViewById(R.id.download_info);
        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressCount = (TextView) findViewById(R.id.progress_count);
        list = (ListView) findViewById(R.id.list);
        context = getApplicationContext();
        initAdapter();
        initActions();
    }

    private void initAdapter() {
        gitList = FileUtils.initDownloadStateList();
        adapter = new GitListAdapter(context, gitList);
        list.setAdapter(adapter);
    }

//    private void initActionBar() {
//        ActionBar bar = getSupportActionBar();
//        if (bar != null) {
//            bar.setLogo(R.drawable.ic_launcher);
//            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM
//                    | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initActions() {
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getEditUrlLegal(editUrl.getText().toString());
                if (!url.equals("false")) {
                    downloadInfo.setVisibility(View.VISIBLE);
                    progress_layout.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                    progressCount.setText(getString(R.string.progress_0));
                    startDownloadService(context, url);
                } else {
                    Toast.makeText(context, getString(R.string.url_error_msg),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, GitHomeActivity.class);
                DownloadState state = gitList.get(i);
                intent.putExtra(GitHomeActivity.FRAGMENT_INDEX, 0);
                intent.putExtra(GitHomeActivity.BUNDLE_HOME_NAME_KEY, state.getGitName());
                intent.putExtra(GitHomeActivity.BUNDLE_HOME_PATH_KEY, state.getExtractFilePath()
                        + File.separator + state.getGitName());
                startActivity(intent);
            }
        });
    }

    private String getEditUrlLegal(String url) {
        Pattern p = Pattern.compile("^([a-zA-Z]*)://([^ ]*)$");
        if (url.startsWith("http://") || url.startsWith("https://")) {
            if (p.matcher(url).matches()) {
                return url;
            }
        } else {
            if (p.matcher("https://" + url).matches()) {
                return "https://" + url;
            }
        }
        return "false";
    }

    private void startDownloadService(Context context, String url) {
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                DownloadBinder binder = (DownloadBinder) iBinder;
                binder.setDownloadInfo(downloadInfo);
                binder.setProgressBar(progressBar);
                binder.setProgressBarCount(progressCount);
                binder.addCallBack(MainActivity.this);
                binder.start();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        downloadIntent = new Intent(context, DownloadService.class);
        downloadIntent.putExtra(AppConfig.BUNDLE_KEY_DOWNLOAD_URL, url);
        startService(downloadIntent);
        bindService(downloadIntent, conn, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            stopService(downloadIntent);
            unbindService(conn);
            conn = null;
        }
    }

    @Override
    public void onBackResult(Object s) {
        DownloadState state = (DownloadState) s;
        if (state.getState().equals(DownloadState.EXTRACT_SUCCESSS)) {
            progress_layout.setVisibility(View.GONE);
            addGitToList(state);
            adapter.notifyDataSetChanged();
        }
    }

    private void addGitToList(DownloadState state) {
        String gitName = state.getGitName();
        for (int i = 0; i < gitList.size(); i++) {
            if (gitName.equals(gitList.get(i).getGitName())) {
                gitList.remove(i);
                gitList.add(i, state);
                return;
            }
        }
        gitList.add(state);
    }
}
