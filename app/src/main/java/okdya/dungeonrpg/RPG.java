package okdya.dungeonrpg;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class RPG extends Activity {

	RelativeLayout relayout_TITLE;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//タイトルバー非表示
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); //ステータスバーを消す
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);	//ステータスバーを隠す
		setContentView(R.layout.titlelayout);

		relayout_TITLE = (RelativeLayout)findViewById(R.id.relayout_TITLE);
		Button btn_STARTGAME = (Button) relayout_TITLE.findViewById(R.id.btn_STARTGAME);
		btn_STARTGAME.setOnClickListener(new Click());

	}

	public class Click implements  OnClickListener {

		public void onClick(View v) {

			switch(v.getId()){
			case R.id.btn_STARTGAME:
				System.out.println("GAME Start!!!");
				setContentView(R.layout.gamelayout);
				FrameLayout flayout_GAME = (FrameLayout)findViewById(R.id.flayout_GAME);
				Button btn_BACKTITLE = (Button) flayout_GAME.findViewById(R.id.btn_BACKTITLE);
				btn_BACKTITLE.setOnClickListener(new Click());

				break;

			case R.id.btn_BACKTITLE:
				setContentView(relayout_TITLE);

				break;
			case R.id.btn_APPLINK:
				Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=example.android.dungeonrpg"));
				startActivity(intent);
				break;

			}

		}
	}

}