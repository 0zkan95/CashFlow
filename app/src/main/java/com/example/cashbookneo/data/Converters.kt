package com.example.cashbookneo.data

import androidx.room.TypeConverter
import com.example.cashbookneo.data.entity.AssetType
import com.example.cashbookneo.data.entity.CategoryType
import com.example.cashbookneo.data.entity.MemoType

class Converters {
    @TypeConverter
    fun fromCategoryType(value: CategoryType): String = value.name

    @TypeConverter
    fun toCategoryType(value: String): CategoryType = try {
        CategoryType.valueOf(value)
    } catch (e: Exception) {
        CategoryType.EXPENSE
    }

    @TypeConverter
    fun fromAssetType(value: AssetType?): String? = value?.name

    @TypeConverter
    fun toAssetType(value: String?): AssetType? = value?.let {
        try { AssetType.valueOf(it) } catch (e: Exception) { null }
    }

    @TypeConverter
    fun fromMemoType(value: MemoType?): String? = value?.name

    @TypeConverter
    fun toMemoType(value: String?): MemoType? = value?.let {
        try { MemoType.valueOf(it) } catch (e: Exception) { null }
    }
}
