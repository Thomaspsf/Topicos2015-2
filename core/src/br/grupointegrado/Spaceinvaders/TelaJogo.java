package br.grupointegrado.Spaceinvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;



/**
 * Created by Thomas on 03/08/2015.
 */
public class TelaJogo extends  TelaBase {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
    private Stage palcoInformacoes;
    private BitmapFont fonte;
    private Label lbPontuacao;
    private Label lbGameOver;
    private Image jogador;
    private Texture textureJogadorDireita;
    private Texture textureJogadorEsquerda;
    private Texture textureJogador;
    private boolean indoDireita;
    private boolean indoEsquerda;
    private boolean atirando;
    private Array<Image> tiros = new Array<Image>();
    private Texture texturatiro;
    private Texture texturameteoro1;
    private Texture texturameteoro2;
    private Array<Image> meteoro1 = new Array<Image>();
    private Array<Image> meteoro2 = new Array<Image>();
    private Array<Texture> texturasExplosao = new Array<Texture>();
    private Array<Explosao> explosoes = new Array<Explosao>();

    private Sound somTiro;
    private Sound somExplosao;
    private Sound somGameOver;
    private Music musicaFundo;

    /**
     * contrutor padrão da tela de jogo
     * @param game Referencia para a classe principal
     */
    public TelaJogo(MainGame game) {
        super(game);
    }

    /**
     * chamado quando a tela é exibida
     */
    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight));
        palcoInformacoes = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight));
        initTexturas();
        initFonte();
        initInformacoes();
        initJogador();
        initSons();

    }

    private void initSons() {
        somTiro = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        somExplosao = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        somGameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicaFundo.setLooping(true);
    }

    private void initTexturas() {

        texturatiro = new Texture("sprites/shot.png");
        texturameteoro1 = new Texture("sprites/enemie-1.png");
        texturameteoro2 = new Texture("sprites/enemie-2.png");

        for (int i = 1; i <= 17; i++){
            Texture text = new Texture("sprites/explosion-" + i + ".png");
            texturasExplosao.add(text);
        }

    }

    /**
     * Intancia os objetos do jogador e adiciona no palco
     */
    private void initJogador() {
        textureJogador = new Texture("sprites/player.png");
        textureJogadorDireita = new Texture("sprites/player-right.png");
        textureJogadorEsquerda = new Texture("sprites/player-left.png");

        jogador = new Image(textureJogador);
        float x = camera.viewportWidth /2 -jogador.getWidth() /2;
        float y = 15;
        jogador.setPosition(x, y);
        palco.addActor(jogador);
    }

    /**
     * Instancia as informaçoes escritas na tela
     */
    private void initInformacoes(){
            Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbPontuacao = new Label("0 pntos",lbEstilo);
        palco.addActor(lbPontuacao);

        lbGameOver = new Label("Game Over", lbEstilo);
        lbGameOver.setVisible(false);
        palcoInformacoes.addActor(lbGameOver);
    }

    /**
     * instancia a fonte
     */
        private void initFonte (){
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
            param.color = Color.WHITE;
            param.size = 24;
            param.shadowOffsetX =2;
            param.shadowOffsetY = 2;
            param.shadowColor = Color.BLUE;

            fonte = generator.generateFont(param);

            generator.dispose();
    }



    /**
     * Chamado a todo quadro de atualização do jogo (FPS)
     * @param delta tempo entre uma quadro e outro (em Segundos)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbPontuacao.setPosition(10, camera.viewportHeight - lbPontuacao.getPrefHeight() - 20);
        lbPontuacao.setText(pontuacao + " pontos");

        lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getWidth() / 2, camera.viewportHeight / 2);
        lbGameOver.setVisible(gameOver == true);

        atualizarExplosoes(delta);
        if (gameOver == false) {// se o meteoro tiver colisao com o jogador o jogo para
            if (!musicaFundo.isPlaying())
                musicaFundo.play();
            capturateclas();
            atualizarJogador(delta);
            atualizarTiros(delta);
            atualizarMeteoros(delta);
            detectarColisoes(meteoro1, 5);
            detectarColisoes(meteoro2, 10);
        }else{
            if (musicaFundo.isPlaying())
                musicaFundo.stop();
        }


        // atualiza a situação do palco na tela
        palco.act(delta);
        //desenha o palco na tela
        palco.draw();

        palcoInformacoes.act(delta);
        palcoInformacoes.draw();
    }

    private void atualizarExplosoes(float delta) {
        for (Explosao explosao : explosoes){
            // verifica se a explosao ja chegoi ao fim
            if (explosao.getEstagio() >= 16){
                explosoes.removeValue(explosao, true); //remove a explosoes do array
                explosao.getAtor().remove();//remove o ator do palco

            }else {
                explosao.atualizar(delta);
            }
        }

    }

    private Rectangle recJogador = new Rectangle();
    private Rectangle recTiro = new Rectangle();
    private Rectangle recMetodo = new Rectangle();
    private int pontuacao = 0;
    private boolean gameOver = false;

    private void detectarColisoes(Array<Image> meteoros, int valePonto) {
        recJogador.set(jogador.getX(), jogador.getY(), jogador.getWidth(), jogador.getHeight());
        for (Image meteoro : meteoros){
            recMetodo.set(meteoro.getX(), meteoro.getY(), meteoro.getWidth(), meteoro.getHeight());
            for (Image tiro : tiros){
                recTiro.set(tiro.getX(), tiro.getY(), tiro.getWidth(), tiro.getHeight());
                if (recMetodo.overlaps(recTiro)){
                    //aqui ocorre uma colisão do tiro com o meteoro1
                    pontuacao += valePonto; // incrementa a pontuação
                    tiro.remove();//remove do palco
                    tiros.removeValue(tiro, true);//remove da lista
                    meteoro.remove();// remove do palco
                    meteoros.removeValue(meteoro, true);//remove da lista
                    criarExplosao(meteoro.getX() + meteoro.getWidth() / 2, meteoro.getY() + meteoro.getHeight() / 2);
                }


            }
            //detecta colisao com o player
            if (recJogador.overlaps(recMetodo)){
                //ocorre colisao do jogador com meteoro
                gameOver = true;
                somGameOver.play();


            }
        }
    }

    /**
     * Cria explosao na posição X y
     * @param x
     * @param y
     */

    private void criarExplosao(float x, float y) {
        Image ator = new Image(texturasExplosao.get(0));
        ator.setPosition(x - ator.getWidth() /2, y - ator.getHeight() /2  );
        palco.addActor(ator);

        Explosao explosao = new Explosao(ator, texturasExplosao);
        explosoes.add(explosao);
        somExplosao.play();


    }

    private void atualizarMeteoros(float delta) {
        int qtdMeteoros = meteoro1.size + meteoro2.size;//retorna a quantidade de meteoros criados

        if(qtdMeteoros < 5) {
            int tipo = MathUtils.random(1, 4);//retorna 1 ou 2 aleartoriamente

            if (tipo == 1) {
                //cria meteoro
                Image meteoro = new Image(texturameteoro1);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoro1.add(meteoro);
                palco.addActor(meteoro);

            } else if (tipo == 2){
                //cria meteoro
                Image meteoro = new Image(texturameteoro2);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoro2.add(meteoro);
                palco.addActor(meteoro);
            }
        }
        float velocidade1 = 100;
        for (Image meteoro : meteoro1){
            float x = meteoro.getX();
            float y = meteoro.getY() - velocidade1 * delta;
            meteoro.setPosition(x,y);//atualiza a posição do meteoro
            if (meteoro.getY() + meteoro.getHeight() < 0){
                meteoro.remove();//remove do palco
                meteoro1.removeValue(meteoro, true);//remove da lista
            }

        }
        float velocidade2 = 150;//250 pixwls por segundo
        for (Image meteoro : meteoro2) {
            float x = meteoro.getX();
            float y = meteoro.getY() - velocidade2 * delta;
            meteoro.setPosition(x, y);//atualiza a posição do meteoro
            if (meteoro.getY() + meteoro.getHeight() < 0) {
                meteoro.remove();//remove do palco
                meteoro2.removeValue(meteoro, true);//remove da lista
            }
        }
    }

    private final float min_intervalo_tiros = 0.4f;//minimo de tempo entre os tiros
    private  float intervaloTiros = 0;// tempo acumulado entre as tiros


    private void atualizarTiros(float delta) {
        intervaloTiros = intervaloTiros + delta;// acumula o tempo percorrido
        if (atirando){
            //verifica se o tempo minimo foi atingido
            if (intervaloTiros >= min_intervalo_tiros){
                Image tiro = new Image(texturatiro);
                float x = jogador.getX() + jogador.getWidth() /2 - tiro.getWidth() / 2;
                float y = jogador.getY() + jogador.getHeight();
                tiro.setPosition(x, y);
                tiros.add(tiro);
                palco.addActor(tiro);
                intervaloTiros = 0;
                somTiro.play();
            }

        }
        float velocidade = 200;//velocidade do movimento do tiro
        //percorre todos os tiros
        for (Image tiro : tiros){
            //movimanta o tiro em direção ao topo
            float x = tiro.getX();
            float y = tiro.getY() + velocidade * delta;
            tiro.setPosition(x, y);
            //remove os tiro que sairem da tela
            if (tiro.getY() > camera.viewportHeight){
                tiros.removeValue(tiro, true);//remove da lista
                tiro.remove();//remove do palco
            }
        }
    }

    /**
     * atualiza a posição do jogador
     * @param delta
     */
    private void atualizarJogador(float delta) {
        float velocidade = 200; // velocidade de movimento do jogador

        if (indoDireita) {
            //verifica se o jogador esta dentro da tela
            if (jogador.getX() < camera.viewportWidth - jogador.getWidth()) {
                float x = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }
        }
        if (indoEsquerda){
            //verifica se o jogador esta dentro da tela
            if (jogador.getX() > 0 ) {
                float x = jogador.getX() - velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }

        }

        if (indoDireita){
            //troar imagen direita
            jogador.setDrawable(new SpriteDrawable(new Sprite(textureJogadorDireita)));
        }else if (indoEsquerda) {
            //trocar imagen esquerda
            jogador.setDrawable(new SpriteDrawable(new Sprite(textureJogadorEsquerda)));
        }else{
            //trocar imagen centro
            jogador.setDrawable(new SpriteDrawable(new Sprite(textureJogador)));
        }
    }

    /**
     * Verifica se as tclas estão percionadas
     */
    private void capturateclas() {
        indoDireita = false;
        indoEsquerda = false;
        atirando = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            indoEsquerda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            indoDireita = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            atirando = true;
        }
    }

    /**
     * È chamado sempre que a uma alteração no tamanho da tela
     * @param width novo valor de largura da tela
     * @param height novo valor de altura da tela
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    /**
     * È chamado sempre que o jogo for minimizado
     */
    @Override
    public void pause() {

    }

    /**
     * È echamdo sempre que o jogo voltar paro o primeiro plano
     */
    @Override
    public void resume() {

    }

    /**
     * È chamado quando a tela for destruida
     */
    @Override
    public void dispose() {
        batch.dispose();
        palco.dispose();
        palcoInformacoes.dispose();
        fonte.dispose();
        textureJogador.dispose();
        textureJogadorDireita.dispose();
        textureJogadorEsquerda.dispose();
        texturatiro.dispose();
        texturameteoro1.dispose();
        texturameteoro2.dispose();
        for (Texture text : texturasExplosao){
            text.dispose();
        }
        somExplosao.dispose();
        somExplosao.dispose();
        somGameOver.dispose();
        musicaFundo.dispose();
    }
}
