package br.grupointegrado.Spaceinvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;



/**
 * Created by Thomas on 03/08/2015.
 */
public class TelaJogo extends  TelaBase {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
    private BitmapFont fonte;
    private Label lbPontuacao;
    private Image jogador;
    private Texture textureJogadorDireita;
    private Texture textureJogadorEsquerda;
    private Texture textureJogador;
    private boolean indoDireita;
    private boolean indoEsquerda;
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
        initFonte();
        initInformacoes();
        initJogador();

    }

    /**
     * ss
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

    private void initInformacoes(){
            Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbPontuacao = new Label("0 pntos",lbEstilo);

        palco.addActor(lbPontuacao);
    }
        private void initFonte (){
        fonte = new BitmapFont();
    }



    /**
     * Chamado a todo quadro de atualização do jogo (FPS)
     * @param delta tempo entre uma quadro e outro (em Segundos)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbPontuacao.setPosition(10, camera.viewportHeight - 20);

        capturateclas();
        atualizarJogador(delta);

        palco.act(delta);
        palco.draw();
    }

    /**
     * atualiza a posição do jogador
     * @param delta
     */
    private void atualizarJogador(float delta) {
        float velocidade = 200; // velocidade de movimento do jogador

        if (indoDireita) {
            if (jogador.getX() < camera.viewportWidth - jogador.getWidth()) {
                float x = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }
        }
        if (indoEsquerda){
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

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            indoEsquerda = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            indoDireita = true;
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
        fonte.dispose();
        textureJogador.dispose();
        textureJogadorDireita.dispose();
        textureJogadorEsquerda.dispose();
    }
}
