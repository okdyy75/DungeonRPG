package okdya.dungeonrpg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class RPGView extends SurfaceView implements SurfaceHolder.Callback,Runnable {

	public static final int
	DISP_WIDTH = 720,
	DISP_HEIGHT = 1280;

	private float
	ratio_width,
	ratio_height;

	//システム定数
	private final static int//シーン(1)
	S_START = 1,//開始
	S_MAP   = 2,//マップ
	S_CLEAR = 3,//クリア
	S_GAMEOVER = 4;//ゲームオーバー

	private final static int
	KEY_NONE  = -1,
	KEY_LEFT  = 0,
	KEY_RIGHT = 1,
	KEY_UP    = 2,
	KEY_DOWN  =3;

	//システム
	private int[][] MAP;
	private int idx;
	private int      init = S_START;//初期化(1)
	private int      scene;       //シーン(1)
	private int      key;         //キー
	private Graphics g;           //グラフィック
	private Bitmap[] mapImg = new Bitmap[7];//ビットマップ
	private SurfaceHolder holder;//サーフェイスホルダー
	private Thread        thread;//スレッド
	private String str;

	private final static int
	MAXHP = 30,
	MAXFLLOR = 5;

	//自分
	private int meF = 1;
	private int meHP;

	private Bitmap meImg[] = new Bitmap[4];
	private int meX;
	private int meY;

	//敵パラメータ(4)
	private int enX;
	private int enY;
	private Bitmap enemyImg[] = new Bitmap[4];
	private int enidx;

	//―――――――――――――――――――――――――――――――――――――
	//コンストラクタ
	//―――――――――――――――――――――――――――――――――――――
	//setContentView(new RPGView(RPG.this))として表示させるとき
	public RPGView(Context context) {
		super(context);

		holder = getHolder();
		holder.addCallback(this);
		holder.setFixedSize(getWidth(),getHeight());
		g = new Graphics(holder);
	}

	//カスタムレイアウトを通して使用する時に呼ばれるので今回は必要
	public RPGView(Context context, AttributeSet attrs) {
		super(context, attrs);

		holder = getHolder();
		holder.addCallback(this);
		holder.setFixedSize(getWidth(),getHeight());
		g = new Graphics(holder);

	}

	//―――――――――――――――――――――――――――――――――――――
	//コンストラクタが呼ばれた際に、SurfaceViewのCallback関数を登録している
	//―――――――――――――――――――――――――――――――――――――
	//SurfaceViewの生成
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Resources r = getResources();
		mapImg[0]=BitmapFactory.decodeResource(r,R.drawable.rpg0);
		mapImg[1]=BitmapFactory.decodeResource(r,R.drawable.rpg1);
		mapImg[2]=BitmapFactory.decodeResource(r,R.drawable.rpg2);
		mapImg[3]=BitmapFactory.decodeResource(r,R.drawable.rpg3);

		enemyImg[0] = BitmapFactory.decodeResource(r, R.drawable.goastleft);
		enemyImg[1] = BitmapFactory.decodeResource(r, R.drawable.goastright);
		enemyImg[2] = BitmapFactory.decodeResource(r, R.drawable.goastback);
		enemyImg[3] = BitmapFactory.decodeResource(r, R.drawable.goastflont);

		meImg[0] = BitmapFactory.decodeResource(r, R.drawable.catleft);
		meImg[1] = BitmapFactory.decodeResource(r, R.drawable.catright);
		meImg[2] = BitmapFactory.decodeResource(r, R.drawable.catback);
		meImg[3] = BitmapFactory.decodeResource(r, R.drawable.catflont);
		//Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, true);

		thread=new Thread(this);
		thread.start();
	}
	
	//SurfaceViewのサイズ変更
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	//SurfaceViewの終了
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread=null;
	}


	//―――――――――――――――――――――――――――――――――――――
	//タッチイベントの取得
	//―――――――――――――――――――――――――――――――――――――
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int touchX=(int)(event.getX());
		int touchY=(int)(event.getY());
		int touchAction=event.getAction();

		if (touchAction==MotionEvent.ACTION_DOWN ||
				touchAction== MotionEvent.ACTION_MOVE ) {

			if (scene==S_MAP) {

				if (Math.abs(touchX-getWidth()/2)>Math.abs(touchY-getHeight()/2)) {
					key=(touchX-getWidth()/2<0)?KEY_LEFT:KEY_RIGHT;
				} else {
					key=(touchY-getHeight()/2<0)?KEY_UP:KEY_DOWN;
				}
			}

			if(scene == S_GAMEOVER) key = 1;

		}
		return true;
	}


	//―――――――――――――――――――――――――――――――――――――
	//メインの描画処理
	//―――――――――――――――――――――――――――――――――――――
	public void run() {
		try{
			while(thread != null) {
				//初期表示
				if (init == S_START) {
						meX = 1;
						meY = 1;
						if (meF == 1) meHP = MAXHP;
						init = S_MAP;
						MAP = new Map().getMAP(meF);
						MAP = enemyBorn(MAP);
						ratio_width = ((float)getWidth() / (float)DISP_WIDTH);
						ratio_height = ((float)getHeight() / (float)DISP_HEIGHT);
						key =KEY_NONE;
				}

				scene = init;
				init = 0;

				//マップ
				if (scene==S_MAP) {
					//描画
					g.lock();
					g.setDisp(ratio_width, ratio_height);

					//移動
					boolean flag=false;
					if (key==KEY_UP) {
						if (MAP[meY-1][meX] !=3) {meY--;flag=true;}
					} else if (key==KEY_DOWN) {
						if (MAP[meY+1][meX]!=3) {meY++;flag=true;}
					} else if (key==KEY_LEFT) {
						if (MAP[meY][meX-1]!=3) {meX--;flag=true;}
					} else if (key==KEY_RIGHT) {
						if (MAP[meY][meX+1]!=3) {meX++;flag=true;}
					}

					//敵の動きの制御
					for (int j=-MAP.length; j<=MAP.length; j++) {
						for (int i=-MAP[0].length; i <=MAP[0].length; i++) {
							int idx=0;
							if (0<=meX+i && meX+i < MAP[0].length &&
									0<=meY+j && meY+j < MAP.length) {
								idx=MAP[meY+j][meX+i];
							}
							if( flag == true && idx == 4) {
								enY = j + meY;
								enX = i + meX;
								if(meX < enX) enX--;
								if(meX > enX) enX++;
								if(meY < enY) enY--;
								if(meY > enY) enY++;
								if(Math.abs(meX - enX) > Math.abs(meY - enY)) {
									if(meX < enX) enidx = 0;
									if(meX > enX) enidx = 1;
									} else {
										if(meY < enY) enidx = 2;
										if(meY > enY) enidx = 3;
									}
								if(MAP[enY][enX] == 0) {
									MAP[enY][enX] = 4;
									MAP[meY+j][meX+i] = 0;
									} else {
										enX = meX+i;
										enY = meY+j;
									}
								if(Math.abs(meX - enX) > 7 || Math.abs(meY - enY) > 7) {
									MAP = enemyBorn(MAP);
									MAP[enY][enX] = 0;
								}
								flag = false;
							}
						}
					}

					//マップ上の表示
					for (int j=-MAP.length; j<=MAP.length; j++) {
						for (int i=-MAP[0].length; i <=MAP[0].length; i++) {
							int idx=3;
							if (0<=meX+i && meX+i < MAP[0].length &&
									0<=meY+j && meY+j < MAP.length) {
								idx=MAP[meY+j][meX+i];
							}
							g.drawBitmap(mapImg[0],DISP_WIDTH/2-40+80*i,DISP_HEIGHT/2-40+80*j);
							if (idx == 4 ) {
								g.drawBitmap(enemyImg[enidx],DISP_WIDTH/2-40+80*i,DISP_HEIGHT/2-40+80*j);
							} else {
								g.drawBitmap(mapImg[idx],DISP_WIDTH/2-40+80*i,DISP_HEIGHT/2-40+80*j);
							}
						}
					}


					if(key != KEY_NONE) idx = key;
					g.drawBitmap(meImg[idx], -40 + DISP_WIDTH / 2, -40 + DISP_HEIGHT / 2);
					drawStatus();


					//敵と自分が当たったら
					if(meX == enX && meY == enY) {

						//自分が階段を降りたら無効
						if(MAP[meY][meX] != 2) {
							g.drawBitmap(enemyImg[enidx], -40 + DISP_WIDTH / 2, -40 + DISP_HEIGHT / 2);
							meHP -= 5;

							//敵の出現をリセット
							MAP[enY][enX] = 0;
							MAP = enemyBorn(MAP);
							enX = 0;enY = 0;
						}

					}

					g.unlock();
					init = S_MAP;

					//各シーンへ移行
					if(MAP[meY][meX] == 2 && init != S_START) {
						meF++;
						init = S_START;
					}

					if(meHP == 0) {
						init = S_GAMEOVER;
						meF = 1;
					}

					//
					if(meF == 6) {
						init = S_CLEAR;
						meF = 1;
					}

				}

				if (scene == S_GAMEOVER ) {
					System.out.println("S_GAMEOVER");
					g.lock();
					g.setDisp(ratio_width, ratio_height);

					g.setColor(Color.BLACK);
					g.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
					g.setColor(Color.RED);
					g.setFontSize(DISP_WIDTH / 7);
					g.drawString("GAMEOVER", (DISP_WIDTH - g.stringWidth("GAMEOVER")) / 2, DISP_HEIGHT / 2);

					g.unlock();
					waitSelect();
					System.out.println("GAMEOVER END");
					init = S_START;

				}


				if(scene == S_CLEAR ) {
					//描画
					g.lock();
					g.setDisp(ratio_width, ratio_height);
					g.setColor(Color.BLACK);
					g.fillRect(0, 0, DISP_WIDTH, DISP_HEIGHT);
					g.setColor(Color.BLUE);
					g.setFontSize(100);
					str = "CLEAR！";
					g.drawString(str, (DISP_WIDTH - g.stringWidth(str)) / 2, DISP_HEIGHT / 3);
					g.setColor(Color.WHITE);
					g.roundRect(100, DISP_HEIGHT / 2, -100 + DISP_WIDTH, -200 + DISP_HEIGHT);
					g.setColor(Color.BLACK);
					g.setFontSize(40);
					str = "Thank　you　for　playing!";
					g.drawString(str, (DISP_WIDTH - g.stringWidth(str)) / 2, 200 + DISP_HEIGHT / 2);
					g.setFontSize(30);
					str = "To　be　continued...";
					g.drawString(str, (DISP_WIDTH - g.stringWidth(str)) / 3, 400 + DISP_HEIGHT / 2);
					g.drawBitmap(meImg[3], -40 + DISP_WIDTH / 2, -100 + DISP_HEIGHT / 2);
					g.unlock();
					waitSelect();
				}
				//スリープ
				key=KEY_NONE;
				Thread.sleep(100);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public int[][] enemyBorn(int[][] map) {
		MAP = map;
		boolean flag = true;
		while(flag) {

			int randX = rand(0, MAP[0].length);
			int randY = rand(0, MAP.length);
			if (Math.abs(meX - randX) <= 7 && Math.abs(meY - randY) <= 7 &&
					Math.abs(meX - randX) >= 2 && Math.abs(meY - randY) >= 2 && MAP[randY][randX] == 0 ) {
				MAP[randY][randX] = 4;
				flag = false;
			}
		}
		return MAP;
	}


	private void drawStatus() {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, DISP_WIDTH, 100);
		g.setColor(Color.GREEN);
		if(meHP <= 10) g.setColor(Color.RED);
		g.setFontSize(30);
		g.drawString("　ねこ　HP"+meHP+"/"+MAXHP+"　"+meF+"F/"+MAXFLLOR+"F", 0,70);
	}

	private void waitSelect() {
		try {
		key=KEY_NONE;
		while (key == KEY_NONE) Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	private static Random rand=new Random();
	public static int rand(int min,int max) {
		return ( (rand.nextInt()>>>1) % max)+ min;
	}




}