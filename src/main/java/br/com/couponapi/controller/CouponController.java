package br.com.couponapi.controller;

import br.com.couponapi.dtos.CouponCreateRequest;
import br.com.couponapi.dtos.CouponResponse;
import br.com.couponapi.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Log4j2
@Tag(
        name = "Cupons",
        description = "API responsável pelo gerenciamento de cupons de desconto"
)
public class CouponController {

    private final CouponService service;

    @Operation(
            summary = "Criar um novo cupom",
            description = "Cria um novo cupom de desconto."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Cupom criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CouponResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos para criação do cupom",
                    content = @Content
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CouponResponse create(
            @RequestBody
            @Parameter(description = "Dados necessários para criação do cupom", required = true)
            CouponCreateRequest request
    ) {
        log.info("Criando cupom com código: {}", request.code());
        return service.create(request);
    }

    @Operation(
            summary = "Buscar cupom por ID",
            description = "Retorna os dados de um cupom específico a partir do seu identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cupom encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CouponResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cupom não encontrado",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public CouponResponse getById(
            @PathVariable
            @Parameter(description = "ID do cupom", required = true)
            UUID id
    ) {
        return service.getById(id);
    }


    @Operation(
            summary = "Listar todos os cupons",
            description = "Retorna todos os cupons que não foram excluídos (soft delete)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de cupons retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CouponResponse.class)
                    )
            )
    })
    @GetMapping
    public List<CouponResponse> getAll() {
        log.info("Recuperando todos os cupons");
        return service.getAll();
    }

    @Operation(
            summary = "Consumir um cupom",
            description = """
                    Marca um cupom como consumido.
                    
                    Regras:
                    - O cupom deve estar publicado
                    - O cupom não pode estar expirado
                    - O cupom não pode já ter sido consumido
                    - O cupom não pode estar excluído
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cupom consumido com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CouponResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cupom inválido para consumo",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cupom não encontrado",
                    content = @Content
            )
    })
    @PatchMapping("/{id}/consume")
    public CouponResponse consume(
            @PathVariable
            @Parameter(description = "ID do cupom a ser consumido", required = true)
            UUID id
    ) {
        log.info("Consumindo cupom com id: {}", id);
        return service.consume(id);
    }

    @Operation(
            summary = "Excluir um cupom",
            description = """
                    Realiza a exclusão lógica (soft delete) de um cupom.
                    
                    Observações:
                    - Um cupom excluído não pode ser consumido
                    - Os dados permanecem armazenados no banco
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Cupom excluído com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cupom não encontrado",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable
            @Parameter(description = "ID do cupom a ser excluído", required = true)
            UUID id
    ) {
        log.info("Excluindo cupom com id: {}", id);
        service.delete(id);
    }
}
