* Android中使用异步线程更新UI视图的几种方法

  1、runOnUiThread
        
    activity提供的一个轻量级更新ui的方法，在Fragment需要使用的时候要用getActivity.runOnUiThread开启线程
    这种方法最简单，方便更新一些不需要判断的通知，比如在聊天项目中动态获取未读消息数量。
        runOnUiThread(new Runnable() {
         @Override
         public void run() {
             Toast.makeText(context, "测试", Toast.LENGTH_SHORT).show();
         }
        });

  2、Handler message
        
    使用这个方法可以设置比如按钮倒计时的控制，也是比较常见的一种更新ui的方法。
    创建一个主线程用于接收子线程不断发送的消息，通过msg.what判断传递的消息类型。
    根据类型进行相关ui的更新操作。

  3、Handler Runnable

    同样也是需要先创建一个线程。

    Handler handler = new Handler();
        
    在一开始加载的地方使用postDelayed设置加载延迟

       handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          updataData();
        }
       }, 2000);
 
  4、AsyncTask    
   
    AsyncTask可以更加轻松地使用UI线程。该类允许执行后台操作并在UI线程上更新视图，而不需要操纵线
    程和处理程序。
    AsyncTask被设计为一个辅助类Thread，Handler 并且不构成通用线程框架。用于短时间更新操作。
    在使用的时候需要继承AsyncTask并重写方法：
        doInBackground：用于返回结果
        onProgressUpdate、onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
        onPostExecute：接收doInBackground的返回结果，用于更新UI