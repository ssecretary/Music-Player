package com.music.mp3player.audio.mediaplayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.music.mp3player.audio.mediaplayer.Interface.AdEventListener;
import com.music.mp3player.audio.mediaplayer.Utils.AppUtils;
import com.music.mp3player.audio.mediaplayer.activities.MainActivity;

import java.util.Random;

public class AdmobAdManager {

    private static final String TAG = "AdmobAdManager";
    private static AdmobAdManager singleton;
    public  InterstitialAd interstitialAd;
    public boolean isAdLoad = false;
    public  boolean isAdLoadProcessing = false;
    public  boolean isAdLoadFailed = false;
    private ProgressDialog progressDialog;

    public AdmobAdManager(Context context) {
        setUpProgress(context);
//        loadInterstitialAd(context, context.getResources().getString(R.string.interstitial_id));
    }


    public static AdmobAdManager getInstance(Context context) {
        if (singleton == null) {
            singleton = new AdmobAdManager(context);
        }

        return singleton;
    }

    public void setUpProgress(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Ad Showing...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void showProgress() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

//    public void loadInterstitialAd(Context context, String interstitialAdID) {
//        try {
//            if (AppUtils.isNetworkAvailable(context)) {
//                interstitialAd = new InterstitialAd(context);
//                interstitialAd.setAdUnitId(interstitialAdID);
//                interstitialAd.loadAd(new AdRequest.Builder().build());
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, "loadInterstitialAd: " + e.toString());
//            e.printStackTrace();
//        }
//    }

    public void loadInterstitialAd(Context context, String interstitialAdID) {
        if (interstitialAd == null && !isAdLoadProcessing) {
            isAdLoadProcessing = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, interstitialAdID, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAds) {
                    isAdLoad = true;
                    isAdLoadFailed = false;
                    isAdLoadProcessing = false;
                    interstitialAd = interstitialAds;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    isAdLoad = false;
                    isAdLoadFailed = true;
                    isAdLoadProcessing = false;

                }
            });
        }
    }


    public void loadInterstitialAd(Activity context, String interstitialAdID, int number, OnAdClosedListener onAdClosedListener) {
        Random random = new Random();
        int r = random.nextInt(number);
        if (number == 1 || r == 1) {
            if (interstitialAd != null) {
                if (isAdLoad && !isAdLoadFailed && !isAdLoadProcessing) {
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            interstitialAd = null;
                            isAdLoad = false;
                            isAdLoadProcessing = false;
                            isAdLoadFailed = false;
                            onAdClosedListener.onAdClosed();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            isAdLoad = false;
                            isAdLoadProcessing = false;
                            isAdLoadFailed = false;
                            interstitialAd = null;
                            loadInterstitialAd(context, interstitialAdID);
                            onAdClosedListener.onAdClosed();
                        }
                    });
                    interstitialAd.show(context);
                }
                else {
                    Log.e("Ads", "Ads still loading!");
                    onAdClosedListener.onAdClosed();
                }
            } else {
                if (!TextUtils.isEmpty(interstitialAdID)) {
                    loadInterstitialAd(context, interstitialAdID);
                }
                onAdClosedListener.onAdClosed();
            }
        } else {
            onAdClosedListener.onAdClosed();
        }
    }


    public interface OnAdClosedListener {
        public void onAdClosed();
    }

    public InterstitialAd getInterstitialAd() {
        if (interstitialAd == null) {
            return null;
        }
        return interstitialAd;
    }


    public void LoadBanner(Context context, RelativeLayout adContainerView, String bannerAdID, final AdEventListener adEventListener) {
        try {
            if (AppUtils.isNetworkAvailable(context)) {
                AdView adView = new AdView(context);
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(bannerAdID);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        if (adEventListener != null) {
                            adEventListener.onAdLoaded(null);
                        }
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        if (adEventListener != null) {
                            adEventListener.onAdClosed();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.e(TAG, "onAdFailedToLoadBanner: " + loadAdError.getMessage());
                        if (adEventListener != null) {
                            adEventListener.onLoadError(loadAdError.getMessage());
                        }
                    }
                });
                adView.loadAd(adRequest);
                adContainerView.addView(adView);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "LoadBanner: " + e.toString());
        }

    }


    public void LoadAdaptiveBanner(Context context, RelativeLayout adContainerView, String bannerAdID, final AdEventListener adEventListener) {

        try {

            if (AppUtils.isNetworkAvailable(context)) {
                // Create an ad request. Check your logcat output for the hashed device ID to
                // get test ads on a physical device. e.g.
                // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
                AdView adView = new AdView(context);
                adView.setAdUnitId(bannerAdID);
                adContainerView.removeAllViews();
                adContainerView.addView(adView);

                final AdSize adSize = getAdSize(context, adContainerView);
                adView.setAdSize(adSize);

                AdRequest adRequest =
                        new AdRequest.Builder().build();
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        if (adEventListener != null) {
                            adEventListener.onAdLoaded(null);
                        }
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();

                        if (adEventListener != null) {
                            adEventListener.onAdClosed();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        if (adEventListener != null) {
                            adEventListener.onLoadError(loadAdError.getMessage());
                        }
                        Log.e(TAG, "onAdFailedAdaptiveBanner: " + loadAdError.toString());
                    }
                });

                // Start loading the ad in the background.
                adView.loadAd(adRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "LoadAdaptiveBanner: " + e.toString());
        }

    }

    public AdSize getAdSize(Context context, RelativeLayout adContainerView) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);

        //  return AdSize.getCurrentOrientationBannerAdSizeWithWidth(context, adWidth);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public void LoadNativeAd(final Context context, String nativeAdID, final AdEventListener adEventListener) {
        AdLoader.Builder builder = new AdLoader.Builder(context, nativeAdID);

        builder.forNativeAd(unifiedNativeAd -> {
            if (adEventListener != null) {
                adEventListener.onAdLoaded(unifiedNativeAd);
            }

        }).withAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (adEventListener != null) {
                    adEventListener.onLoadError(loadAdError.getMessage());
                }
                Log.e(TAG, "onAdFailedToLoadNative:" + loadAdError.getCode());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void populateUnifiedNativeAdView(Context context, FrameLayout frameLayout, NativeAd nativeAd, boolean isShowMedia, boolean isGrid) {
        if (AppUtils.isNetworkAvailable(context)) {
            LayoutInflater inflater = LayoutInflater.from(context);
            // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
            NativeAdView adView;
            if (isGrid) {
                adView = (NativeAdView) inflater.inflate(R.layout.layout_big_native_ad_mob, null);
            } else {
                adView = (NativeAdView) (isShowMedia ?
                        inflater.inflate(R.layout.layout_big_native_ad_mob, null) :
                        inflater.inflate(R.layout.layout_small_native_ad_mob, null));
            }

            if (frameLayout != null) {
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
                frameLayout.setVisibility(View.VISIBLE);
            }
            try {
                MediaView mediaView = adView.findViewById(R.id.mediaView);
                mediaView.setMediaContent(nativeAd.getMediaContent());
//            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                adView.setMediaView(mediaView);
//
//                if (color == 1) {
//                    mediaView.setVisibility(View.VISIBLE);
//                } else {
//                    mediaView.setVisibility(View.GONE);
//                }

                adView.setHeadlineView(adView.findViewById(R.id.adTitle));
                adView.setBodyView(adView.findViewById(R.id.adDescription));
                adView.setIconView(adView.findViewById(R.id.adIcon));

                ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
//                adView.getMediaView().setMediaContent(nativeAd.getMediaContent());


                if (nativeAd.getBody() == null) {
                    adView.getBodyView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getBodyView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
                }


                if (nativeAd.getIcon() == null) {
                    adView.getIconView().setVisibility(View.GONE);
                } else {
                    ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                    adView.getIconView().setVisibility(View.VISIBLE);
                }

                if (isShowMedia) {
                    adView.getMediaView().setVisibility(View.GONE);
                } else {
                    adView.getMediaView().setVisibility(View.GONE);
                }

                adView.setNativeAd(nativeAd);
                VideoController vc = nativeAd.getMediaContent().getVideoController();
                vc.mute(true);
                if (vc.hasVideoContent()) {
                    vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                        @Override
                        public void onVideoEnd() {
                            super.onVideoEnd();
                        }
                    });
                }


                adView.setNativeAd(nativeAd);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "populateUnifiedNativeAdView Exception: " + e.getMessage());
            }
        }
    }

}
