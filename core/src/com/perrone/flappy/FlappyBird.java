package com.perrone.flappy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.w3c.dom.Text;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Random numeroRandomico;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private Circle passaroCirculo;
	private Rectangle canoTopoHibox;
	private Rectangle canoBaixoHitbox;
	/*private ShapeRenderer shape;*/

	private float larguraDispositivo;
	private float alturaDispositivo;
	private int estadoJogo=0; //jogo nao iniciado - 1 iniciado - 2 game over
	private int pontuacao=0;


	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private float deltaTime;
	private float alturaEntreCanosRandomica;
	private boolean marcouPonto;
	private float acelerar = 200;
	private final float VIRTUAL_WIDHT = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	//camera
	private OrthographicCamera camera;
	private Viewport viewport;


	@Override
	public void create () {

		batch = new SpriteBatch();
		numeroRandomico = new Random();
		passaroCirculo = new Circle();
		/*canoBaixoHitbox = new Rectangle();
		canoTopoHibox = new Rectangle();
		shape = new ShapeRenderer();*/
		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		gameOver = new Texture("game_over.png");
		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");

		larguraDispositivo = VIRTUAL_WIDHT;
		alturaDispositivo = VIRTUAL_HEIGHT;

		posicaoInicialVertical = alturaDispositivo / 2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 400;

		//camera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDHT/2, VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDHT, VIRTUAL_HEIGHT, camera);

	}

	@Override
	public void render () {

		camera.update();

		//limpar frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 4;
		if (variacao > 2) variacao = 0;


		if (estadoJogo == 0){
			if (Gdx.input.justTouched()){
				estadoJogo = 1;
			}
		}else {

			velocidadeQueda++;
			//velocidade de queda do pombo
			if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
				posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

				if (estadoJogo == 1){
					posicaoMovimentoCanoHorizontal -= deltaTime * acelerar;
					if (Gdx.input.justTouched()) {
					velocidadeQueda = -20;
				}
				//Verifica se o cano saiu da tela completamente
					if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
					marcouPonto = false;
					}

					//verifica pontuacao
					if (posicaoMovimentoCanoHorizontal < 120){
						if (!marcouPonto){
						pontuacao++;
						acelerar += 10;
						marcouPonto = true;
						}
					}
				}
			}else{//gameover
				if (Gdx.input.justTouched()){
					estadoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoInicialVertical = alturaDispositivo/2;
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
				}
			}
		}

		//config da camera
		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
		batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
		batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);
		batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
		fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

		if (estadoJogo == 2){
			batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2);
			mensagem.draw(batch, "Toque para reiniciar", larguraDispositivo/2 - 200, alturaDispositivo/2 - gameOver.getHeight()/2);
		}

		batch.end();

		passaroCirculo.set(120 + passaros[0].getWidth()/2, posicaoInicialVertical + passaros[0].getHeight()/2, passaros[0].getWidth()/2);
		canoBaixoHitbox = new Rectangle(posicaoMovimentoCanoHorizontal,alturaDispositivo / 2 - alturaDispositivo / 2 - espacoEntreCanos / 2 + alturaEntreCanosRandomica, canoBaixo.getWidth(), alturaDispositivo/2);
		canoTopoHibox = new Rectangle(posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica, canoTopo.getWidth(), alturaDispositivo/2);

		/*//desenhar formas
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
		shape.rect(canoBaixoHitbox.x, canoBaixoHitbox.y, canoBaixoHitbox.width, canoBaixoHitbox.height);
		shape.rect(canoTopoHibox.x, canoTopoHibox.y, canoTopoHibox.width, canoTopoHibox.height);
		shape.setColor(Color.RED);
		shape.end();*/

		//teste colisao
		if (Intersector.overlaps(passaroCirculo, canoBaixoHitbox) || Intersector.overlaps(passaroCirculo, canoTopoHibox) || posicaoInicialVertical<=0 || posicaoInicialVertical>=alturaDispositivo){
			estadoJogo = 2;
		}

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
