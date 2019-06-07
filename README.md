PhysicsTimerの各クラスの機能を説明します。`MainActivity`と`BackgroundActivty`はKotlinで、それ以外はJavaで書かれています。  
# アクティビティ
## MainActivityクラス
`onCreate()`がこのアプリのエントリポイントです。タイルの描画、時間管理などタイマー機能ほぼすべては`onCreate()`内で生成される`PhysicsTimer`クラスのインスタンスが担っています。この`MainActivity`クラスでは、ボタンのタッチなどの処理、加速度センサーの処理を主にしています。
## BackgroundActivityクラス
`MainActivity`クラスのスーパークラスで、こいつが`Activity`クラスを継承しています。図にすると、  
`Activity`  
　↑  
`BackgroundActivity`  
　↑  
`MainActivity`  
となります。このクラスはボタンなどの生成とレイアウトを行っています。当初xmlレイアウトファイルでボタンの配置を行っていましたが、端末のディスプレイサイズに合わせてボタンサイズも変更しようとした場合うまく動作しなかったので、全てコードで書くことにしました。と言って大量のボタン生成コードが目障りだったため、クラスを分けた次第です(C#のpartial的なものがあればよいのですが)。  
`Activity`クラスのオーバーライドメソッドはここをスルーして`MainActivity`クラスで呼ばれます。
# 概略
## 物理演算ライブラリ
物理演算ライブラリはJBox2Dを使用しています。JBox2DはC/C++用ライブラリBox2DのJava移植版です。ライブラリ本体は  
`PhysicsTimer\app\libs\`  
にあります。
最近はメンテナンスされておらず、マニュアルすらありません。仕方ないのでC/C++用Box2Dのマニュアルを参照しています。オリジナルは英語だったので全訳しました(1週間くらいかかったので一読の価値ありです)。苦心のマニュアルは  
`PhysicsTimer\app\libs\Docs\`  
に入っています。  
JBox2D内での演算に使用する長さ単位はメートルです。したがって物理演算はメートル座標で行い、その結果をピクセル座標に変換してディスプレイ表示する必要があります。このアプリではディスプレイ高さを`PhysicsTimer.DEFAULT_DISPLAY_HEIGHT_IN_METER = 8`メートルとしてJBox2Dのメートル座標と関連付けています。メートル座標とピクセル座標の橋渡しをしているのは`Scale`クラスです。このクラスにはディスプレイのサイズ(縦x横)がメートル、ピクセルで保持され、メートル->ピクセル、ピクセル->メートルの任意の値の変換も行います。
## 座標系
### Canvas座標系
タイルの描画には`Canvas`クラスを使用します。`Canvas`のデフォルト座標原点はディスプレイ左上、x軸：右が正、y軸：下が正、単位はピクセルです。
### World座標系
JBox2D内での計算に使用するワールド座標のデフォルト原点は左下で、x軸：右が正、y軸：上が正、単位はメートルです。
### Canvas座標系からWorld座標系への変換
`Canvas`へのタイル描画の際、`Canvas`側の座標系を変換してWorld座標系と一致させています。変換している場所は`MainSurfaceView`クラスの内部クラス`DrawCanvas.draw()`内です。具体的な手順は`Matrix`クラスを使用して以下の通りです。
- setScale()でx軸を対称に反転
- postTraslate()でディスプレイ高さ分をオフセット
## タイル描画
画面上を動き回る青い四角を「タイル」と呼びます。タイルで残りの「分：秒」が表示されている部分を「タイマー」と呼んでいます。
### クラス概略
タイルの描画に必要なクラスを説明します。タイルは`SurfaceView`上で直接描画していますが、タイルの位置、並び方、ジョイントの生成、「分：秒」などの時間情報の保持のため、下記のような仮想的な階層構造をしています。  

|階層|クラス名|概要|
|:---|:---|:---|
|5|Tile|タイルの画像と、タイルを拘束しておくTile側のジョイントアンカー位置を保持するクラス|
|4|TileBase|タイルを拘束しておくMainSurfaceView側のジョイントアンカー位置を保持しているクラス|
|4|フォントクラス|タイルを配置するマトリクスを保持し、数字をどのように表示するか定義するクラス|
|3|DialPanel|「分」「秒」もしくは「：(コロン)」のいづれかの情報を保持しているクラス|
|2|Dial|タイマーのデザイン、「分」「：(コロン)」「秒」にあたるDialPanelを保持しているクラス|
|1|MainSurfaceView|Tileを描画するためのクラス|

### MainSurfaceViewクラス
`Runnable`を実装した内部クラス`DrawCanvas`をハンドラーに渡して一定レートで描画しています。ここから`mWorld.drawBodies(canvas)`が呼ばれ、呼び出し先で`World`が保持している全Bodyに対して`Tile.drawBody(canvas, body)`が呼ばれ、その呼び出し先で`Canvas.drawBitmap()`が呼ばれてタイルの描画に至ります(ややこしい)。
### Dialクラス
タイマーのデザイン・残り分と秒などは`Dial`クラスが管理しています(Dialは文字盤という意味で、「時計の文字盤」のつもりで名前つけました。名称の変更検討中)。`Dial`クラスは`DialPanel`クラスのインスタンスを保持しています。「分」「：(コロン)」「秒」の3つの`DialPanel`インスタンスが`mDialPanelList`に格納されます。
### DialPanelクラス
`DialPanel`クラスは「分」もしくは「秒」にあたる2桁の数字、もしくはコロン「：」を管理しています。`DialPanel`のコンストラクタにフォーマットを渡していますが、これはこの`DialPanel`インスタンスが「分」を表すものなら`DialPanel.MINUTE`を、コロンならば`DialPanel.COLOGNE`といった風に指定します。  
`DialPanel`はまた、両側のスペースの大きさも保持しています。スペースサイズは`Dial.setTimerSizeScale()`で設定され、これは`PhysicsTimer.init()`で呼ばれています。`Dial.setTimerSizeScale()`ではスペースサイズの算出に「セクション」という概念を使っています。  
`DialPanel`は`TileBase`クラスのインスタンスを保持しています。`DialPanel`は`DialPanel.mFormat`および`DialPanel.mFont`に従い`TileBase`を並べます。
### TileBaseクラス
`TileBase`クラスはタイルを`MainSurfaceView`上に拘束するためのジョイントアンカーの位置を保持しています。このジョイントアンカーの座標原点は`MainSurfaceView`原点と一致します。1つの`TileBase`と1つの`Tile`は1対となっており、`TileBase`インスタンスを任意の配列で並べ、タイルをジョイントで拘束することで数字を表現しています。どのような規則で`TileBase`を並べるかは、`FontBase`抽象クラスを継承したフォントクラス(具体的には`NormalB`クラス、`NormalRoundB`クラス)で定義されています。
### フォントクラス
数字を表現するのに、どのようにタイルを並べるのか定義するクラスです。継承関係は以下の通りです。  
`FontBase`  
　↑  
`FontBaseB`  
　↑  
 `NormalB`, `NormalRoundB`  
 `FontBaseB`と`FontBaseS`の違いはタイルを並べるサイズの違いで、`FontBaseB`が5x4、`FontBaseS`が10x9です。`FontBaseS`はでかすぎるため現在使用していません。コード上で使用するのは`NormalB`と `NormalRoundB`です。ほかにも`FontBaseB`を継承することでいろんなデザインのフォントを定義できるはずです。
### Tileクラス
タイルの画像と、タイル自身を拘束するためのジョイントアンカーの座標を保持しています。このジョイントアンカーの座標原点はタイルサイズの中心です。このジョイントアンカー座標と`TileBase`のジョイントアンカー座標を用いてジョイントを生成します。

## Numpad(数字キー)の動き
タイマーセットの仕方は次の通り。
- 1〜9分を入力したい場合は、1〜9のキーをタッチして離す
- 10分以上の入力は最初の数字をタッチ、そのままスライドして次の数字へ移動し離す
- 11分、22分などゾロ目の入力は、タッチした数字キー上で軽くスライド

実装されている`View.onTouchListener.onTouch`の中身は以下の通り。
- ACTION_DOWN
  - getSelectedButtonNumber()で1つ目のボタンを取得
  - 押された座標を`ActionDownPoint`に記録
- ACTION_UP
  - getSelectedButtonNumber()で2つ目のボタンを取得
  - getInputTime()で`IsDragged=false`なら1桁の入力、`true`で2桁の入力とみなす
- ACTION_MOVE
  - `isDragged`でActionDownPointからの距離が`DRAGABLE_DISTANCE`以上なら「スライドした」と判定する


## 時間管理
タイマースタートから1秒ごとに通知を発行し、ついで現在の残り「(時)分秒」を教えてくれる機構です(このアプリでは「時」は通知されません)。GoFパターンで言うところのObserverパターンを利用しています。Observer抽象クラスは`TimeChangedListener`インターフェイス、Observer具象クラスは`PhysicsTimer`、Subject抽象クラスは存在せず、Subject(具象)クラス`TimeChanged`で直接処理しています。
### TimeChangedListenerインターフェイス
`PhysicsTimer`クラスは`TimeChangedListener`インターフェイスを実装し、`onTimeChanged(int hour, int minute, int second)`メソッドをオーバーライドしています。この状態で`PhysicsTimer.StartTimer()`から`setOnTimeChangedListener(this)`呼ぶことで、1秒ごとに`onTimeChanged(int hour, int minute, int second)`がコールバックされ、残りの「時分秒」を取得できます。
### TimeChangedクラス
`setOnTimeChangedListener()`で`TimeChangedListener`インターフェイスを実装した`PhysicsTimer`インスタンスを渡しておきます。`startTimer()`が呼ばれると`Runnable.run()`され、`DELAY_TIME = 1000`ミリ秒ごとに`forword()`が呼ばれます。`Forword()`では呼ばれるごとに`mActualSeconds`をカウントダウンしていきます。`mActualSeconds`は残りの分秒を秒に計算しなおしたもので、残り1分30秒なら`mActualSeconds = 90`です。これが"<0"となれば`false`を返してタイマー終了です。`mMinute`と`mSecond`も同時にカウントダウンされていきますが、これはコールバック用です。


