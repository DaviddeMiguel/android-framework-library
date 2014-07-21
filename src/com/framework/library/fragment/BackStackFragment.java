package com.framework.library.fragment;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public class BackStackFragment {
	private ArrayList<FragmentSaved> fragments = new ArrayList<FragmentSaved>();
	private static BackStackFragment instance;
	
	public static BackStackFragment getInstance(){
		if(instance == null){
			instance = new BackStackFragment();
		}
		
		return instance;
	}
	
	/**
	 * Ejemplo de uso: BackStackFragment.getInstance().add(getActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom), new ShopsFragment(), R.id.content_frame)
	 * @param transaction
	 * @param fragment
	 * @param resid
	 */
	public void add(FragmentTransaction transaction, Fragment fragment, int resid){
// Con este codigo comentado, hace el efecto de irse el anterior y mostrar el nuevo
//		int size = fragments.size();
//		
//		if(size > 0){
//			FragmentSaved fragmentToRemove = fragments.get(size - 1);
//			fragments.remove(size - 1);
//			
//			transaction.remove(fragmentToRemove.fragment);
//		}
		
		fragments.add(new FragmentSaved(fragment, resid));
		transaction.add(resid, fragment).commit();
	}
	
	/**
	 * Ejemplo de uso: BackStackFragment.getInstance().remove(getSupportFragmentManager().beginTransaction()
        		.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom))
	 * @param transaction
	 * @return
	 */
	public boolean remove(FragmentTransaction transaction){
// Con este codigo comentado, hace el efecto de irse el actual y mostrar el anterior
		int size = fragments.size();
		
		if(size > 0){
			FragmentSaved fragmentToRemove = fragments.get(size - 1);
			fragments.remove(size - 1);
			
			transaction.remove(fragmentToRemove.fragment).commit();
			
//			size = fragments.size();
//			
//			if(size > 0){
//				FragmentSaved fragmentToAdd = fragments.get(size - 1);
//				
//				transaction.add(fragmentToAdd.resid, fragmentToAdd.fragment).commit();
//			}
			
			return true;
		}else{
			return false;
		}
	}
	
    static class FragmentSaved {
    	FragmentSaved(Fragment fragment, int resid){
    		this.fragment = fragment;
    		this.resid = resid;
    	}
    	
    	Fragment fragment;
    	int resid;
    }
}
