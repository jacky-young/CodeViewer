package com.jack.codeviewer.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jack.codeviewer.R;
import com.jack.codeviewer.interf.OnBackListener;
import com.jack.codeviewer.ui.GitHomeActivity;
import com.jack.codeviewer.ui.HTMLViewerActivity;
import com.jack.codeviewer.utils.FileUtils;
import com.jack.codeviewer.utils.GitPage;

import java.io.File;
import java.util.List;

/**
 * Created by jack on 2/24/16.
 */
public class BaseListFragment extends BaseFragment implements OnBackListener{

    private ListView listView;
    private BaseListAdapter adapter;

    private GitPage gitPage;
    private List<String> pathList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GitHomeActivity) getActivity()).setOnBackListener(this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.simple_list_view, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);
        initAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String path = gitPage.getParent() + File.separator + pathList.get(position);
                if (FileUtils.isDirectory(path)) {
                    gitPage.setParent(path);
                    gitPage.levelLower();
                    gitPage = FileUtils.getLowerGitPage(gitPage);
                    pathList = gitPage.getElementList();
                    getActivity().setTitle(FileUtils.getShortTitle(gitPage));
                    adapter.refresh(pathList);
                    adapter.notifyDataSetChanged();
                } else if (FileUtils.isFile(path)) {
                    Intent intent = new Intent(getActivity(), HTMLViewerActivity.class);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("file://" + path));
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    private void initAdapter() {
        String currentPath = getArguments().getString(GitHomeActivity.BUNDLE_HOME_PATH_KEY);
        String gitName = getArguments().getString(GitHomeActivity.BUNDLE_HOME_NAME_KEY);
        gitPage = new GitPage();
        gitPage.setGitName(gitName);
        gitPage.setHomePath(currentPath);
        gitPage.setParent(currentPath);
        gitPage = FileUtils.getLowerGitPage(gitPage);
        pathList = gitPage.getElementList();
        adapter = new BaseListAdapter(getActivity(), pathList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        gitPage.levelUpper();
        gitPage = FileUtils.getUpperGitPage(gitPage);
        pathList = gitPage.getElementList();
        getActivity().setTitle(FileUtils.getShortTitle(gitPage));
        adapter.refresh(pathList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean isTopLevel() {
        return gitPage.getLevelCount() == 0;
    }

    private static class BaseListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<String> gitList;

        BaseListAdapter(Context context, List<String> list) {
            inflater = LayoutInflater.from(context);
            gitList = list;
        }

        @Override
        public int getCount() {
            return gitList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (convertView == null) {
                v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new ViewHolder();
                holder.textView = (TextView) v.findViewById(android.R.id.text1);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.textView.setText(gitList.get(position));
            return  v;
        }

        public void refresh(List<String> list) {
            gitList = list;
            notifyDataSetChanged();
        }
    }

    static class ViewHolder{
        TextView textView;
    }

}
