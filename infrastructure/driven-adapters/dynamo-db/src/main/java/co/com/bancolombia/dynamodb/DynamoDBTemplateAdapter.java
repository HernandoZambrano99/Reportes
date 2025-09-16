package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.reporte.Reporte;
import co.com.bancolombia.model.reporte.gateways.ReporteRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedResponse;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Repository
public class DynamoDBTemplateAdapter extends TemplateAdapterOperations<Reporte, String, ReporteEntity>
implements ReporteRepository {
    private final DynamoDbAsyncClient dynamoDbClient;

    public DynamoDBTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory,
                                   ObjectMapper mapper,
                                   DynamoDbAsyncClient dynamoDbClient) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, Reporte.class), "reportes");
        this.dynamoDbClient = dynamoDbClient;
    }

    public Mono<List<Reporte>> getEntityBySomeKeys(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return query(queryExpression);
    }

    public Mono<List<Reporte>> getEntityBySomeKeysByIndex(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return queryByIndex(queryExpression);
    }

    private QueryEnhancedRequest generateQueryExpression(String partitionKey, String sortKey) {
        return QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(partitionKey).build()))
                .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(Key.builder().sortValue(sortKey).build()))
                .build();
    }

    @Override
    public Mono<Reporte> getReporte(String id) {
        Key key = Key.builder().partitionValue(id).build();
        return Mono.fromFuture(table.getItem(key)) // table es protected ahora
                .map(entity -> {
                    if (entity == null)
                        return new Reporte(id, 0L, BigDecimal.ZERO);
                    BigDecimal amount = entity.getTotalAmount() == null
                            ? BigDecimal.ZERO
                            : new BigDecimal(entity.getTotalAmount());
                    return new Reporte(id,
                            entity.getTotalCount() == null ? 0L : entity.getTotalCount(),
                            amount);
                })
                .onErrorResume(e -> Mono.just(new Reporte(id, 0L, BigDecimal.ZERO)));
    }

    @Override
    public Mono<Void> increment(String id, long countDelta, BigDecimal amountDelta) {
        Map<String, AttributeValue> exprValues = new HashMap<>();
        exprValues.put(":incCount", AttributeValue.builder().n(Long.toString(countDelta)).build());
        exprValues.put(":zeroCount", AttributeValue.builder().n("0").build());
        exprValues.put(":incAmount", AttributeValue.builder().n(amountDelta.toPlainString()).build());
        exprValues.put(":zeroAmount", AttributeValue.builder().n("0").build());

        String updateExpr = "SET totalCount = if_not_exists(totalCount, :zeroCount) + :incCount, "
                + "totalAmount = if_not_exists(totalAmount, :zeroAmount) + :incAmount";

        return Mono.fromFuture(
                dynamoDbClient.updateItem(u -> u
                        .tableName("reportes")
                        .key(Map.of("id", AttributeValue.builder().s(id).build()))
                        .updateExpression(updateExpr)
                        .expressionAttributeValues(exprValues))
        ).then();
    }

    @Override
    public Mono<Reporte> save(Reporte reporte) {
        ReporteEntity entity = mapper.map(reporte, ReporteEntity.class);
        return Mono.fromFuture(table.putItem(entity)).thenReturn(reporte);
    }

}
