# PhysicsTimer
各クラスの機能を説明します。`MainActivity`と`BackgroundActivty`はKotlinで、それ以外はJavaで書かれています。  
## 概略
### 物理演算ライブラリ
物理演算ライブラリはJBox2Dを使用しています。JBox2DはC/C++用ライブラリBox2DのJava移植版です。最近はメンテナンスされておらず、マニュアルすらありません。仕方ないのでC/C++用Box2Dのマニュアルを参照しています。オリジナルは英語だったので全訳しました(1週間くらいかかったので一読の価値ありです)。マニュアルは`PhysicsTimer\app\libs\Docs\`に入っています。  
JBox2D内での演算に使用する長さ単位はメートルです。したがって物理演算はメートル座標で行い、その結果をピクセル座標に変換してディスプレイ表示する必要があります。このアプリではディスプレイ高さを`PhysicsTimer.DEFAULT_DISPLAY_HEIGHT_IN_METER = 8`メートルとしてJBox2Dのメートル座標と関連付けています。メートル座標とピクセル座標の橋渡しをしているのは`Scale`クラスです。このクラスにはディスプレイのサイズ(縦x横)がメートル、ピクセルで保持され、メートル->ピクセル、ピクセル->メートルの任意の値の変換も行います。

### タイル描画部分の概略
画面上を動き回る青い四角を「タイル」と呼びます。タイルで残りの「分：秒」が表示されている部分を「タイマー」と呼んでいます。
#### クラス概略
タイルの描画に必要なクラスを説明します。タイルは`SurfaceView`上で直接描画していますが、タイルの位置、並び方、ジョイントの生成、「分：秒」などの時間情報の保持のため、下記のような仮想的な階層のクラス構造をしています。  

|階層|クラス名|概要|
|:---|:---|:---|
|5|Tile|タイルの画像と、タイルを拘束しておくTile側のジョイントアンカー位置を保持するクラス|
|4|TileBase|タイルを拘束しておくMainSurfaceView側のジョイントアンカー位置を保持しているクラス|
|3|DialPanel|「分」「秒」もしくは「：(コロン)」のいづれかの情報を保持しているクラス|
|2|Dial|タイマーのデザイン、「分」「：(コロン)」「秒」にあたるDialPanelを保持しているクラス|
|1|MainSurfaceView|Tileを描画するためのクラス|

#### MainSurfaceViewクラス


タイマーのデザイン・残り分と秒などは`Dial`クラスが管理しています(Dialは文字盤という意味で、「時計の文字盤」のつもりで名前つけました。名称の変更検討中)。  
`Dial`クラスは`DialPanel`クラスのインスタンスを保持しています。`DialPanel`クラスは「分」もしくは「秒」にあたる2桁の数字、もしくはコロン「：」を管理しています。`DialPanel`のコンストラクタにフォーマットを渡していますが、これはこの`DialPanel`インスタンスが「分」を表すものなら`DialPanel.MINUTE`を、コロンならば`DialPanel.COLOGNE`といった風に指定します。  
`DialPanel`はまた、両側のスペースの大きさも保持しています。スペースサイズは`Dial.setTimerSizeScale()`で設定され、これは`PhysicsTimer.init()`で呼ばれています。`Dial.setTimerSizeScale()`ではスペースサイズの算出に「セクション」という概念を使っていますが、これはあまりいい考えではないです。  
`DialPanel`は`TileBase`クラスのインスタンスを保持しています。`DialPanel`は`DialPanel.mFormat`および`DialPanel.mFont`に従い`TileBase`を並べます。1つの`TileBase`は1つの`Tile`と

## MainActivity
このアプリ唯一のActivityクラスです。ボタン類の宣言と初期化を行う`BackgroundActivity`クラスを継承しています。  
`onCreate`メソッドで必要な設定を行い、`onTouch`メソッドでNumpad(数字キー)からの入力が確定した時点でタイマーのカウントダウンが始まります。

### Numpad(数字キー)の動き
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
  - `isDragged`でキー間のスライド距離が`DRAGABLE_DISTANCE`以上なら「スライドした」と判定する

## BackgroundActivity
`Activity`クラスを継承しています。ボタンなどの宣言、定義、初期化がだらだらと長く目障りだったのでこのファイルに分割しました。
ボタンのサイズは端末のディスプレイサイズに合わせて決めています。レイアウトxmlファイルでボタンのレイアウトを行うと、コード上で
動的にサイズを変更してもうまく動かないので、このクラスでゴリゴリとインスタンス化を行なっています。
### initButtonWidth
Numpadキー1つのサイズを、ディスプレイ幅から算出しています。
### initControlButtons
- タイマー表示状態からNumpadに戻るボタン(左上に表示される「0」のボタン(仮))
- 設定ボタン(未実装)
### initNumpad
数字キーボタン

## PhysicsTimer
### init()
タイマーに必要な各種設定を行い、`TimeChanged`クラスを`bindService`として実行します。
### setDialPosition
タイマー表示部分をディスプレイ幅に対して中央に、高さは
