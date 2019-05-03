package br.edu.ifpb.pweb2.springdemo.services;

import br.edu.ifpb.pweb2.springdemo.client.ConteudoControllerClient;
import br.edu.ifpb.pweb2.springdemo.domain.Conteudo;
import br.edu.ifpb.pweb2.springdemo.domain.Postagem;
import br.edu.ifpb.pweb2.springdemo.repositories.PostagemRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PostagemService {

    private final PostagemRepository postagemRepository;

    private final ConteudoControllerClient conteudoControllerClient;

    public PostagemService(PostagemRepository postagemRepository, ConteudoControllerClient conteudoControllerClient) {
        this.postagemRepository = postagemRepository;
        this.conteudoControllerClient = conteudoControllerClient;
    }

    public Postagem cadastrarPostagem(Postagem postagem) throws PostagemException {

        for (Long conteudoId : postagem.getConteudos()) {
            try {
                ResponseEntity<Conteudo> resposta = conteudoControllerClient.recuperarConteudo(conteudoId);
                log.info(String.format("Conteúdo %s verificado e existe", resposta.getBody().getTitulo()));
            } catch (FeignException ex) {
                ex.printStackTrace();
                throw new PostagemException("Foi passado um conteúdo não existente");
            }
        }
        
        return postagemRepository.save(postagem);
    }

    public List<Postagem> listarPostagens() {
        return postagemRepository.findAll();
    }

}