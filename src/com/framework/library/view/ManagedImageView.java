package com.framework.library.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;

import com.framework.library.connection.ConnectionInterfaces.ConnectionListener;
import com.framework.library.connection.DefaultConnection;
import com.framework.library.connection.HttpConnection;
import com.framework.library.exception.ConnectionException;
import com.framework.library.memory.FileCache;
import com.framework.library.memory.FlushedInputStream;
import com.framework.library.memory.StreamUtils;
import com.framework.library.model.ConnectionResponse;
import com.framework.library.model.IModel;
import com.framework.library.util.Functions;

public class ManagedImageView extends ImageView implements ConnectionListener{

	private static FileCache fileCache;
	
	private enum BackgroundType {DEFAULT, DOWNLOADED}
	
	private BackgroundType backgroundType = BackgroundType.DEFAULT;
	
	private ImageConnection connection;
	
	private String url;
	private Drawable drawable;
	private Drawable drawableDefault;
	private int idDrawable;
	private int idColor;
	private OnBackgroundChangedListener onBackgroundChangedListener;
	private int defaultWidth;
	private int defaultHeight;
	
	private boolean gridview;
	private boolean enableDownloadOnScrollingStop;
	
	private boolean firstCreation;
	
	private HandlerUI handlerUI;
	
	private int scaleFactor;
	
	private boolean dynamic;
	private boolean rounded;
		
	public ManagedImageView(Context context) {
		super(context);
		init(null);
	}

	public ManagedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public ManagedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	/**
	 * Inicializa los valores por defecto de la vista
	 */
	public void init(AttributeSet attrs){
		setFirstCreation(true);
		
	    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		   
	    DisplayMetrics displaymetrics = new DisplayMetrics();
	    wm.getDefaultDisplay().getMetrics(displaymetrics);
	    setDefaultWidth(displaymetrics.widthPixels);
	    setDefaultHeight(displaymetrics.heightPixels);
	    
	    if(attrs != null){
			try {
				dynamic = attrs.getAttributeBooleanValue(null, "dynamic", false);
				rounded = attrs.getAttributeBooleanValue(null, "rounded", false);
			} finally {}
	    }else{
	    	dynamic = false;
	    	rounded = false;
	    }
	    
	    if(handlerUI == null){
	    	handlerUI = new HandlerUI(getContext().getApplicationContext());
	    }
	}

	/**
	 * Activa el modo cache, si tiene memoria SD guardara las imagenes alli, sino, las guardara en el directorio cache
	 * del telefono
	 * @param context
	 * @param path
	 */
	public static void enableCache(Context context, String path){
		fileCache = new FileCache(context, path);
	}
	
	/**
	 * Desactiva el modo cache, no se guardara ninguna imagen en cache
	 */
	public static void disableCache(){
		if(fileCache != null){
			fileCache.clear();
			fileCache = null;	
		}
	}
	
	/**
	 * Elimina las imagenes guardadas en cache
	 */
	public static void clearCache(){
		if(fileCache != null){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					fileCache.clear();
				}
			}).start();
			
		}
	}

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		if(bitmap != null){
			super.setImageBitmap(bitmap);
		}
		
		if(getOnBackgroundChangedListener() != null){
			if(bitmap != null){
				getOnBackgroundChangedListener().onBackgroundChange((View)this, false);
			}else{
				getOnBackgroundChangedListener().onBackgroundChange((View) this, true);
			}
		}
	}

	@Override
	public void setImageResource(int resId) {
		if(resId < 0){
			super.setImageResource(resId);
		}
		
		if(getOnBackgroundChangedListener() != null){
			if(resId < 0){
				getOnBackgroundChangedListener().onBackgroundChange((View)this, false);
			}else{
				getOnBackgroundChangedListener().onBackgroundChange((View)this, true);
			}	
		}
	}

	@Override
	public void setImageURI(Uri uri) {
		if(uri != null){
			super.setImageURI(uri);
		}
		
		if(getOnBackgroundChangedListener() != null){
			if(uri != null){
				getOnBackgroundChangedListener().onBackgroundChange((View)this, false);
			}else{
				getOnBackgroundChangedListener().onBackgroundChange((View)this, true);
			}	
		}
	}

	public String getUrl() {
		if(url == null){
			return "";
		}else{
			return url;
		}	
	}

	public void setUrl(String url) {
		if(url != null){
			this.url = url.replace(" ", "%20");
		}else{
			this.url = url;
		}
	}

	ImageConnection getConnection() {
		return connection;
	}

	void setConnection(ImageConnection connection) {
		this.connection = connection;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public int getIdDrawable() {
		return idDrawable;
	}

	public void setIdDrawable(int idDrawable) {
		this.idDrawable = idDrawable;
	}

	public int getIdColor() {
		return idColor;
	}

	public void setIdColor(int idColor) {
		this.idColor = idColor;
	}

	/**
	 * Si el valor es true, actualizara las vistas una a una
	 * @return
	 */
	public boolean isGridview() {
		return gridview;
	}

	/**
	 * Al asignar un valor true, hara que no se puedan actualizar muchas vistas al mismo tiempo
	 * @param gridview
	 */
	public void setGridview(boolean gridview) {
		this.gridview = gridview;
	}
	
	/**
	 * Este metodo sirve para activar el modo OnScrollingStop, lo que quiere decir que solo descargara y actualizara
	 * la UI cuando la lista padre no este haciendo scroll
	 * @param enable
	 * @param absListView
	 */
	public void enableDownloadOnScrollingStop(boolean enable, AbsListView absListView){
		if(!isEnableDownloadOnScrollingStop() && enable){
			absListView.setOnScrollListener(getParentOnScrollListener());
		}else if(isEnableDownloadOnScrollingStop() && !enable){
			absListView.setOnScrollListener(null);
		}
		
		setEnableDownloadOnScrollingStop(enable);
	}

	public boolean isEnableDownloadOnScrollingStop() {
		return enableDownloadOnScrollingStop;
	}

	public void setEnableDownloadOnScrollingStop(
			boolean enableDownloadOnScrollingStop) {
		this.enableDownloadOnScrollingStop = enableDownloadOnScrollingStop;
	}

	public OnBackgroundChangedListener getOnBackgroundChangedListener() {
		return onBackgroundChangedListener;
	}

	public void setOnBackgroundChangedListener(
			OnBackgroundChangedListener onBackgroundChangedListener) {
		this.onBackgroundChangedListener = onBackgroundChangedListener;
	}
	
	public int getDefaultWidth() {
		return defaultWidth;
	}

	public void setDefaultWidth(int defaultWidth) {
		this.defaultWidth = defaultWidth;
	}

	public int getDefaultHeight() {
		return defaultHeight;
	}

	public void setDefaultHeight(int defaultHeight) {
		this.defaultHeight = defaultHeight;
	}
	
	public int getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(int scaleFactor) {
		this.scaleFactor = scaleFactor;
	}
	
	/**
	 * Es el tipo de background que tiene asignada la vista, puede ser por DEFAULT o DOWNLOADED
	 * @return
	 */
	BackgroundType getBackgroundType() {
		return backgroundType;
	}

	void setBackgroundType(BackgroundType backgroundType) {
		this.backgroundType = backgroundType;
	}

	/**
	 * Devuelve el OnScrollListener de la lista padre
	 * @return
	 */
	private OnScrollListener getParentOnScrollListener() {
		return parentScrollListener;
	}

	/**
	 * Este valor sirve para saber si la vista esta recien creada o se esta reutilizando
	 * @return
	 */
	public boolean isFirstCreation() {
		return firstCreation;
	}

	public void setFirstCreation(boolean firstCreation) {
		this.firstCreation = firstCreation;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
	public void cancelDownload(){
		if(getConnection() != null){
			getConnection().cancel();
		}
	}

	/**
	 * Descarga la imagen y asigna una imagen por defecto a la vista mientras se descarga
	 * @param url
	 * @param drawable
	 */
	public synchronized void download(final String url, final Drawable drawable){	
		drawableDefault = drawable;
		
		if(!TextUtils.isEmpty(url) && URLUtil.isValidUrl(url)){
			setUrl(url);
			
			if(existInCache(url)){
				download(url);
			}else{
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						final Drawable d = scaleDrawable(drawable);
						
						handlerUI.post(new Runnable() {
							public void run() {	
								if(getDrawable() == null || getDrawable() != drawable){
									setDrawable(drawable);
									setImageDrawable(d);
								}
								
								download(url);
							}
						});
					}
				}).start();
			}
		}else{
			new Thread(new Runnable() {
				@Override
				public void run() {
					final Drawable d = scaleDrawable(drawable);
					
					handlerUI.post(new Runnable() {
						public void run() {	
							if(getDrawable() == null || getDrawable() != drawable){
								setDrawable(drawable);
								setImageDrawable(d);
							}
						}
					});
				}
			}).start();
		}
	}
	
	/**
	 * Descarga la imagen desde una url y asigna un color como fondo a la vista mientras se descarga
	 * @param url
	 * @param color
	 */
	public synchronized void download(final String url, final int color){	
		if(!TextUtils.isEmpty(url) && URLUtil.isValidUrl(url)){
			setUrl(url);
			
			if(existInCache(url)){
				download(url);
			}else{
				handlerUI.post(new Runnable() {
					public void run() {								
						setBackgroundColor(color);
						download(url);
					}
				});
			}
		}else{
			handlerUI.post(new Runnable() {
				public void run() {								
					setBackgroundColor(color);
				}
			});
		}
	}
	
	/**
	 * Descarga la imagen desde una url
	 * @param url
	 */
	public synchronized void download(final String url){
		setBackgroundType(BackgroundType.DEFAULT);
		download(url, isEnableDownloadOnScrollingStop());
	}
	
	/**
	 * Comprueba si existe la imagen en cache, si existe actualiza la UI, si no existe la descarga en caso de que no este
	 * en modo OnScrollingStop (que actualice la UI solo cuando haya terminado de hacer scroll la lista padre)
	 * @param url
	 * @param enableDownloadOnScrollingStop
	 */
	private synchronized void download(final String url, boolean enableDownloadOnScrollingStop){
		if(url != null && URLUtil.isValidUrl(url)){
			if(getConnection() != null){
				getConnection().cancel();
			}

			if(existInCache(url)){
				new Thread(new Runnable() {
					public void run() {
						try{
							setImageDownloaded(decodeFile(fileCache.getFile(String.valueOf(url.hashCode()))));
						}catch(Exception e){
							Functions.log(getClass().getSimpleName(), "Error loading url: " + url);
							Functions.log(e);
						}
					}
				}).start();
			}else{
				if(!enableDownloadOnScrollingStop || isFirstCreation()){
					executeDownload(url);
				}
			}	
		}
		
		setFirstCreation(false);
	}
	
	/**
	 * Ejecuta la descarga desde una url dada
	 * @param url
	 */
	private void executeDownload(String url){
		setConnection(new ImageConnection(getContext(), this, url));
		getConnection().request();	
	}
	
	public void load(final String path){
		new Thread(new Runnable() {
			public void run() {
				try{
					//setImageDownloaded(decodeFile(fileCache.getFileBypath(path)));
					setImageDownloaded(decodeFile(new File(path)));
				}catch(Exception e){
					Functions.log(getClass().getSimpleName(), "Error loading url: " + url);
					Functions.log(e);
				}
			}
		}).start();
	}
	
	/**
	 * Comprueba si existe en cache
	 * @param url
	 * @return
	 */
	private boolean existInCache(String url){
		if(fileCache != null && url != null){
			final File file = fileCache.getFile(String.valueOf(url.hashCode()));
			
			if(file != null && file.exists()){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Escala un drawable para ajustar el tamaï¿½ï¿½o a la vista
	 * @param drawable
	 * @return
	 */
	public Drawable scaleDrawable(Drawable drawable){
		if(drawable != null){
			int width = getWidth();
			int height = getHeight();
			
		    if(width == 0){
		    	width = getDefaultWidth();
		    }
		    
		    if(height == 0){
		    	height = getDefaultHeight();
		    }
			
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			if(width < bitmap.getWidth() && height < bitmap.getHeight()){
				return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true));
			}
		}

		return drawable;
	}
	
	/**
	 * Calcula las propiedades que tiene que tener una imagen en funcion del tamaï¿½ï¿½o de la vista (siempre menor o igual
	 * que la vista)
	 * @param path
	 * @return
	 */
	public BitmapFactory.Options getBitmapOptions(String path){
	    int targetW;
	    int targetH;
	    
	    if(isDynamic()){
		    targetW = getDefaultWidth();
		    targetH = getDefaultHeight();
	    }else{
		    targetW = getWidth();
		    targetH = getHeight();
		    
		    if(targetW == 0){
		    	targetW = getDefaultWidth();
		    }
		    
		    if(targetH == 0){
		    	targetH = getDefaultHeight();
		    }
	    }
	    
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	  
	    scaleFactor = 1;
	    
	    if(targetW > 0 && targetH > 0){
		    while(photoW > targetW || photoH > targetH){
		    	scaleFactor++;
		    	photoH = photoH / 2;
		    	photoW = photoW / 2;
		    }	
	    }
	  
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	    
	    return bmOptions;
	}

	@Override
	public void onConnectionComplete(ConnectionResponse response) {}

	@Override
	public void onConnectionStart(ConnectionResponse response) {}
	
	@Override
	public void onConnectionHeader(ConnectionResponse response) {}
	
	@Override
	public void onConnectionBitmap(ConnectionResponse response) {}

	@Override
	public void onConnectionError(ConnectionException exception) {
		if(exception != null && exception.getUrl() != null){
			Functions.log(getClass().getSimpleName(), "Error downloading url: " + exception.getUrl());
		}
		
		Functions.log(exception.getException());
	}

	/**
	 * Este metodo se llama cuando ha terminado la conexion y todo ha ido bien.
	 * Su funcion es de crear un bitmap (desde el fichero que guardara en cache o desde la respuesta del servidor directamente
	 * si no tiene cache) y actualiza la UI.
	 */
	@Override
	public void onConnectionResponse(final ConnectionResponse response) {
		if(response != null && getUrl().equals(response.getUrl())){
			
			Runnable runnable = new Runnable() {
				public void run() {
					try{
						if(fileCache != null){
							File file = saveResponse(response);
							if(file != null){
								setImageDownloaded(decodeFile(file));
							}	
						}else{
							InputStream inputStream = ((HttpResponse)response.getData()).getEntity().getContent();
							setImageDownloaded(BitmapFactory.decodeStream(inputStream));
						}
					}catch(Exception e){
						if(response != null && response.getUrl() != null){
							Functions.log(getClass().getSimpleName(), "Error downloading url: " + response.getUrl());
						}
						
						Functions.log(e);
					}finally{
						ManagerImagesUpdateGridView.getInstance().didComplete(this);
					}
				}
			};
			
			if(isGridview()){
				ManagerImagesUpdateGridView.getInstance().push(runnable);
			}else{
				new Thread(runnable).start();
			}
		}
	}
	
	/**
	 * Guarda la respuesta obtenida del servidor en un fichero en la memoria del telefono
	 * @param response
	 * @return Devuelve el fichero del archivo obtenido
	 */
	public File saveResponse(ConnectionResponse response){
		File file = fileCache.getFile(String.valueOf(response.getUrl().hashCode()));
		
		if(file == null || !file.exists()){
			HttpResponse httpResponse = (HttpResponse) response.getData();
	        final HttpEntity entity = httpResponse.getEntity();
	        FlushedInputStream inputStream = null;
	        if (entity != null) {  
	            try {
					Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
					if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					    inputStream = new FlushedInputStream(new GZIPInputStream(httpResponse.getEntity().getContent()));
					}else{
						inputStream = new FlushedInputStream(entity.getContent());
					}
					
//	            	inputStream = new FlushedInputStream(entity.getContent());

	                OutputStream os = new FileOutputStream(file);
	                StreamUtils.saveStream(inputStream, os);
	                os.close();
	            } catch (IllegalStateException e) {
	            	Functions.log(e);
				} catch (IOException e) {
					Functions.log(e);
				} catch (Exception e){
					Functions.log(e);
				} finally {
					try{
		                if (inputStream != null) {
		                    inputStream.close();
		                }
		                
		                entity.consumeContent();	
					}catch(Exception e){
						Functions.log(e);
					}
	            }
	        }
		}
        
        return file;
	}
	
	/**
	 * Crea un bitmap a partir de una ruta de un fichero
	 * @param file
	 * @return
	 */
	public Bitmap decodeFile(File file){
		int rotate = 0;
		
		try{
	        ExifInterface exif = new ExifInterface(file.getAbsolutePath());
	        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	        
	        switch (orientation) {
	        case ExifInterface.ORIENTATION_ROTATE_270:
	            rotate = 270;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
	            rotate = 180;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_90:
	            rotate = 90;
	            break;
	        }
		}catch(Exception e){}
        
		if(rotate != 0){
	        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), getBitmapOptions(file.getPath()));
	        
			Matrix matrix = new Matrix();
			matrix.postRotate(rotate);
			Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, 
					bitmap.getWidth(), bitmap.getHeight(), 
			                              matrix, true);
			
			return rotated;	
		}
		
		return BitmapFactory.decodeFile(file.getPath(), getBitmapOptions(file.getPath()));
	}
	
	/**
	 * Actualiza la UI de la vista con un bitmap dado en caso de que sea diferente de la que tiene asignada
	 * @param bitmap
	 */
	public void setImageDownloaded(final Bitmap bitmap){
		setBackgroundType(BackgroundType.DOWNLOADED);
		
		final Bitmap bitmapFinal;
		if(rounded){
			bitmapFinal = getCroppedBitmap(bitmap);
		}else{
			bitmapFinal = bitmap;
		}
				
		handlerUI.post(new Runnable() {
			public void run() {			
				if(bitmapFinal != null && bitmapFinal.getWidth() != -1 && bitmapFinal.getHeight() != -1){
					Drawable drawable = new BitmapDrawable(getResources(), bitmapFinal);
					if(getDrawable() == null || getDrawable() != drawable){
						setDrawable(drawable);
						setImageDrawable(drawable);
						
						if(dynamic){
							double newWidth = drawable.getIntrinsicWidth();
							double newHeight = drawable.getIntrinsicHeight();
							
							double scaleFactor;
							
							if(newWidth > getDefaultWidth() || newHeight > getDefaultHeight()){
								if(newWidth - getDefaultWidth() > newHeight - getDefaultHeight()){
									scaleFactor = getDefaultWidth() * 100 / newWidth;
								}else{
									scaleFactor = getDefaultHeight() * 100 / newHeight;
								}
								
								int finalWidth = (int) (newWidth * (scaleFactor / 100));
								int finalHeight = (int) (newHeight * (scaleFactor / 100));

								LayoutParams params = (LayoutParams) getLayoutParams();
								params.width = finalWidth;
								params.height = finalHeight;

								setLayoutParams(params);
							}
						}
					}	
				}else{
					setDrawable(drawableDefault);
					setImageDrawable(drawableDefault);
				}
			}
		});	
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
//		url = null;
	}
	
	public Bitmap getCroppedBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
			bitmap.getHeight(), Config.ARGB_8888);

			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			paint.setAntiAlias(true);
			paint.setColor(color);
			canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
			bitmap.getWidth() / 2, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
			
			return output;
		} else {
			return bitmap;
		}
	}

	/**
	 * 
	 * @author dmiguel
	 *
	 * Esta clase es la que realiza las conexiones para la descarga de imagenes
	 */
	public class ImageConnection extends DefaultConnection{	
		public ImageConnection(Context context, ConnectionListener listener, String url){
			setUrl(url);
			setConnectionHandler(new ConnectionHandler(this, listener));
			setConnection(new HttpConnection<IModel>(
					context, 
					HttpConnection.GET, 
					getUrl(), 
					getConnectionHandler()));
			
			getConnection().setResponse(true);
			getConnection().setImageConnection(true);
		}
	}
	
	public interface OnBackgroundChangedListener {
		public abstract void onBackgroundChange(View view, boolean empty);
	}
	
	/**
	 * 
	 * @author dmiguel
	 *
	 * Esta clase es una cola de runnables que insertan el drawable en el imageview como fondo
	 */
	static class ManagerImagesUpdateGridView {
	     
	     public static final int MAX_CONNECTIONS = 1;
	 
	     private ArrayList<Runnable> active = new ArrayList<Runnable>();
	     private ArrayList<Runnable> queue = new ArrayList<Runnable>();
	 
	     private static ManagerImagesUpdateGridView instance;
	 
	     /**
	      * Devuelve una instancia de la clase, es una clase sigleton por lo que devolvera siempre la misma una vez creada por primera vez
	      * @return
	      */
	     public static ManagerImagesUpdateGridView getInstance() {
	          if (instance == null){
	        	  instance = new ManagerImagesUpdateGridView();
	          }
	               
	          return instance;
	     }
	 
	     /**
	      * Introduce un runnable en la cola
	      * @param runnable
	      */
	     public void push(Runnable runnable) {
	          queue.add(runnable);
	          if (active.size() < MAX_CONNECTIONS){
	        	  startNext();
	          }       
	     }
	     
	     /**
	      * Introduce un runnable en la cola en la posicion indicada o en la primera si no hay tantas posiciones
	      * @param runnable
	      * @param priority
	      */
	     public void push(Runnable runnable, int priority) {
	    	 if(priority < queue.size()){
	    		 queue.add(priority, runnable);
	    	 }else{
	    		 queue.add(0, runnable);
	    	 }
	    	 
	    	 if (active.size() < MAX_CONNECTIONS){
	    		 startNext();
	    	 }       
	     }
	 
	     /**
	      * Inicia el siguiente hilo si hay alguno en cola cuando ha terminado uno de los activos
	      */
	     private void startNext() {
	    	 try{
	             if (!queue.isEmpty()) {
	                 Runnable next = (Runnable) queue.get(0);
	                 queue.remove(0);
                     active.add(next);
                     
                     Thread thread = new Thread(next);
                     thread.start(); 
	            }
	    	 }catch(Exception e){
	    		 Functions.log(e);
	    	 }
	     }
	 
	     /**
	      * Elimina el runnable de la cola de activos e inicia el siguiente si es que hay alguno pendiente
	      * @param runnable
	      */
	     public void didComplete(Runnable runnable) {
	    	 try{
	             active.remove(runnable);
	             startNext();
	    	 }catch(Exception e){
	    		 Functions.log(e);
	    	 }
	     }
	}
	
	/**
	 * Este listener nos ayuda a interceptar el scroll del padre que contiene esta vista, para actualizar las imagenes
	 * solamente cuando se ha parado de hacer scroll
	 */
	private OnScrollListener parentScrollListener = new OnScrollListener() {

		/**
		 * Este metodo intercepta cuando se ha parado de hacer scroll y activa la descarga y actualizacion de la UI
		 * de todas las vistas que estan visibles
		 */
		@Override
		public void onScrollStateChanged(AbsListView absListView, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				
				break;

			case OnScrollListener.SCROLL_STATE_IDLE:	
				for(int i=0; i<absListView.getChildCount(); i++){
					View parent = absListView.getChildAt(i);
					
					if(parent instanceof ViewGroup){
						ViewGroup viewGroup = (ViewGroup)parent;
						for(int j=0; j<viewGroup.getChildCount(); j++){
							if(viewGroup.getChildAt(j) instanceof ManagedImageView){
								startDownload((ManagedImageView) viewGroup.getChildAt(j));
							}
						}
					}else if(parent instanceof ManagedImageView){
						startDownload((ManagedImageView) parent);
					}
				}
				
				break;
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
		
		/**
		 * Ejecuta la descarga de la imagen asignada a esta vista
		 * @param managedImageview
		 */
		public void startDownload(ManagedImageView managedImageview){
			if(managedImageview.getBackgroundType() != BackgroundType.DOWNLOADED){
				managedImageview.download(managedImageview.getUrl(), false);	
			}
		}
	};
	
	/**
	 * 
	 * @author dmiguel
	 *
	 * Este handler nos ayuda siempre que queremos actualizar la UI
	 */
	static class HandlerUI extends Handler{
		private final WeakReference<Context> mContext;
		
		HandlerUI(Context context) {
			mContext = new WeakReference<Context>(context);
	    }

		@Override
		public void handleMessage(Message message){
			Context context = mContext.get();
			if (context != null) {

			}
		}
	}
}
