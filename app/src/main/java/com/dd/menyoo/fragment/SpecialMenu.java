package com.dd.menyoo.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dd.menyoo.R;
import com.dd.menyoo.adapter.RestaurantAdapter;
import com.dd.menyoo.adapter.SpecialTabAdapter;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.common.RoundedTransformation;
import com.dd.menyoo.model.MenuModel;
import com.dd.menyoo.model.RestaurantModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpecialMenu extends BaseFragment {


    public SpecialMenu() {
        // Required empty public constructor
    }
    RecyclerView mRvSpecialTab;
    ArrayList<MenuModel>  mSpecailMenuArray;
    ProgressBar pbWait;
    private Animator mCurrentAnimator;
    private ImageView enlargeView;
    private int mShortAnimationDuration;


    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_special_menu, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvSpecialTab =(RecyclerView) view.findViewById(R.id.rv_special_tab);
        pbWait = (ProgressBar) view.findViewById(R.id.pb_menu);
        enlargeView = (ImageView)view.findViewById(R.id.enlarge_view);
        setAdapter();
        getSpecailMenu();
    }

    SpecialTabAdapter scAdapter;
    public void setAdapter(){
        mSpecailMenuArray = new ArrayList<>();
        scAdapter = new SpecialTabAdapter(getActivity(),this);
        mRvSpecialTab.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        scAdapter.setData(mSpecailMenuArray);
        mRvSpecialTab.setAdapter(scAdapter);
    }

    protected void getSpecailMenu() {
        pbWait.setVisibility(View.VISIBLE);
        pbWait.bringToFront();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(getActivity(),
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                AppHelper.showConnectionAlert(getActivity());
                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo",obj.toString());
                afterGetMenu(obj);
            }
        });
        String url =String.format("GetSpecialMenuForRestaurant?restaurantid=%d&UserId=%d", AppController.
                getCurrentRestaurent().getRestaurantID(),AppController.getLoginUser().getUserId());
        String[] params = {getString(R.string.url_offline)+url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void afterGetMenu(Object obj) {
        try {
            JSONArray jsonArr = new JSONArray(obj.toString());
            for(int i=0;i<jsonArr.length();i++){
                JSONObject jObj = jsonArr.getJSONObject(i);
                int id = jObj.getInt("MenuId");
                String name = jObj.getString("Name");
                String description = jObj.getString("Description");
                double prize = jObj.getDouble("Price");
                boolean isSpecial =jObj.getBoolean("IsSpecial");
                boolean isPopular = jObj.getBoolean("IsPopular");
                boolean isFirstTime =  jObj.getBoolean("IsFirstTimer");
                String discount;
                if(isSpecial)
                     discount= jObj.getString("SpecialOfferOverlayText");
                else
                    discount="";
                String imageNAme = jObj.getString("SpecialDisplayPictureFile");
                if((!AppController.isFirstTimeOrderAdded()&&isFirstTime)||isSpecial||isPopular)
                mSpecailMenuArray.add(new MenuModel(name,description,
                        imageNAme,prize,id,isSpecial,discount,isPopular,isFirstTime));
            }
            pbWait.setVisibility(View.GONE);
            Collections.reverse(mSpecailMenuArray);
            scAdapter.setData(mSpecailMenuArray);
            scAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void zoomImageFromThumb(final View thumbView, String imageName) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) view.findViewById(
                R.id.enlarge_view);

        Picasso.with(getActivity())
                .load(imageName)

                .into(expandedImageView);
        //expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        view.findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        //thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    public void setEnlargeImage(String enlargeImage){
        enlargeView.setVisibility(View.VISIBLE);
        Picasso.with(getActivity())
                .load(enlargeImage)
                .into(enlargeView);
    }
}
