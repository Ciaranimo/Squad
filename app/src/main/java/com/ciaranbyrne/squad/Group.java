package com.ciaranbyrne.squad;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by ciaranbyrne on 04/04/2017.
 */

public class Group {
    public List<User> memberList = new List<User>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @NonNull
        @Override
        public Iterator<User> iterator() {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] ts) {
            return null;
        }

        @Override
        public boolean add(User user) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends User> collection) {
            return false;
        }

        @Override
        public boolean addAll(int i, @NonNull Collection<? extends User> collection) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public User get(int i) {
            return null;
        }

        @Override
        public User set(int i, User user) {
            return null;
        }

        @Override
        public void add(int i, User user) {

        }

        @Override
        public User remove(int i) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<User> listIterator() {
            return null;
        }

        @NonNull
        @Override
        public ListIterator<User> listIterator(int i) {
            return null;
        }

        @NonNull
        @Override
        public List<User> subList(int i, int i1) {
            return null;
        }
    };


    public Group() {
    }

    public Group(List<User> users) {
        this.memberList = users;
    }

    public void addMember(User member) {
        this.memberList.add(member);
    }


    public void test(String s){
        String x = "";

    }
}



