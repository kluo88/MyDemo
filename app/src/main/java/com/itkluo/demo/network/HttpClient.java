package com.itkluo.demo.network;

/**
 * 网络请求工具
 * <p>
 * =============================
 * 使用：
 * new HttpClient.Builder()
 * .baseUrl(..)
 * .interceptor(..)
 * .listener(..)
 * .build()
 * .request(...);
 * ================================
 *
 * @author luobingyong
 * @date 2020/10/9
 */
public class HttpClient {
//    public static String DEFAULT_URL = "http://xxxx";
//    private static OkHttpClient.Builder mOkHttpClient;
//    private ApiService mApiService;
//    private String mBaseUrl;
//    private Interceptor mInterceptor;
//
//    private HttpClient() {
//        mOkHttpClient = getOkHttp();
//        if (mInterceptor != null) {
//            mOkHttpClient.addInterceptor(mInterceptor);
//        }
//        mBaseUrl = TextUtils.isEmpty(mBaseUrl) ? DEFAULT_URL : mBaseUrl;
//        mApiService = createService(ApiService.class);
//    }
//
//    public ApiService getDefaultApiService() {
//        return mApiService;
//    }
//
//    /**
//     * 通过RetrofitClient提供的create()实例化你的API Service，自己传入Observer来解析数据。此方法定制化
//     * 低，可灵活使用
//     *
//     * @param observable observable
//     * @param subscriber subscriber
//     * @param <T>
//     */
//    public <T> Disposable request(Observable<T> observable, DisposableObserver<T> subscriber) {
//        observable.compose(HttpClient.<T>lifeTransFormer())
//                .compose(HttpClient.<T>handleErrTransformer())
//                .subscribe(subscriber);
//        return subscriber;
//    }
//
//    /**
//     * create ApiService
//     */
//    public <T> T createService(final Class<T> service) {
//        return getRetrofit(mBaseUrl, mOkHttpClient).create(service);
//    }
//
//    public static final class Builder {
//        private String baseUrl;
//        private Interceptor interceptor;
//
//        public Builder baseUrl(String baserUrl) {
//            this.baseUrl = baserUrl;
//            return this;
//        }
//
//        public Builder interceptor(Interceptor interceptor) {
//            this.interceptor = interceptor;
//            return this;
//        }
//
//        public HttpClient build() {
//            HttpClient httpClient = new HttpClient();
//            httpClient.mBaseUrl = baseUrl;
//            httpClient.mInterceptor = interceptor;
//            return httpClient;
//        }
//    }
//
//
//    /**
//     * 初始化Retrofit
//     */
//    private static Retrofit getRetrofit(String baseUrl, OkHttpClient.Builder okHttpClient) {
//        Retrofit.Builder builder = new Retrofit.Builder();
//        //支持返回Call<String>
//        builder.addConverterFactory(ScalarsConverterFactory.create());
//        //支持直接格式化json返回Bean对象
//        builder.addConverterFactory(GsonConverterFactory.create());
//        //支持RxJava
//        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
//        builder.baseUrl(baseUrl);
//        builder.client(okHttpClient.build());
//        return builder.build();
//    }
//
//    /**
//     * create OkHttp 初始化OKHttpClient,设置缓存,设置超时时间,设置打印日志
//     *
//     * @return Builder
//     */
//    private static OkHttpClient.Builder getOkHttp() {
////        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpInterceptorLogger());
////        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        HttpLogInterceptor interceptor = new HttpLogInterceptor().setLevel(HttpLogInterceptor.Level.BODY);
//        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS);
//
//        return mBuilder;
//    }
//
//    private static <T> ObservableTransformer<T, T> lifeTransFormer() {
//        ObservableTransformer<T, T> observableTransformer = new ObservableTransformer<T, T>() {
//
//            @Override
//            public ObservableSource<T> apply(Observable<T> upstream) {
//                return upstream.subscribeOn(Schedulers.io())
//                        .unsubscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread());
//            }
//        };
//        return observableTransformer;
//    }
//
//    private static <T> ObservableTransformer<T, T> handleErrTransformer() {
//        ObservableTransformer<T, T> observableTransformer = new ObservableTransformer<T, T>() {
//
//            @Override
//            public ObservableSource<T> apply(Observable<T> upstream) {
//                //拦截原Observable的onError通知, 可以创建并返回一个新的Observable
//                // 我这里通过Observable.error(...), 返回经过了处理的错误信息， 使订阅者这次还是收到onError通知
//                return upstream.onErrorResumeNext(new HttpClient.HttpResponseFunc<T>());
//
//
//            }
//        };
//        return observableTransformer;
//    }
//
//    /**
//     * onErrorResumeNext操作符会拦截（拦截指的是不会触发onError）发送给观察者的异常，
//     * 并发送出一个正常的item，这个item需要我们自己在 Function 中自定义
//     *
//     * @param <T>
//     */
//    private static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
//        @Override
//        public Observable<T> apply(Throwable throwable) throws Exception {
//            // 重新触发onError通知, 下发经过自己处理的异常信息
//            return Observable.error(DefineException.handleException(throwable));
//        }
//    }

}
