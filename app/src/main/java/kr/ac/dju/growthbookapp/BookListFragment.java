package kr.ac.dju.growthbookapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        _rootView = super.onCreateView(inflater, container, savedInstanceState);
        setWithCommonNavigationBar(getResources().getString(R.string.booklist), (View v) -> {

            MainActivity ma = (MainActivity) this.getActivity();
            ma.toggleMenu();
        }, (View v) -> {

        });

        ListView listView = (ListView) _rootView.findViewById(R.id.list_book);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, booklist);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                parent.getItemAtPosition(position);
                                                DetailBookListFragment page1 = new DetailBookListFragment();
                                                switch(position)
                                                {
                                                    case 0: {
                                                              String number = "01";
                                                              Bundle numbers = new Bundle();
                                                              numbers.putString("key", number);
                                                              page1.setArguments(numbers);
                                                        break;

                                                    }
                                                    case 1: {
                                                        String number = "02";
                                                        Bundle numbers = new Bundle();
                                                        numbers.putString("key", number);
                                                        page1.setArguments(numbers);
                                                        break;
                                                    }
                                                    case 2: {
                                                        String number = "03";
                                                        Bundle numbers = new Bundle();
                                                        numbers.putString("key", number);
                                                        page1.setArguments(numbers);
                                                        break;
                                                    }
                                                    case 3: {
                                                        String number = "04";
                                                        Bundle numbers = new Bundle();
                                                        numbers.putString("key", number);
                                                        page1.setArguments(numbers);
                                                        break;
                                                        }

                                                    case 4: {
                                                        String number = "05";
                                                        Bundle numbers = new Bundle();
                                                        numbers.putString("key", number);
                                                        page1.setArguments(numbers);
                                                        break;
                                                    }
                                                    case 5: {
                                                        String number = "06";
                                                        Bundle numbers = new Bundle();
                                                        numbers.putString("key", number);
                                                        page1.setArguments(numbers);
                                                        break;
                                                    }
                                                    case 6: {
                                                        String number = "07";
                                                        Bundle numbers = new Bundle();
                                                        numbers.putString("key", number);
                                                        page1.setArguments(numbers);
                                                        break;
                                                    }
                                                    case 7: {
                                                        String number = "08";
                                                        Bundle numbers = new Bundle();
                                                        numbers.putString("key", number);
                                                        page1.setArguments(numbers);
                                                        break;
                                                    }
                                                    default:
                                                        break;


                                                }

                                                FragmentManager fragmentManager = getFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                fragmentTransaction.add(R.id.front_side_container, page1);
                                                fragmentTransaction.addToBackStack(null);
                                                fragmentTransaction.commit();
                                            }
                                        });

        return _rootView;
    }
}








