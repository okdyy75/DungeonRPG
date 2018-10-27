package okdya.dungeonrpg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;

public class Graphics {
	private SurfaceHolder holder;
	private Paint         paint;
	private Canvas        canvas;


	public void setDisp(float ratio_width, float ratio_height) {

		canvas.scale(ratio_width,ratio_height);

	}


	public Graphics(SurfaceHolder holder) {
		this.holder=holder;
		paint=new Paint();
		paint.setAntiAlias(true);
	}

	public void lock() {
		canvas=holder.lockCanvas();
	}

	public void unlock() {
		holder.unlockCanvasAndPost(canvas);
	}

	public void setColor(int color) {
		paint.setColor(color);
	}

	public void setFontSize(int fontSize) {
		paint.setTextSize(fontSize);
	}

	public int stringWidth(String string) {
		return (int)paint.measureText(string);
	}


	public void fillRect(int x,int y,int xw,int yh) {
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(new Rect(x, y, xw, yh),paint);
	}

	public void roundRect(int x, int y, int xw, int yh) {
		paint.setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
		canvas.drawRoundRect(new RectF(x, y, xw, yh), 20, 20, paint);
	}

	public void drawBitmap(Bitmap bitmap,int x,int y) {
		canvas.drawBitmap(bitmap, x, y,paint);
	}

	public void drawString(String string,int x,int y) {
		canvas.drawText(string, x, y,paint);
	}



}
