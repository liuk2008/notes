### Activity \ view \ window 之间的关系

### setContentView 方法分析：
	
	* setContentView()：底层调用AppCompatActivity的setContentView()方法
	* AppCompatActivity：底层通过调用生成AppCompatDelegate对象的setContentView()方法
	    
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
	
	* 其中 getWindow() 返回一个mWindow对象，该对象在Activity类中attach方法中获得：
		 mWindow = new PhoneWindow(this, window, activityConfigCallback);
         mWindow.setCallback(this); // 为窗设置一个回调，回调对象就是当前的activity

### AppCompatDelegateImpl 类中 setContentView 方法：

	* setContentView()方法：
	
		public void setContentView(int resId) {
			this.ensureSubDecor(); // 生成 mSubDecor 对象，底层 this.mSubDecor = this.createSubDecor();
			ViewGroup contentParent = (ViewGroup)this.mSubDecor.findViewById(android.R.id.content);
			contentParent.removeAllViews();
			LayoutInflater.from(this.mContext).inflate(resId, contentParent);
			this.mOriginalWindowCallback.onContentChanged();
		}
		
	* createSubDecor()方法：生成mSubDecor对象，替换 android.R.id.content 布局
	
		private ViewGroup createSubDecor() {
			//在这里可设置主题样式
			 ...
			 ...
			this.mWindow.getDecorView();
			LayoutInflater inflater = LayoutInflater.from(this.mContext);
			ViewGroup subDecor = null;
			
			//填充subDecor，不同的属性加载不同的XML布局
			...
			...
			subDecor = (ViewGroup) inflater.inflate(R.layout.abc_screen_simple, null);
			...
				...
			// 在subDecor中找到action_bar_activity_content
			ContentFrameLayout contentView = (ContentFrameLayout) subDecor.findViewById( R.id.action_bar_activity_content);
			// 找到Window中的 android.R.id.content 布局           
			ViewGroup windowContentView = (ViewGroup) mWindow.findViewById(android.R.id.content);
			if (windowContentView != null) {
			        while(windowContentView.getChildCount() > 0) {
						// 删除Window中的所有子View
			            View child = windowContentView.getChildAt(0);
			            windowContentView.removeViewAt(0);
						// 将所有子View添加到subDecor中
			            contentView.addView(child);
			        }
					// 清除Window中View中的id，并将subDecor中的id替换为Window中View中的id
			        windowContentView.setId(-1);
			        contentView.setId(android.R.id.content);
			        if (windowContentView instanceof FrameLayout) {
			            ((FrameLayout)windowContentView).setForeground((Drawable)null);
			        }
			}
			// 将 subDecor 添加到Window中
			this.mWindow.setContentView(subDecor);
			...
			return subDecor;
		}

	* 调用 Window 中的方法：
	
	  // 创建generateDecor和generateLayout的过程 
	  this.mWindow.getDecorView()

	  // 找到Window中的 android.R.id.content 布局           
      ViewGroup windowContentView = (ViewGroup) mWindow.findViewById(android.R.id.content);

### PhoneWindow 类中方法：

	* 调用installDecor()生成DecorView，底层调用generateDecor()、generateLayout()
	
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

		当我们新启动一个Activity是mDecor 和 mContentParent 都是为空的

	* generateDecor 创建 DecorView
	
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

	* generateLayout：根据不同的属性加载其中一个XML文件：R.layout.screen_simple，返回ViewGroup

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

	这就是DecorView加载的布局，通过代码找到这个id为ID_ANDROID_CONTENT的ViewGroup


	* 调用installDecor()生成DecorView
	
		public final View getDecorView() {
		    if (mDecor == null || mForceDecorInstall) {
		        installDecor();
		    }
		    return mDecor;
		}

	
		