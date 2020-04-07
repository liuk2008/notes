## Activity \ view \ window 之间的关系 ##

**setContentView()分析**
	
* 1、setContentView()：底层调用AppCompatActivity的setContentView()方法

* 2、AppCompatActivity：底层通过调用生成AppCompatDelegate对象的setContentView()方法

		public void setContentView(@LayoutRes int layoutResID) {
			this.getDelegate().setContentView(layoutResID);
		}

		public AppCompatDelegate getDelegate() {
			if (this.mDelegate == null) {
			    this.mDelegate = AppCompatDelegate.create(this, this);
			}
			return this.mDelegate;
		}
		// 获取Window对象
		public static AppCompatDelegate create(Activity activity, AppCompatCallback callback) {
			return new AppCompatDelegateImpl(activity, activity.getWindow(), callback);
		}

* 3、getWindow() 返回一个mWindow对象，该对象在Activity类中attach方法中获得：

		mWindow = new PhoneWindow(this, window, activityConfigCallback);
		// 为窗设置一个回调，回调对象就是当前的activity
		mWindow.setCallback(this);


**AppCompatDelegateImpl 类中 setContentView()**

* 1、setContentView()：

		public void setContentView(int resId) {
			// 生成 mSubDecor 对象，底层 this.mSubDecor = this.createSubDecor();
			this.ensureSubDecor(); 
			// 查找 R.id.content，底层实际使用的PhoneWindow中的布局id
			ViewGroup contentParent = (ViewGroup)this.mSubDecor.findViewById(android.R.id.content);
			contentParent.removeAllViews();
			LayoutInflater.from(this.mContext).inflate(resId, contentParent);
			this.mOriginalWindowCallback.onContentChanged();
		}

* 2、createSubDecor()：生成 mSubDecor 对象，替换 android.R.id.content 布局

		private ViewGroup createSubDecor() {
			//在这里可设置主题样式
			 ...
			// 调用 PhoneWindow 方法，底层调用generateDecor和generateLayout
			this.mWindow.getDecorView();
			
			LayoutInflater inflater = LayoutInflater.from(this.mContext);
			ViewGroup subDecor = null;

			//填充 subDecor，不同的属性加载不同的XML布局
			...
			subDecor = (ViewGroup) inflater.inflate(R.layout.abc_screen_simple, null);
			...
			
			// 1、在 subDecor 中找到 id 为 action_bar_activity_content 布局 contentView
			ContentFrameLayout contentView = (ContentFrameLayout) subDecor.findViewById( R.id.action_bar_activity_content);

			// 2、在 PhoneWindow 中找到 id 为 android.R.id.content 布局 windowContentView         
			ViewGroup windowContentView = (ViewGroup) mWindow.findViewById(android.R.id.content);

			if (windowContentView != null) {
			
				while(windowContentView.getChildCount() > 0) {

					// 3、删除 windowContentView 中的所有子View
					View child = windowContentView.getChildAt(0);
					windowContentView.removeViewAt(0);

					// 4、 将所有 子View 添加到 subDecor 中的 contentView
					contentView.addView(child);
				}

				// 5、 清除 PhoneWindow 中 windowContentView 的id，并将 subDecor 中 contentView 的id替换为 windowContentView 中的id
				windowContentView.setId(-1);
				contentView.setId(android.R.id.content);
				if (windowContentView instanceof FrameLayout) {
				    ((FrameLayout)windowContentView).setForeground((Drawable)null);
				}

			}
			// 将 subDecor 添加到 PhoneWindow 中
			this.mWindow.setContentView(subDecor);
			...
			return subDecor;
		}

* 3、根据不同的属性加载其中一个XML文件：

		<android.support.v7.widget.FitWindowsLinearLayout
		    xmlns:android="http://schemas.android.com/apk/res/android"
		    android:id="@+id/action_bar_root"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent"
		    android:orientation="vertical"
		    android:fitsSystemWindows="true">

		    <android.support.v7.widget.ViewStubCompat
			android:id="@+id/action_mode_bar_stub"
			android:inflatedId="@+id/action_mode_bar"
			android:layout="@layout/abc_action_mode_bar"
			android:layout_width="match_parent"
			android:layout_height="wrap_content" />

		    <android.support.v7.widget.ContentFrameLayout
		    android:id="@id/action_bar_activity_content"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent"
		    android:foregroundGravity="fill_horizontal|top"
		    android:foreground="?android:attr/windowContentOverlay" />

		</android.support.v7.widget.FitWindowsLinearLayout>
		

**PhoneWindow 类中方法**

* 1、调用installDecor()生成DecorView

		public final View getDecorView() {
		    if (mDecor == null || mForceDecorInstall) {
			installDecor();
		    }
		    return mDecor;
		}

* 2、installDecor()底层调用generateDecor()、generateLayout()，当我们新启动一个Activity时 mDecor 和 mContentParent 都是为空的
	
		private void installDecor() {
			mForceDecorInstall = false;
			if (mDecor == null) {
			    mDecor = generateDecor(-1);
			    mDecor.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
			    mDecor.setIsRootNamespace(true);
			    if (!mInvalidatePanelMenuPosted && mInvalidatePanelMenuFeatures != 0) {
				mDecor.postOnAnimation(mInvalidatePanelMenuRunnable);
			    }
			} else {
			    mDecor.setWindow(this);
			}
			if (mContentParent == null) {
			    mContentParent = generateLayout(mDecor);   
			    ...
			    }
			}
		}

* 3、generateDecor创建DecorView
	
		protected DecorView generateDecor(int featureId) {
		    Context context;
		    if (mUseDecorContext) {
		        Context applicationContext = getContext().getApplicationContext();
		        if (applicationContext == null) {
		            context = getContext();
		        } else {
		            context = new DecorContext(applicationContext, getContext().getResources());
		            if (mTheme != -1) {
		                context.setTheme(mTheme);
		            }
		        }
		    } else {
		        context = getContext();
		    }
		    return new DecorView(context, featureId, this, getAttributes());
		}

* 4、generateLayout：根据不同的属性加载其中一个XML文件：R.layout.screen_simple，返回父ViewGroup，就是DecorView加载的布局，最后通过findViewById找到这个id为ID_ANDROID_CONTENT的子ViewGroup
		
		window中定义的常量：
		public static final int ID_ANDROID_CONTENT = com.android.internal.R.id.content;

		<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent"
		    android:fitsSystemWindows="true"
		    android:orientation="vertical">
		    <ViewStub android:id="@+id/action_mode_bar_stub"
		              android:inflatedId="@+id/action_mode_bar"
		              android:layout="@layout/action_mode_bar"
		              android:layout_width="match_parent"
		              android:layout_height="wrap_content"
		              android:theme="?attr/actionBarTheme" />
		    <FrameLayout
		         android:id="@android:id/content"
		         android:layout_width="match_parent"
		         android:layout_height="match_parent"
		         android:foregroundInsidePadding="false"
		         android:foregroundGravity="fill_horizontal|top"
		         android:foreground="?android:attr/windowContentOverlay" />
		</LinearLayout>

		

