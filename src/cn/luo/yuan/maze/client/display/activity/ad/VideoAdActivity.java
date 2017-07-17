package cn.luo.yuan.maze.client.display.activity.ad;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.luo.yuan.maze.R;
import sw.ls.ps.normal.common.ErrorCode;
import sw.ls.ps.normal.video.VideoAdListener;
import sw.ls.ps.normal.video.VideoAdManager;
import sw.ls.ps.normal.video.VideoAdSettings;

/**
 * <p>视频广告演示窗口</p>
 * Created by Alian Lee on 2016-11-25 11:24.
 */
public class VideoAdActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_ad);

		// 设置视频广告
		setupVideoAd();
	}

	/**
	 * 设置视频广告
	 */
	private void setupVideoAd() {
		// 设置视频广告
		final VideoAdSettings videoAdSettings = new VideoAdSettings();
		videoAdSettings.setInterruptTips("退出视频播放将无法获得奖励" + "\n确定要退出吗？");

		//		// 只需要调用一次，由于在主页窗口中已经调用了一次，所以此处无需调用
		//		VideoAdManager.getInstance().requestVideoAd(mContext);

		Button btnShowVideoAd = (Button) findViewById(R.id.btn_show_video_ad);
		btnShowVideoAd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 展示视频广告
				VideoAdManager.getInstance(mContext)
						.showVideoAd(mContext, videoAdSettings, new VideoAdListener() {
							@Override
							public void onPlayStarted() {
								logDebug("开始播放视频");
							}

							@Override
							public void onPlayInterrupted() {
								showShortToast("播放视频被中断");
							}

							@Override
							public void onPlayFailed(int errorCode) {
								logError("视频播放失败");
								switch (errorCode) {
								case ErrorCode.NON_NETWORK:
									logError("网络异常");
									break;
								case ErrorCode.NON_AD:
									logError("视频暂无广告");
									break;
								case ErrorCode.RESOURCE_NOT_READY:
									logError("视频资源还没准备好");
									break;
								case ErrorCode.SHOW_INTERVAL_LIMITED:
									logError("视频展示间隔限制");
									break;
								case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
									logError("视频控件处在不可见状态");
									break;
								default:
									logError("请稍后再试");
									break;
								}
							}

							@Override
							public void onPlayCompleted() {
								showShortToast("视频播放成功");
							}
						});
			}
		});
	}
}
