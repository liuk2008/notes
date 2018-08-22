### Activity \ view \ window 之间的关系

### setContentView 方法分析：
	* setContentView(layoutId)
	* 调用activity中的方法：
		 public void setContentView(int layoutResID) {
      	  getWindow().setContentView(layoutResID);
   		 } 
	* 其中 getWindow() 返回一个mWindow对象，该对象在Activity类中attach方法中获得：
		 mWindow = PolicyManager.makeNewWindow(this);
         mWindow.setCallback(this); // 为窗设置一个回调，回调对象就是当前的activity
	* PolicyManager.makeNewWindow 的调用过程：
		private static final String POLICY_IMPL_CLASS_NAME ="com.android.internal.policy.impl.Policy";
		Class policyClass = Class.forName(POLICY_IMPL_CLASS_NAME);
        sPolicy = (IPolicy)policyClass.newInstance();
		public static Window makeNewWindow(Context context) {
	        return sPolicy.makeNewWindow(context);
	    }
		*  PolicyManager.makeNewWindow 其实是调用了com.android.internal.policy.impl.Policy.makeNewWindow(context);
	* com.android.internal.policy.impl.Policy类中代码：
		public PhoneWindow makeNewWindow(Context context) {
       		 return new PhoneWindow(context);
   		 }
	*** Activity中，setContentView 方法，其实是将view,设置给了一个window，该window是 new PhoneWindow(context)**

### PhoneWindow 类中 setContentView 方法：
	    public void setContentView(int layoutResID) {
        if (mContentParent == null) { // 第一次调用 setContentView 方法时mContentParent = null , ContentParent就是layoutResID 对象view的父view

            installDecor(); // Decor 装饰 
        } else {
            mContentParent.removeAllViews(); // 清空 mContentParent 中的内容
        }
        mLayoutInflater.inflate(layoutResID, mContentParent); // 将layoutResID 对象的view对象，添加至 mContentParent

        final Callback cb = getCallback(); // 获得phoneWindow对应的 activity  
        if (cb != null) {
            cb.onContentChanged(); // 执行 activity中的onContentChanged 方法 
        }
    }
	
### installDecor 方法：
	
    private void installDecor() {
        if (mDecor == null) { // 如果mDecor 是空就创建mDecor ，mDecor 其实就是一个FrameLayout
            mDecor = generateDecor();
        }
        if (mContentParent == null) { // 如果mContentParent 是null，就根据mDecor 创建mContentParent
            mContentParent = generateLayout(mDecor);

            mTitleView = (TextView)findViewById(com.android.internal.R.id.title);
            if (mTitleView != null) {
                if ((getLocalFeatures() & (1 << FEATURE_NO_TITLE)) != 0) {
                    View titleContainer = findViewById(com.android.internal.R.id.title_container);
                    if (titleContainer != null) {
                        titleContainer.setVisibility(View.GONE);
                    } else {
                        mTitleView.setVisibility(View.GONE);
                    }
                    if (mContentParent instanceof FrameLayout) {
                        ((FrameLayout)mContentParent).setForeground(null);
                    }
                } else {
                    mTitleView.setText(mTitle);
                }
            }
        }
### generateLayout 创建mContentParent 的代码分析：
	    protected ViewGroup generateLayout(DecorView decor) {
		// 根据当前窗体的属性，设置标记位
		if (a.getBoolean(com.android.internal.R.styleable.Window_windowNoTitle, false)) {
            requestFeature(FEATURE_NO_TITLE);
        }
		...
		// 根据 窗体的属性，如有没有标题，是否是全屏，activity是否是对话框样式，等，找到一个对应的 布局资源的ID layoutResourceId 
		 if (mIsFloating) {
            layoutResource = com.android.internal.R.layout.dialog_custom_title;
        } else {
            layoutResource = com.android.internal.R.layout.screen_custom_title;
        }
		...
		View in = mLayoutInflater.inflate(layoutResource, null); // 通过 layoutResourceId 获得view对象
        decor.addView(in, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)); // 将view 添加至 decorView

        ViewGroup contentParent = (ViewGroup)findViewById(ID_ANDROID_CONTENT); // 找到 ID = R.andoird.internal.content 的布局，就是contentParent
	