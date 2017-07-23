package kr.ac.dju.growthbookapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dju.book.HttpConn;

/**
 * Created by geonyounglim on 2017. 5. 28..
 */

public class BackMenuFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView _menuListView;
    private MenuListAdapter _adapter;
    private View.OnClickListener _loginOnClickListener;
    private View.OnClickListener _logoutOnClickListener;

    private boolean _isLogin;
    private Button _loginBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_back_menu, container, false);

        _menuListView = (ListView) rootView.findViewById(R.id.menu_list_view);

        _adapter = new MenuListAdapter();
        _menuListView.setAdapter(_adapter);


        _adapter.addItem(R.layout.fragment_news, ContextCompat.getDrawable(getActivity().getApplicationContext()
                , R.drawable.news_icon), getResources().getString(R.string.news));
        _adapter.addItem(R.layout.fragment_book_event, ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.event_icon), getResources().getString(R.string.event));
        _adapter.addItem(R.layout.fragment_mileage, ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.mileage_icon), getResources().getString(R.string.mileage));
        _adapter.addItem(R.layout.fragment_one_by_one, ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.one_by_one_icon), getResources().getString(R.string.one_by_one));
        _adapter.addItem(R.layout.fragment_book_list, ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.booklist_icon), getResources().getString(R.string.booklist));
        _adapter.addItem(R.layout.fragment_recent_pass_book_test, ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.recent_pass), "최근 합격 시험");
        _menuListView.setOnItemClickListener(this);

        _isLogin = false;
        _loginBtn = (Button) rootView.findViewById(R.id.loginout_btn);
        _loginOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _loginBtn.setOnClickListener(null);
                MainActivity ma = (MainActivity) getActivity();
                ma.switchFrontFragment(R.layout.fragment_login);
            }
        };
        _logoutOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _isLogin = false;
                HttpConn.CookieStorage.sharedStorage().setCookie("");
                _loginBtn.setText("로그인이 필요합니다");
                _loginBtn.setOnClickListener(_loginOnClickListener);
                MainActivity ma = (MainActivity) getActivity();
                ma.switchFrontFragment(R.layout.fragment_login);

            }
        };
        _loginBtn.setOnClickListener(_loginOnClickListener);

        return rootView;
    }

    public void activeItemClick() {
        _menuListView.setOnItemClickListener(this);
        if ( _isLogin == true ) {
            _loginBtn.setOnClickListener(_logoutOnClickListener);
        } else if ( _isLogin == false ){
            _loginBtn.setOnClickListener(_loginOnClickListener);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        _menuListView.setOnItemClickListener(null);
        _loginBtn.setOnClickListener(null);
        _adapter.removeAllHightLight(_menuListView);
        MainActivity ma = (MainActivity) getActivity();
        View highlightLine = view.findViewById(R.id.highlight_line);
        TextView highlightText = (TextView) view.findViewById(R.id.menu_string);
        highlightText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorHighLight));
        highlightLine.setVisibility(View.VISIBLE);
        ma.switchFrontFragment((int) id);
    }

    public void changeToLogout() {
        _isLogin = true;
        _loginBtn.setText("로그아웃");

        _loginBtn.setOnClickListener(

        _logoutOnClickListener);
    }

}
