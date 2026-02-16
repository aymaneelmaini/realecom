package com.aymanegeek.imedia.common.config

import com.aymanegeek.imedia.inventory.domain.InventoryId
import com.aymanegeek.imedia.order.domain.OrderId
import com.aymanegeek.imedia.order.domain.OrderLineId
import com.aymanegeek.imedia.common.vo.ProductId
import com.aymanegeek.imedia.user.domain.UserId
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import java.util.*

@Configuration
@EnableJdbcRepositories(basePackages = ["com.aymanegeek.imedia"])
class JdbcConfig : AbstractJdbcConfiguration() {

    override fun userConverters(): List<*> {
        return listOf(
            UuidToUserIdConverter(),
            UserIdToUuidConverter(),
            UuidToProductIdConverter(),
            ProductIdToUuidConverter(),
            UuidToInventoryIdConverter(),
            InventoryIdToUuidConverter(),
            UuidToOrderIdConverter(),
            OrderIdToUuidConverter(),
            UuidToOrderLineIdConverter(),
            OrderLineIdToUuidConverter()
        )
    }
}

@ReadingConverter
class UuidToUserIdConverter : Converter<UUID, UserId> {
    override fun convert(source: UUID): UserId = UserId(source)
}

@WritingConverter
class UserIdToUuidConverter : Converter<UserId, UUID> {
    override fun convert(source: UserId): UUID = source.value
}

@ReadingConverter
class UuidToProductIdConverter : Converter<UUID, ProductId> {
    override fun convert(source: UUID): ProductId = ProductId(source)
}

@WritingConverter
class ProductIdToUuidConverter : Converter<ProductId, UUID> {
    override fun convert(source: ProductId): UUID = source.value
}

@ReadingConverter
class UuidToInventoryIdConverter : Converter<UUID, InventoryId> {
    override fun convert(source: UUID): InventoryId = InventoryId(source)
}

@WritingConverter
class InventoryIdToUuidConverter : Converter<InventoryId, UUID> {
    override fun convert(source: InventoryId): UUID = source.value
}

@ReadingConverter
class UuidToOrderIdConverter : Converter<UUID, OrderId> {
    override fun convert(source: UUID): OrderId = OrderId(source)
}

@WritingConverter
class OrderIdToUuidConverter : Converter<OrderId, UUID> {
    override fun convert(source: OrderId): UUID = source.value
}

@ReadingConverter
class UuidToOrderLineIdConverter : Converter<UUID, OrderLineId> {
    override fun convert(source: UUID): OrderLineId = OrderLineId(source)
}

@WritingConverter
class OrderLineIdToUuidConverter : Converter<OrderLineId, UUID> {
    override fun convert(source: OrderLineId): UUID = source.value
}
