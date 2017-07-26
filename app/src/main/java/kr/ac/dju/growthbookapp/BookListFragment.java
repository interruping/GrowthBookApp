package kr.ac.dju.growthbookapp;



import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ListView;




/**
 * Created by geonyounglim on 2017. 5. 31..
 */

public class BookListFragment extends NavigationBarFragment {

    private View _rootView;

    static final String[] booklist = {"ACE 독서인증", "일반인문학", "동양문학", "서양문학", "인문학 일반", "사회과학", "과학 일반", "자연과학"};

    public BookListFragment() {
        super(R.layout.fragment_book_list, R.id.root_constraint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        _rootView = super.onCreateView(inflater, container, savedInstanceState);
        setWithCommonNavigationBar(getResources().getString(R.string.booklist), (View v) -> {

            MainActivity ma = (MainActivity) this.getActivity();
            ma.toggleMenu();
        }, (View v) -> {

        });

        hideRightAcc();

        ListView listView = (ListView) _rootView.findViewById(R.id.list_book);

        BookListAdapter adapter = new BookListAdapter();

        for ( int id = 0; id < booklist.length; id++ ) {
            adapter.addItem(new BookListAdapter.BookGenreItem(id, booklist[id]));
        }

        listView.setAdapter(adapter);
        listView.setDividerHeight(1); //임근영이 추가 함.



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                //parent.getItemAtPosition(position);
                                                DetailBookListFragment page1 = new DetailBookListFragment();


                                                Bundle numbers = new Bundle();
                                                numbers.putString("key", String.valueOf(position+1));
                                                numbers.putString("title",booklist[position]);
                                                pushFragmentTo(R.id.front_side_container, page1, numbers);

                                            }


        });

        return _rootView;
    }
}








