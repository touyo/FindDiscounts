package com.android.testbaiduapi;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.Activity;  
import android.content.res.Configuration;  
import android.os.Bundle;  
import android.view.Menu;  
import android.widget.FrameLayout;  
import android.widget.Toast;  
import com.baidu.mapapi.BMapManager;  
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;  
import com.baidu.mapapi.map.MapController;  
import com.baidu.mapapi.map.MapPoi;  
import com.baidu.mapapi.map.MapView;  
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint; 

public class MainActivity extends Activity {
	BMapManager mBMapMan = null;  
	MapView mMapView = null; 
	MKSearch mMKSearch = null ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init("1ad52fcaf8c7df02515e84b39f32dbf5", null);  

		//注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		setContentView(R.layout.activity_main);
		mMapView=(MapView)findViewById(R.id.map_View);
		mMapView.setBuiltInZoomControls(true);
		//设置启用内置的缩放控件
		MapController mMapController=mMapView.getController();
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		//用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);//设置地图中心点
		mMapController.setZoom(12);//设置地图zoom级别
		mMapView.setTraffic(true); 
//		mMapView.
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapMan, new DiscountsSearching());//注意，MKSearchListener只支持一个，以最后一次设置为准
		// 北京西站
		GeoPoint ptLB = new GeoPoint( (int)(39.901375 * 1E6),(int)(116.329099 * 1E6)); 
		// 北京北站
		GeoPoint ptRT = new GeoPoint( (int)(39.949404 * 1E6),(int)(116.360719 * 1E6));
		mMKSearch.poiSearchInbounds("KFC", ptLB, ptRT);

	}
	@Override
	protected void onDestroy(){
	        mMapView.destroy();
	        if(mBMapMan!=null){
	                mBMapMan.destroy();
	                mBMapMan=null;
	        }
	        super.onDestroy();
	}
	@Override
	protected void onPause(){
	        mMapView.onPause();
	        if(mBMapMan!=null){
	               mBMapMan.stop();
	        }
	        super.onPause();
	}
	@Override
	protected void onResume(){
	        mMapView.onResume();
	        if(mBMapMan!=null){
	                mBMapMan.start();
	        }
	       super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public class DiscountsSearching implements MKSearchListener {  
		
	    @Override  
	    public void onGetAddrResult(MKAddrInfo result, int iError) {  
	           //返回地址信息搜索结果  
	    	//Log.println(1, null, null);
	    	result = null;
	    	iError = 1;
	    }  
	    @Override  
	    public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {  
	            //返回驾乘路线搜索结果  
	    }  
	 
	    @Override  
	    public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {  
	            //返回公交搜索结果  
	    }  
	    @Override  
	    public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {  
	            //返回步行路线搜索结果  
	    }  
	    @Override      
	    public void onGetBusDetailResult(MKBusLineResult result, int iError) {  
	            //返回公交车详情信息搜索结果  
	    }  
	    @Override  
	    public void onGetSuggestionResult(MKSuggestionResult result, int iError) {  
	            //返回联想词信息搜索结果  
	    }
	     @Override 
	     public void onGetShareUrlResult(MKShareUrlResult result , int type, int error) {
	           //在此处理短串请求返回结果. 
	    }
		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			

			if ( error == MKEvent.ERROR_RESULT_NOT_FOUND){
				Toast.makeText(MainActivity.this, "抱歉，未找到结果",Toast.LENGTH_LONG).show();
				return ;
			}
			else if (error != 0 || res == null) {	
				Toast.makeText(MainActivity.this, "搜索出错啦..", Toast.LENGTH_LONG).show();
				return;
			}
			// 将poi结果显示到地图上
			PoiOverlay poiOverlay = new PoiOverlay(MainActivity.this, mMapView);
			poiOverlay.setData(res.getAllPoi());
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(poiOverlay);
			mMapView.refresh();
			//当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
			for(MKPoiInfo info: res.getAllPoi()){
					if(info.pt!=null){
						mMapView.getController().animateTo(info.pt);
						break;
					}
				}
		}


	}
}
