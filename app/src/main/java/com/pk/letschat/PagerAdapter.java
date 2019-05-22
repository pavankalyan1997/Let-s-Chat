package com.pk.letschat;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumtabs;
    public PagerAdapter(FragmentManager fm,int NumOfTabs) {
        super(fm);
        this.mNumtabs=NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                MessagesTab messagesTab=new MessagesTab();
                return messagesTab;
            case 1:
                GroupChatTab groupChatTab=new GroupChatTab();
                return groupChatTab;
            case 2:
                ContactsTab contactsTab=new ContactsTab();
                return contactsTab;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mNumtabs;
    }
}
