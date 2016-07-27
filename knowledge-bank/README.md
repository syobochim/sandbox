
## マネージドビーンのスコープ一覧

|スコープ|範囲|
|:-----------|:------------|
|@RequestScoped|アプリケーションサーバにHTTPの要求がきてから処理が終了するまで 
|@SessionScoped|ユーザがログインしてからログアウトするまで|
|@ApplicationScoped|サーバが起動してから停止するまで|
|@ConvasationScoped|CDIが提供するConversationクラスのbeginメソッドを呼び出してからendメソッドを呼び出すまで|
|@ViewScoped|同一画面を表示している間|
|@FlowScoped|FacesFlowで定義した画面遷移定義内で遷移する間|


